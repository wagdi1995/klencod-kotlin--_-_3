package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Account
import com.example.data.model.AccountDetailsSummary
import com.example.data.model.Category
import com.example.data.model.Currency
import com.example.data.model.DashboardOverview
import com.example.data.model.ReportResult
import com.example.data.model.ReportType
import com.example.data.model.SecuritySettings
import com.example.data.model.TransactionItem
import com.example.data.model.Voucher
import com.example.data.model.VoucherType
import com.example.data.repository.AccountingRepository
import com.example.ui.components.NavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.net.Uri
import java.io.File

sealed class AppScreen {
  object Home : AppScreen()
  object Accounts : AppScreen()
  data class AccountDetails(val accountNo: Long) : AppScreen()
  object Transfer : AppScreen()
  data class Vouchers(val initialType: VoucherType = VoucherType.RECEIPT) : AppScreen()
  object Reports : AppScreen()
  object Categories : AppScreen()
  object Currencies : AppScreen()
  object Settings : AppScreen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = AccountingRepository.getInstance(application)

  // Navigation State
  private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _navigationStack = mutableListOf<AppScreen>(AppScreen.Home)

  private val _currentNavTab = MutableStateFlow(NavDestination.HOME)
  val currentNavTab: StateFlow<NavDestination> = _currentNavTab.asStateFlow()

  // App Lock State
  private val _securitySettings = MutableStateFlow(repository.getSecuritySettings())
  val securitySettings: StateFlow<SecuritySettings> = _securitySettings.asStateFlow()

  private val _isAppLocked = MutableStateFlow(repository.getSecuritySettings().isPinEnabled)
  val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

  // Dashboard Data
  private val _dashboardOverview = MutableStateFlow<DashboardOverview?>(null)
  val dashboardOverview: StateFlow<DashboardOverview?> = _dashboardOverview.asStateFlow()

  // Accounts Data
  private val _accounts = MutableStateFlow<List<Account>>(emptyList())
  val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

  private val _selectedCategoryNo = MutableStateFlow<Long?>(null)
  val selectedCategoryNo: StateFlow<Long?> = _selectedCategoryNo.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _sortOrder = MutableStateFlow("NAME_ASC")
  val sortOrder: StateFlow<String> = _sortOrder.asStateFlow()

  // Categories & Currencies
  private val _categories = MutableStateFlow<List<Category>>(emptyList())
  val categories: StateFlow<List<Category>> = _categories.asStateFlow()

  private val _currencies = MutableStateFlow<List<Currency>>(emptyList())
  val currencies: StateFlow<List<Currency>> = _currencies.asStateFlow()

  // Account Details Screen State
  private val _accountDetailsSummary = MutableStateFlow<AccountDetailsSummary?>(null)
  val accountDetailsSummary: StateFlow<AccountDetailsSummary?> = _accountDetailsSummary.asStateFlow()

  private val _accountTransactions = MutableStateFlow<List<TransactionItem>>(emptyList())
  val accountTransactions: StateFlow<List<TransactionItem>> = _accountTransactions.asStateFlow()

  private val _selectedDetailsCurrencyId = MutableStateFlow<Long?>(null)
  val selectedDetailsCurrencyId: StateFlow<Long?> = _selectedDetailsCurrencyId.asStateFlow()

  private val _selectedDetailsFilterType = MutableStateFlow("ALL")
  val selectedDetailsFilterType: StateFlow<String> = _selectedDetailsFilterType.asStateFlow()

  // Reports Screen State
  private val _currentReportResult = MutableStateFlow<ReportResult?>(null)
  val currentReportResult: StateFlow<ReportResult?> = _currentReportResult.asStateFlow()

  // Backup & Restore Points
  private val _restorePoints = MutableStateFlow<List<File>>(emptyList())
  val restorePoints: StateFlow<List<File>> = _restorePoints.asStateFlow()

  // Notification Toast Message
  private val _notificationMessage = MutableStateFlow<String?>(null)
  val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

  // Contextual Bottom Sheet Action State
  private val _selectedAccountForAction = MutableStateFlow<Account?>(null)
  val selectedAccountForAction: StateFlow<Account?> = _selectedAccountForAction.asStateFlow()

  init {
    loadAllData()

    // Observe data invalidations from repository
    viewModelScope.launch {
      repository.dataInvalidationTrigger.collect {
        loadAllData()
      }
    }
  }

  private var loadJob: Job? = null
  private var accountsJob: Job? = null

  fun loadAllData() {
    loadJob?.cancel()
    loadJob = viewModelScope.launch {
      coroutineScope {
        val overview = async { repository.getDashboardOverview() }
        val cats = async { repository.getCategories() }
        val curs = async { repository.getCurrencies() }
        val points = async { repository.getLocalRestorePoints() }
        val accs = async {
          repository.getAccounts(
            searchQuery = _searchQuery.value,
            categoryId = _selectedCategoryNo.value,
            sortOrder = _sortOrder.value
          )
        }
        _dashboardOverview.value = overview.await()
        _categories.value = cats.await()
        _currencies.value = curs.await()
        _restorePoints.value = points.await()
        _accounts.value = accs.await()
      }
      _securitySettings.value = repository.getSecuritySettings()

      val screen = _currentScreen.value
      if (screen is AppScreen.AccountDetails) {
        loadAccountDetails(screen.accountNo)
      }
    }
  }

  fun unlockApp() {
    _isAppLocked.value = false
  }

  // --- NAVIGATION ---

  fun navigateTo(screen: AppScreen) {
    if (_currentScreen.value != screen) {
      _navigationStack.add(screen)
      _currentScreen.value = screen
    }

    when (screen) {
      is AppScreen.Home -> _currentNavTab.value = NavDestination.HOME
      is AppScreen.Accounts -> _currentNavTab.value = NavDestination.ACCOUNTS
      is AppScreen.Reports -> _currentNavTab.value = NavDestination.REPORTS
      is AppScreen.Settings -> _currentNavTab.value = NavDestination.MORE
      else -> {}
    }

    if (screen is AppScreen.AccountDetails) {
      loadAccountDetails(screen.accountNo)
    }
  }

  fun navigateBack(): Boolean {
    if (_navigationStack.size > 1) {
      _navigationStack.removeAt(_navigationStack.size - 1)
      val prev = _navigationStack.last()
      _currentScreen.value = prev

      when (prev) {
        is AppScreen.Home -> _currentNavTab.value = NavDestination.HOME
        is AppScreen.Accounts -> _currentNavTab.value = NavDestination.ACCOUNTS
        is AppScreen.Reports -> _currentNavTab.value = NavDestination.REPORTS
        is AppScreen.Settings -> _currentNavTab.value = NavDestination.MORE
        else -> {}
      }

      if (prev is AppScreen.AccountDetails) {
        loadAccountDetails(prev.accountNo)
      }
      return true
    }
    return false
  }

  fun selectNavTab(tab: NavDestination) {
    _currentNavTab.value = tab
    when (tab) {
      NavDestination.HOME -> navigateTo(AppScreen.Home)
      NavDestination.ACCOUNTS -> navigateTo(AppScreen.Accounts)
      NavDestination.REPORTS -> {
        navigateTo(AppScreen.Reports)
        generateReport(ReportType.ACCOUNTS_BALANCE_SUMMARY)
      }
      NavDestination.MORE -> navigateTo(AppScreen.Settings)
    }
  }

  // --- ACCOUNTS LOGIC ---

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
    loadAccounts(debounceMs = 150)
  }

  fun selectCategoryFilter(categoryNo: Long?) {
    _selectedCategoryNo.value = categoryNo
    loadAccounts()
  }

  fun toggleSortOrder() {
    _sortOrder.value = when (_sortOrder.value) {
      "NAME_ASC" -> "NAME_DESC"
      "NAME_DESC" -> "BAL_DESC"
      "BAL_DESC" -> "BAL_ASC"
      else -> "NAME_ASC"
    }
    loadAccounts()
  }

  private fun loadAccounts(debounceMs: Long = 0) {
    accountsJob?.cancel()
    accountsJob = viewModelScope.launch {
      if (debounceMs > 0) delay(debounceMs)
      _accounts.value = repository.getAccounts(
        searchQuery = _searchQuery.value,
        categoryId = _selectedCategoryNo.value,
        sortOrder = _sortOrder.value
      )
    }
  }

  fun addAccount(
    name: String,
    ctgryNo: Long,
    mobile: String,
    openingBalance: Double,
    creditLimit: Double,
    debitLimit: Double,
    reminder: String?,
    currencyId: Long = 1L
  ) {
    viewModelScope.launch {
      val accNo = repository.addAccount(
        accountName = name,
        ctgryNo = ctgryNo,
        mobile = mobile,
        openingBalance = openingBalance,
        currencyId = currencyId,
        creditLimit = creditLimit,
        debitLimit = debitLimit,
        reminderDate = reminder
      )
      _notificationMessage.value = "تمت إضافة الحساب بنجاح"
      loadAllData()
    }
  }

  fun updateAccount(
    accountNo: Long,
    name: String,
    ctgryNo: Long,
    mobile: String,
    creditLimit: Double,
    debitLimit: Double,
    reminder: String?
  ) {
    viewModelScope.launch {
      repository.updateAccount(accountNo, name, ctgryNo, mobile, reminder, creditLimit, debitLimit)
      _notificationMessage.value = "تم تحديث بيانات الحساب"
      loadAllData()
    }
  }

  fun deleteAccount(accountNo: Long) {
    viewModelScope.launch {
      repository.deleteAccount(accountNo)
      _notificationMessage.value = "تم حذف الحساب وجميع عملياته"
      loadAllData()
    }
  }

  fun moveAccount(accountNo: Long, targetCtgryNo: Long) {
    viewModelScope.launch {
      repository.moveAccountToCategory(accountNo, targetCtgryNo)
      _notificationMessage.value = "تم نقل الحساب إلى التصنيف الجديد"
      loadAllData()
    }
  }

  fun copyAccount(sourceAccountNo: Long, newName: String) {
    viewModelScope.launch {
      repository.copyAccount(sourceAccountNo, newName)
      _notificationMessage.value = "تم نسخ الحساب بنجاح"
      loadAllData()
    }
  }

  fun setSelectedAccountForAction(account: Account?) {
    _selectedAccountForAction.value = account
  }

  // --- ACCOUNT DETAILS LOGIC ---

  fun loadAccountDetails(accountNo: Long) {
    viewModelScope.launch {
      _accountDetailsSummary.value = repository.getAccountDetailsSummary(accountNo)
      _accountTransactions.value = repository.getAccountTransactions(
        accountNo = accountNo,
        currencyId = _selectedDetailsCurrencyId.value,
        filterType = _selectedDetailsFilterType.value
      )
    }
  }

  fun setDetailsCurrencyFilter(currencyId: Long?) {
    _selectedDetailsCurrencyId.value = currencyId
    val screen = _currentScreen.value
    if (screen is AppScreen.AccountDetails) {
      loadAccountDetails(screen.accountNo)
    }
  }

  fun setDetailsTypeFilter(type: String) {
    _selectedDetailsFilterType.value = type
    val screen = _currentScreen.value
    if (screen is AppScreen.AccountDetails) {
      loadAccountDetails(screen.accountNo)
    }
  }

  // --- TRANSACTIONS & TRANSFERS ---

  fun addTransaction(
    accountNo: Long,
    amount: Double,
    date: String,
    details: String,
    currencyId: Long
  ) {
    viewModelScope.launch {
      repository.addTransaction(accountNo, amount, date, details, currencyId)
      _notificationMessage.value = "تم تسجيل الحركة المالية بنجاح"
      loadAllData()
    }
  }

  fun deleteTransaction(transId: Long) {
    viewModelScope.launch {
      repository.deleteTransaction(transId)
      _notificationMessage.value = "تم حذف الحركة وتحديث الرصيد"
      loadAllData()
    }
  }

  fun executeTransfer(
    sourceNo: Long,
    destNo: Long,
    amount: Double,
    currencyId: Long,
    date: String,
    details: String
  ) {
    viewModelScope.launch {
      val success = repository.executeTransfer(sourceNo, destNo, amount, currencyId, date, details)
      if (success) {
        _notificationMessage.value = "تم التحويل بنجاح وتحديث الحسابين"
        loadAllData()
      } else {
        _notificationMessage.value = "فشل إجراء التحويل"
      }
    }
  }

  fun createVoucher(
    type: VoucherType,
    accountNo: Long,
    amount: Double,
    currencyId: Long,
    date: String,
    details: String,
    onDone: (Voucher) -> Unit
  ) {
    viewModelScope.launch {
      val v = repository.createVoucher(type, accountNo, amount, currencyId, date, details)
      _notificationMessage.value = "تم إصدار وتوثيق ${type.titleAr} بنجاح"
      onDone(v)
      loadAllData()
    }
  }

  // --- CATEGORIES & CURRENCIES ---

  fun addCategory(name: String) {
    viewModelScope.launch {
      repository.addCategory(name)
      _notificationMessage.value = "تمت إضافة التصنيف"
      loadAllData()
    }
  }

  fun updateCategory(ctgryNo: Long, name: String, isEnabled: Boolean) {
    viewModelScope.launch {
      repository.updateCategory(ctgryNo, name, isEnabled)
      _notificationMessage.value = "تم تعديل التصنيف"
      loadAllData()
    }
  }

  fun deleteCategory(ctgryNo: Long) {
    viewModelScope.launch {
      val success = repository.deleteCategory(ctgryNo)
      if (success) {
        _notificationMessage.value = "تم حذف التصنيف"
        loadAllData()
      } else {
        _notificationMessage.value = "لا يمكن حذف التصنيف الرئيسي"
      }
    }
  }

  fun addCurrency(name: String) {
    viewModelScope.launch {
      repository.addCurrency(name)
      _notificationMessage.value = "تمت إضافة العملة"
      loadAllData()
    }
  }

  fun updateCurrency(cId: Long, name: String) {
    viewModelScope.launch {
      repository.updateCurrency(cId, name)
      _notificationMessage.value = "تم تعديل العملة"
      loadAllData()
    }
  }

  fun deleteCurrency(cId: Long) {
    viewModelScope.launch {
      val success = repository.deleteCurrency(cId)
      if (success) {
        _notificationMessage.value = "تم حذف العملة"
        loadAllData()
      } else {
        _notificationMessage.value = "لا يمكن حذف العملة الأساسية"
      }
    }
  }

  // --- REPORTS ---

  fun generateReport(
    type: ReportType,
    from: String? = null,
    to: String? = null,
    accNo: Long? = null,
    catNo: Long? = null,
    curId: Long? = null
  ) {
    viewModelScope.launch {
      _currentReportResult.value = repository.generateReport(type, from, to, accNo, catNo, curId)
    }
  }

  fun exportReportCsv(report: ReportResult): File? {
    return repository.exportReportToCsv(report)
  }

  // --- BACKUP & SECURITY ---

  fun createLocalBackup() {
    viewModelScope.launch {
      val file = repository.createLocalBackup()
      if (file != null) {
        _notificationMessage.value = "تم إنشاء نسخة احتياطية: ${file.name}"
        _restorePoints.value = repository.getLocalRestorePoints()
      }
    }
  }

  fun restoreBackup(file: File) {
    viewModelScope.launch {
      val success = repository.restoreDatabaseFromFile(file)
      if (success) {
        _notificationMessage.value = "تمت استعادة البيانات بنجاح"
        loadAllData()
      }
    }
  }

  fun deleteBackup(file: File) {
    viewModelScope.launch {
      file.delete()
      _restorePoints.value = repository.getLocalRestorePoints()
      _notificationMessage.value = "تم حذف نقطة الاستعادة"
    }
  }

  // --- BACKUP TO / RESTORE FROM A USER-PICKED LOCATION (SAF) ---

  fun createBackupToUri(uri: Uri) {
    viewModelScope.launch {
      val success = repository.createBackupToUri(uri)
      if (success) {
        _notificationMessage.value = "تم حفظ النسخة الاحتياطية في الموقع الذي اخترته"
        _restorePoints.value = repository.getLocalRestorePoints()
      } else {
        _notificationMessage.value = "فشل حفظ النسخة الاحتياطية في الموقع المحدد"
      }
    }
  }

  fun restoreFromUri(uri: Uri) {
    viewModelScope.launch {
      val success = repository.restoreFromUri(uri)
      if (success) {
        _notificationMessage.value = "تمت استعادة البيانات من الملف المحدد بنجاح"
        loadAllData()
        _restorePoints.value = repository.getLocalRestorePoints()
      } else {
        _notificationMessage.value = "فشل استعادة البيانات من الملف المحدد"
      }
    }
  }

  fun performAnnualClosing(cutoffDate: String) {
    viewModelScope.launch {
      repository.performAnnualClosing(cutoffDate)
      _notificationMessage.value = "تم تنفيذ الإقفال السنوي وترحيل الأرصدة بنجاح"
      loadAllData()
    }
  }

  fun updateSecuritySettings(settings: SecuritySettings) {
    repository.updateSecuritySettings(settings)
    _securitySettings.value = settings
    _isAppLocked.value = false
    _notificationMessage.value = "تم تحديث إعدادات الأمان بنجاح"
  }

  fun clearNotification() {
    _notificationMessage.value = null
  }
}
