package com.example

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.fragment.app.FragmentActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import com.example.ui.BiometricHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.CreateDocumentParams
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Account
import com.example.data.model.TransactionItem
import com.example.data.model.ReportType
import com.example.data.model.VoucherType
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AccountActionBottomSheet
import com.example.ui.components.AddEditAccountDialog
import com.example.ui.components.AddEditTransactionDialog
import com.example.ui.components.FloatingCapsuleNavigation
import com.example.ui.screens.AccountDetailsScreen
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CurrenciesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PinLockScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TransferScreen
import com.example.ui.screens.VouchersScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppContent()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: MainViewModel = viewModel()) {
  val context = LocalContext.current
  val currentScreen by viewModel.currentScreen.collectAsState()
  val currentNavTab by viewModel.currentNavTab.collectAsState()
  val isAppLocked by viewModel.isAppLocked.collectAsState()
  val securitySettings by viewModel.securitySettings.collectAsState()

  val dashboardOverview by viewModel.dashboardOverview.collectAsState()
  val accounts by viewModel.accounts.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val currencies by viewModel.currencies.collectAsState()
  val selectedCategoryNo by viewModel.selectedCategoryNo.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()

  val accountSummary by viewModel.accountDetailsSummary.collectAsState()
  val accountTransactions by viewModel.accountTransactions.collectAsState()
  val selectedDetailsCurrencyId by viewModel.selectedDetailsCurrencyId.collectAsState()
  val selectedDetailsFilterType by viewModel.selectedDetailsFilterType.collectAsState()

  val currentReportResult by viewModel.currentReportResult.collectAsState()
  val restorePoints by viewModel.restorePoints.collectAsState()
  val notificationMessage by viewModel.notificationMessage.collectAsState()

  val selectedAccountForAction by viewModel.selectedAccountForAction.collectAsState()

  // Dialog States
  var showAddAccountDialog by remember { mutableStateOf(false) }
  var accountToEdit by remember { mutableStateOf<Account?>(null) }
  var showAddTransactionDialog by remember { mutableStateOf(false) }
  var targetAccountNoForTx by remember { mutableStateOf<Long?>(null) }
  var showMoveAccountDialog by remember { mutableStateOf(false) }
  var showCopyAccountDialog by remember { mutableStateOf(false) }
  var accountToDelete by remember { mutableStateOf<Account?>(null) }
  var transactionToDelete by remember { mutableStateOf<TransactionItem?>(null) }

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  // SAF:让用户选择保存备份的位置（如“下载”文件夹）
  val saveBackupLauncher = rememberLauncherForActivityResult(
    CreateDocument()
  ) { uri: Uri? ->
    uri?.let { viewModel.createBackupToUri(it) }
  }

  // SAF:让用户选择要还原的备份文件
  val pickRestoreLauncher = rememberLauncherForActivityResult(
    OpenDocument()
  ) { uri: Uri? ->
    uri?.let { viewModel.restoreFromUri(it) }
  }

  // Handle toast / notification message
  LaunchedEffect(notificationMessage) {
    notificationMessage?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      viewModel.clearNotification()
    }
  }

  // BackHandler to handle sub-screen back navigation smoothly
  BackHandler(enabled = currentScreen != AppScreen.Home) {
    if (!viewModel.navigateBack()) {
      (context as? ComponentActivity)?.finish()
    }
  }

  // App Lock Screen Gate
  if (isAppLocked && securitySettings.isPinEnabled && securitySettings.pinCode.isNotBlank()) {
    PinLockScreen(
      expectedPin = securitySettings.pinCode,
      isFingerprintEnabled = securitySettings.isFingerprintEnabled,
      onUnlockSuccess = { viewModel.unlockApp() },
      onFingerprintClick = {
        (context as? FragmentActivity)?.let { act ->
          BiometricHelper.authenticate(
            activity = act,
            onSuccess = { viewModel.unlockApp() },
            onFailure = { cancelled, msg ->
              if (!cancelled && msg.isNotBlank()) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
              }
            }
          )
        }
      }
    )
    return
  }

  // Whether floating bottom capsule should be visible
  val showFloatingNav = when (currentScreen) {
    is AppScreen.Home, is AppScreen.Accounts, is AppScreen.Reports, is AppScreen.Settings -> true
    else -> false
  }

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        Crossfade(
          targetState = currentScreen,
          animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
          label = "screen_transition"
        ) { screen ->
          when (screen) {
            is AppScreen.Home -> {
              dashboardOverview?.let { overview ->
                HomeScreen(
                  overview = overview,
                  onQuickAction = { actionId ->
                    when (actionId) {
                      "add_account" -> showAddAccountDialog = true
                      "add_transaction" -> {
                        targetAccountNoForTx = null
                        showAddTransactionDialog = true
                      }
                      "transfer" -> viewModel.navigateTo(AppScreen.Transfer)
                      "receipt_voucher" -> viewModel.navigateTo(AppScreen.Vouchers(VoucherType.RECEIPT))
                      "payment_voucher" -> viewModel.navigateTo(AppScreen.Vouchers(VoucherType.PAYMENT))
                      "reports" -> {
                        viewModel.navigateTo(AppScreen.Reports)
                        viewModel.generateReport(ReportType.ACCOUNTS_BALANCE_SUMMARY)
                      }
                    }
                  },
                  onViewAllTransactions = {
                    viewModel.navigateTo(AppScreen.Reports)
                    viewModel.generateReport(ReportType.DETAILED_MOVEMENTS)
                  },
                  onTransactionClick = { /* detail */ },
                  onNotificationClick = {
                    Toast.makeText(context, "لا توجد تنبيهات جديدة معلقة", Toast.LENGTH_SHORT).show()
                  },
                  onAccountClick = { accNo ->
                    viewModel.navigateTo(AppScreen.AccountDetails(accNo))
                  }
                )
              }
            }

            is AppScreen.Accounts -> {
              AccountsScreen(
                accounts = accounts,
                categories = categories,
                selectedCategoryNo = selectedCategoryNo,
                searchQuery = searchQuery,
                onSearchChange = { viewModel.setSearchQuery(it) },
                onCategorySelect = { viewModel.selectCategoryFilter(it) },
                onAccountClick = { accNo ->
                  viewModel.navigateTo(AppScreen.AccountDetails(accNo))
                },
                onAccountLongClick = { acc ->
                  viewModel.setSelectedAccountForAction(acc)
                },
                onAddAccountClick = {
                  accountToEdit = null
                  showAddAccountDialog = true
                },
                onSortClick = {
                  viewModel.toggleSortOrder()
                  Toast.makeText(context, "تم تغيير ترتيب الحسابات", Toast.LENGTH_SHORT).show()
                },
                onNotificationClick = {
                  Toast.makeText(context, "جميع الإشعارات مفعلة ومحدثة", Toast.LENGTH_SHORT).show()
                }
              )
            }

            is AppScreen.AccountDetails -> {
              accountSummary?.let { summary ->
                AccountDetailsScreen(
                  summary = summary,
                  transactions = accountTransactions,
                  currencies = currencies,
                  selectedCurrencyId = selectedDetailsCurrencyId,
                  selectedFilterType = selectedDetailsFilterType,
                  onCurrencySelect = { viewModel.setDetailsCurrencyFilter(it) },
                  onFilterTypeSelect = { viewModel.setDetailsTypeFilter(it) },
                  onBackClick = { viewModel.navigateBack() },
                  onAddTransactionClick = {
                    targetAccountNoForTx = screen.accountNo
                    showAddTransactionDialog = true
                  },
                  onTransactionClick = { tx ->
                    // Open confirmation to delete
                    transactionToDelete = tx
                  }
                )
              }
            }

            is AppScreen.Transfer -> {
              TransferScreen(
                accounts = accounts,
                currencies = currencies,
                onBackClick = { viewModel.navigateBack() },
                onExecuteTransfer = { src, dst, amt, cur, dt, det ->
                  viewModel.executeTransfer(src, dst, amt, cur, dt, det)
                }
              )
            }

            is AppScreen.Vouchers -> {
              VouchersScreen(
                accounts = accounts,
                currencies = currencies,
                initialType = screen.initialType,
                onBackClick = { viewModel.navigateBack() },
                onCreateVoucher = { type, accNo, amt, cur, dt, det, done ->
                  viewModel.createVoucher(type, accNo, amt, cur, dt, det, done)
                }
              )
            }

            is AppScreen.Reports -> {
              ReportsScreen(
                accounts = accounts,
                categories = categories,
                currencies = currencies,
                reportResult = currentReportResult,
                onGenerateReport = { type, from, to, acc, cat, cur ->
                  viewModel.generateReport(type, from, to, acc, cat, cur)
                },
                onExportCsv = { report ->
                  viewModel.exportReportCsv(report)
                },
                onBackClick = { viewModel.navigateBack() }
              )
            }

            is AppScreen.Categories -> {
              CategoriesScreen(
                categories = categories,
                onBackClick = { viewModel.navigateBack() },
                onAddCategory = { viewModel.addCategory(it) },
                onUpdateCategory = { id, name, enabled -> viewModel.updateCategory(id, name, enabled) },
                onDeleteCategory = { viewModel.deleteCategory(it) }
              )
            }

            is AppScreen.Currencies -> {
              CurrenciesScreen(
                currencies = currencies,
                onBackClick = { viewModel.navigateBack() },
                onAddCurrency = { viewModel.addCurrency(it) },
                onUpdateCurrency = { id, name -> viewModel.updateCurrency(id, name) },
                onDeleteCurrency = { viewModel.deleteCurrency(it) }
              )
            }

            is AppScreen.Settings -> {
              SettingsScreen(
                securitySettings = securitySettings,
                restorePoints = restorePoints,
                onUpdateSecurity = { viewModel.updateSecuritySettings(it) },
                onNavigateToAccounts = { viewModel.navigateTo(AppScreen.Accounts) },
                onNavigateToCategories = { viewModel.navigateTo(AppScreen.Categories) },
                onNavigateToCurrencies = { viewModel.navigateTo(AppScreen.Currencies) },
                onCreateLocalBackup = { viewModel.createLocalBackup() },
                onChooseBackupLocation = {
                  val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.ENGLISH).format(Date())
                  saveBackupLauncher.launch(
                    ActivityResultContracts.CreateDocumentParams(
                      "application/octet-stream",
                      "myaccounts_backup_$timeStamp.db"
                    )
                  )
                },
                onRestoreBackup = { viewModel.restoreBackup(it) },
                onChooseRestoreFile = {
                  pickRestoreLauncher.launch(arrayOf("*/*"))
                },
                onDeleteBackup = { viewModel.deleteBackup(it) },
                onAnnualClosing = { viewModel.performAnnualClosing(it) },
                onBackClick = { viewModel.navigateBack() }
              )
            }
          }
        }

        // Floating Capsule Bottom Navigation
        if (showFloatingNav) {
          FloatingCapsuleNavigation(
            currentDestination = currentNavTab,
            onDestinationSelected = { dest -> viewModel.selectNavTab(dest) },
            modifier = Modifier.align(Alignment.BottomCenter)
          )
        }

        // Add / Edit Account Dialog
        if (showAddAccountDialog) {
          AddEditAccountDialog(
            categories = categories,
            currencies = currencies,
            existingAccount = accountToEdit,
            onDismiss = {
              showAddAccountDialog = false
              accountToEdit = null
            },
            onConfirm = { name, catNo, mobile, openBal, credLim, debLim, reminder, curId ->
              if (accountToEdit == null) {
                viewModel.addAccount(name, catNo, mobile, openBal, credLim, debLim, reminder, curId)
              } else {
                viewModel.updateAccount(accountToEdit!!.accountNo, name, catNo, mobile, credLim, debLim, reminder)
              }
              showAddAccountDialog = false
              accountToEdit = null
            }
          )
        }

        // Add / Edit Transaction Dialog
        if (showAddTransactionDialog) {
          AddEditTransactionDialog(
            accounts = accounts,
            currencies = currencies,
            selectedAccountNo = targetAccountNoForTx,
            onDismiss = { showAddTransactionDialog = false },
            onConfirm = { accNo, amt, dt, det, curId ->
              viewModel.addTransaction(accNo, amt, dt, det, curId)
              showAddTransactionDialog = false
            }
          )
        }

        // Contextual Account Action Bottom Sheet
        selectedAccountForAction?.let { account ->
          AccountActionBottomSheet(
            account = account,
            onDismiss = { viewModel.setSelectedAccountForAction(null) },
            onOpenDetails = { viewModel.navigateTo(AppScreen.AccountDetails(account.accountNo)) },
            onEdit = {
              accountToEdit = account
              showAddAccountDialog = true
            },
            onDelete = { accountToDelete = account },
            onCopy = { viewModel.copyAccount(account.accountNo, "${account.accountName} (نسخة)") },
            onMove = {
              // Move to next category
              val nextCat = categories.find { it.ctgryNo != account.ctgryNo } ?: categories.first()
              viewModel.moveAccount(account.accountNo, nextCat.ctgryNo)
            },
            onTransfer = { viewModel.navigateTo(AppScreen.Transfer) },
            onAddTransaction = {
              targetAccountNoForTx = account.accountNo
              showAddTransactionDialog = true
            }
          )
        }

        // Delete Account Confirmation Dialog
        accountToDelete?.let { account ->
          AlertDialog(
            onDismissRequest = { accountToDelete = null },
            title = { Text("حذف الحساب") },
            text = { Text("هل أنت متأكد من رغبتك بحذف حساب '${account.accountName}'؟ سيتم حذف جميع عملياته.") },
            confirmButton = {
              TextButton(onClick = {
                viewModel.deleteAccount(account.accountNo)
                accountToDelete = null
                viewModel.setSelectedAccountForAction(null)
              }) {
                Text("حذف", color = androidx.compose.ui.graphics.Color.Red)
              }
            },
            dismissButton = {
              TextButton(onClick = { accountToDelete = null }) {
                Text("إلغاء")
              }
            }
          )
        }

        // Delete Transaction Confirmation Dialog
        transactionToDelete?.let { tx ->
          AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("حذف الحركة") },
            text = { Text("هل أنت متأكد من رغبتك بحذف هذه الحركة؟") },
            confirmButton = {
              TextButton(onClick = {
                viewModel.deleteTransaction(tx.transId)
                transactionToDelete = null
              }) {
                Text("حذف", color = androidx.compose.ui.graphics.Color.Red)
              }
            },
            dismissButton = {
              TextButton(onClick = { transactionToDelete = null }) {
                Text("إلغاء")
              }
            }
          )
        }
      }
    }
  }
}