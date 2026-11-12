package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Account
import com.example.data.model.Category
import com.example.data.model.Currency
import com.example.data.model.Voucher
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DividerColor
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.NegativeRedSoft
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PositiveGreenSoft
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue
import com.example.ui.theme.WarningOrange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

// --- ADD / EDIT ACCOUNT DIALOG ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountDialog(
  categories: List<Category>,
  currencies: List<Currency> = emptyList(),
  existingAccount: Account? = null,
  onDismiss: () -> Unit,
  onConfirm: (name: String, ctgryNo: Long, mobile: String, openingBalance: Double, creditLimit: Double, debitLimit: Double, reminder: String?, currencyId: Long) -> Unit
) {
  var name by remember { mutableStateOf(existingAccount?.accountName ?: "") }
  var selectedCategory by remember {
    mutableStateOf(categories.find { it.ctgryNo == existingAccount?.ctgryNo } ?: categories.firstOrNull() ?: Category(1, "عام"))
  }
  var selectedCurrency by remember { mutableStateOf(currencies.firstOrNull() ?: Currency(1, "ر.ي")) }
  var mobile by remember { mutableStateOf(existingAccount?.mobile ?: "") }
  var openingBalanceStr by remember { mutableStateOf(if (existingAccount != null) abs(existingAccount.totalBalance).toString() else "") }
  var isCreditOpening by remember { mutableStateOf(existingAccount?.isCredit ?: true) }
  var creditLimitStr by remember { mutableStateOf(if ((existingAccount?.creditLimit ?: 0.0) > 0) existingAccount?.creditLimit.toString() else "") }
  var debitLimitStr by remember { mutableStateOf(if ((existingAccount?.debitLimit ?: 0.0) > 0) existingAccount?.debitLimit.toString() else "") }
  var reminderDate by remember { mutableStateOf(existingAccount?.alarmDate ?: "") }
  var categoryExpanded by remember { mutableStateOf(false) }
  var currencyExpanded by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      Surface(
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp)
      ) {
        Column(
          modifier = Modifier
            .padding(22.dp)
            .verticalScroll(rememberScrollState()),
          horizontalAlignment = Alignment.Start
        ) {
          Text(
            text = if (existingAccount == null) "إضافة حساب جديد" else "تعديل الحساب",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = PrimaryText
          )
          Spacer(modifier = Modifier.height(16.dp))

          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("اسم الحساب *") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_account_name"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Category Selector
          ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = it },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = selectedCategory.ctgryName,
              onValueChange = {},
              readOnly = true,
              label = { Text("التصنيف") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
              shape = RoundedCornerShape(14.dp)
            )
            ExposedDropdownMenu(
              expanded = categoryExpanded,
              onDismissRequest = { categoryExpanded = false }
            ) {
              categories.forEach { cat ->
                DropdownMenuItem(
                  text = { Text(cat.ctgryName) },
                  onClick = {
                    selectedCategory = cat
                    categoryExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Currency Selector
          ExposedDropdownMenuBox(
            expanded = currencyExpanded,
            onExpandedChange = { currencyExpanded = it },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = selectedCurrency.cName,
              onValueChange = {},
              readOnly = true,
              label = { Text("العملة") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
              shape = RoundedCornerShape(14.dp)
            )
            ExposedDropdownMenu(
              expanded = currencyExpanded,
              onDismissRequest = { currencyExpanded = false }
            ) {
              currencies.forEach { cur ->
                DropdownMenuItem(
                  text = { Text(cur.cName) },
                  onClick = {
                    selectedCurrency = cur
                    currencyExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = mobile,
            onValueChange = { mobile = it },
            label = { Text("رقم الهاتف / الجوال") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
          )

          if (existingAccount == null) {
            Spacer(modifier = Modifier.height(12.dp))
            AmountTextField(
              value = openingBalanceStr,
              onValueChange = { openingBalanceStr = it },
              label = { Text("الرصيد الافتتاحي") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = { isCreditOpening = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                  containerColor = if (isCreditOpening) PositiveGreenSoft else Color.Transparent
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                  brush = androidx.compose.ui.graphics.SolidColor(if (isCreditOpening) PositiveGreen else BorderColor)
                )
              ) {
                Text("لك (وارد)", color = if (isCreditOpening) PositiveGreen else SecondaryText)
              }

              OutlinedButton(
                onClick = { isCreditOpening = false },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                  containerColor = if (!isCreditOpening) NegativeRedSoft else Color.Transparent
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                  brush = androidx.compose.ui.graphics.SolidColor(if (!isCreditOpening) NegativeRed else BorderColor)
                )
              ) {
                Text("عليك (صادر)", color = if (!isCreditOpening) NegativeRed else SecondaryText)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          AmountTextField(
            value = debitLimitStr,
            onValueChange = { debitLimitStr = it },
            label = { Text("سقف الحساب / حد المديونية (اختياري)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = reminderDate,
            onValueChange = { reminderDate = it },
            label = { Text("تاريخ التذكير (مثال: 2026-11-20)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = RoyalBlue) },
            singleLine = true
          )

          Spacer(modifier = Modifier.height(20.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(onClick = onDismiss) {
              Text("إلغاء", color = SecondaryText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                if (name.isNotBlank()) {
                  val rawOpen = openingBalanceStr.replace(",", "").toDoubleOrNull() ?: 0.0
                  val effectiveOpen = if (isCreditOpening) rawOpen else -rawOpen
                  val credLim = creditLimitStr.replace(",", "").toDoubleOrNull() ?: 0.0
                  val debLim = debitLimitStr.replace(",", "").toDoubleOrNull() ?: 0.0
                  onConfirm(name, selectedCategory.ctgryNo, mobile, effectiveOpen, credLim, debLim, reminderDate.ifBlank { null }, selectedCurrency.cId)
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.testTag("submit_account_button")
            ) {
              Text("حفظ الحساب", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

// --- ADD / EDIT TRANSACTION DIALOG ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionDialog(
  accounts: List<Account>,
  currencies: List<Currency>,
  selectedAccountNo: Long? = null,
  onDismiss: () -> Unit,
  onConfirm: (accountNo: Long, amount: Double, date: String, details: String, currencyId: Long) -> Unit
) {
  var selectedAccount by remember {
    mutableStateOf(accounts.find { it.accountNo == selectedAccountNo } ?: accounts.firstOrNull())
  }
  var selectedCurrency by remember { mutableStateOf(currencies.firstOrNull() ?: Currency(1, "ر.ي")) }
  var isIncoming by remember { mutableStateOf(true) } // true = لك / وارد (+), false = عليك / صادر (-)
  var amountStr by remember { mutableStateOf("") }
  var details by remember { mutableStateOf("") }
  val currentDate = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date()) }
  var dateStr by remember { mutableStateOf(currentDate) }

  var accountExpanded by remember { mutableStateOf(false) }
  var currencyExpanded by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      Surface(
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp)
      ) {
        Column(
          modifier = Modifier
            .padding(22.dp)
            .verticalScroll(rememberScrollState()),
          horizontalAlignment = Alignment.Start
        ) {
          Text(
            text = "إضافة حركة مالية جديدة",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = PrimaryText
          )
          Spacer(modifier = Modifier.height(16.dp))

          // Transaction Type Switcher (وارد / صادر)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isIncoming) PositiveGreenSoft else SecondaryBackground)
                .border(
                  width = 1.5.dp,
                  color = if (isIncoming) PositiveGreen else Color.Transparent,
                  shape = RoundedCornerShape(14.dp)
                )
                .clickable { isIncoming = true }
                .padding(vertical = 12.dp)
                .testTag("tx_type_incoming"),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.ArrowDownward,
                  contentDescription = null,
                  tint = if (isIncoming) PositiveGreen else SecondaryText,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "وارد (لك)",
                  color = if (isIncoming) PositiveGreen else SecondaryText,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(if (!isIncoming) NegativeRedSoft else SecondaryBackground)
                .border(
                  width = 1.5.dp,
                  color = if (!isIncoming) NegativeRed else Color.Transparent,
                  shape = RoundedCornerShape(14.dp)
                )
                .clickable { isIncoming = false }
                .padding(vertical = 12.dp)
                .testTag("tx_type_outgoing"),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.ArrowUpward,
                  contentDescription = null,
                  tint = if (!isIncoming) NegativeRed else SecondaryText,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "صادر (عليك)",
                  color = if (!isIncoming) NegativeRed else SecondaryText,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Account Dropdown
          ExposedDropdownMenuBox(
            expanded = accountExpanded,
            onExpandedChange = { accountExpanded = it },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = selectedAccount?.accountName ?: "اختر الحساب",
              onValueChange = {},
              readOnly = true,
              label = { Text("الحساب *") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .testTag("input_tx_account"),
              shape = RoundedCornerShape(14.dp)
            )
            ExposedDropdownMenu(
              expanded = accountExpanded,
              onDismissRequest = { accountExpanded = false }
            ) {
              accounts.forEach { acc ->
                DropdownMenuItem(
                  text = { Text("${acc.accountName} (${acc.ctgryName})") },
                  onClick = {
                    selectedAccount = acc
                    accountExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Amount & Currency Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            AmountTextField(
              value = amountStr,
              onValueChange = { amountStr = it },
              label = { Text("المبلغ *") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier
                .weight(1.5f)
                .testTag("input_tx_amount"),
              shape = RoundedCornerShape(14.dp),
              singleLine = true
            )

            ExposedDropdownMenuBox(
              expanded = currencyExpanded,
              onExpandedChange = { currencyExpanded = it },
              modifier = Modifier.weight(1f)
            ) {
              OutlinedTextField(
                value = selectedCurrency.cName,
                onValueChange = {},
                readOnly = true,
                label = { Text("العملة") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                modifier = Modifier
                  .fillMaxWidth()
                  .menuAnchor(),
                shape = RoundedCornerShape(14.dp)
              )
              ExposedDropdownMenu(
                expanded = currencyExpanded,
                onDismissRequest = { currencyExpanded = false }
              ) {
                currencies.forEach { cur ->
                  DropdownMenuItem(
                    text = { Text(cur.cName) },
                    onClick = {
                      selectedCurrency = cur
                      currencyExpanded = false
                    }
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = details,
            onValueChange = { details = it },
            label = { Text("وصف المعاملة / البيان") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_tx_details"),
            shape = RoundedCornerShape(14.dp),
            minLines = 2
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = dateStr,
            onValueChange = { dateStr = it },
            label = { Text("التاريخ") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = RoyalBlue) },
            singleLine = true
          )

          Spacer(modifier = Modifier.height(20.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(onClick = onDismiss) {
              Text("إلغاء", color = SecondaryText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                val acc = selectedAccount
                val amt = amountStr.replace(",", "").toDoubleOrNull()
                if (acc != null && amt != null && amt > 0) {
                  val effectiveAmount = if (isIncoming) amt else -amt
                  onConfirm(acc.accountNo, effectiveAmount, dateStr, details, selectedCurrency.cId)
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isIncoming) PositiveGreen else NegativeRed
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.testTag("submit_tx_button")
            ) {
              Text("حفظ المعاملة", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

// --- ACCOUNT ACTION CONTEXTUAL BOTTOM SHEET ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountActionBottomSheet(
  account: Account,
  sheetState: SheetState = rememberModalBottomSheetState(),
  onDismiss: () -> Unit,
  onOpenDetails: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onCopy: () -> Unit,
  onMove: () -> Unit,
  onTransfer: () -> Unit,
  onAddTransaction: () -> Unit
) {
  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    ModalBottomSheet(
      onDismissRequest = onDismiss,
      sheetState = sheetState,
      shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 22.dp, vertical = 12.dp)
      ) {
        // Account summary header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = account.accountName,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryText
            )
            Text(
              text = "التصنيف: ${account.ctgryName} • الرصيد: ${String.format(Locale.ENGLISH, "%,.2f", account.totalBalance)} ر.ي",
              fontSize = 12.sp,
              color = SecondaryText
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = SecondaryText)
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

        ActionItemRow(icon = Icons.Default.Receipt, title = "كشف الحساب والعمليات", onClick = { onDismiss(); onOpenDetails() })
        ActionItemRow(icon = Icons.Default.Edit, title = "إضافة حركة مالية للحساب", onClick = { onDismiss(); onAddTransaction() })
        ActionItemRow(icon = Icons.Default.DriveFileMove, title = "تحويل أموال من / إلى هذا الحساب", onClick = { onDismiss(); onTransfer() })
        ActionItemRow(icon = Icons.Default.Edit, title = "تعديل بيانات الحساب", onClick = { onDismiss(); onEdit() })
        ActionItemRow(icon = Icons.Default.DriveFileMove, title = "نقل الحساب إلى تصنيف آخر", onClick = { onDismiss(); onMove() })
        ActionItemRow(icon = Icons.Default.ContentCopy, title = "نسخ الحساب", onClick = { onDismiss(); onCopy() })
        ActionItemRow(icon = Icons.Default.Delete, title = "حذف الحساب والمعاملات", tint = NegativeRed, onClick = { onDismiss(); onDelete() })

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun ActionItemRow(
  icon: ImageVector,
  title: String,
  tint: Color = PrimaryText,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .padding(horizontal = 12.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    Spacer(modifier = Modifier.width(14.dp))
    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = tint)
  }
}

// --- VOUCHER PREVIEW & SHARING DIALOG ---

@Composable
fun VoucherPreviewDialog(
  voucher: Voucher,
  onDismiss: () -> Unit,
  onShare: () -> Unit = {}
) {
  Dialog(onDismissRequest = onDismiss) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      Surface(
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp)
      ) {
        Column(
          modifier = Modifier
            .padding(22.dp)
            .verticalScroll(rememberScrollState()),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Voucher Top Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (voucher.voucherType.isCredit) PositiveGreenSoft else NegativeRedSoft)
              .padding(horizontal = 16.dp, vertical = 8.dp)
          ) {
            Text(
              text = voucher.voucherType.titleAr,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = if (voucher.voucherType.isCredit) PositiveGreen else NegativeRed
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "رقم السند: ${voucher.voucherNumber}", color = SecondaryText, fontSize = 12.sp)

          Spacer(modifier = Modifier.height(16.dp))

          // Clean Document Card
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SecondaryBackground)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              VoucherDetailRow(label = "الحساب المستفيد / الطرف", value = voucher.accountName)
              HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
              VoucherDetailRow(label = "المبلغ المالي", value = "${String.format(Locale.ENGLISH, "%,.2f", voucher.amount)} ${voucher.currencyName}", isBold = true)
              HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
              VoucherDetailRow(label = "التاريخ والوقت", value = voucher.date)
              if (voucher.details.isNotBlank()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                VoucherDetailRow(label = "البيان / الوصف", value = voucher.details)
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onDismiss,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text("إغلاق", color = SecondaryText)
            }
            Button(
              onClick = onShare,
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
              shape = RoundedCornerShape(14.dp)
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("مشاركة", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun VoucherDetailRow(label: String, value: String, isBold: Boolean = false) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = label, color = SecondaryText, fontSize = 12.sp)
    Text(text = value, color = PrimaryText, fontSize = 14.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
  }
}

// --- ANNUAL CLOSING WARNING DIALOG ---

@Composable
fun AnnualClosingDialog(
  onDismiss: () -> Unit,
  onConfirm: (cutoffDate: String) -> Unit
) {
  val defaultCutoff = remember {
    val cal = Calendar.getInstance()
    "${cal.get(Calendar.YEAR)}-12-31"
  }
  var cutoffDate by remember { mutableStateOf(defaultCutoff) }

  Dialog(onDismissRequest = onDismiss) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      Surface(
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(22.dp),
          horizontalAlignment = Alignment.Start
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(NegativeRedSoft),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = NegativeRed, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "تأكيد الإقفال السنوي",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = PrimaryText
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "تنبيه هام: عملية الإقفال السنوي ستقوم بأرشفة المعاملات حتى التاريخ المحدد، وترحيل الأرصدة النهائية كأرصدة افتتاحية جديدة لجميع الحسابات. لا يمكن التراجع عن هذه العملية بدون استعادة نسخة احتياطية.",
            fontSize = 13.sp,
            color = SecondaryText,
            lineHeight = 20.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = cutoffDate,
            onValueChange = { cutoffDate = it },
            label = { Text("تاريخ الإقفال (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
          )

          Spacer(modifier = Modifier.height(20.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(onClick = onDismiss) {
              Text("إلغاء", color = SecondaryText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = { onConfirm(cutoffDate) },
              colors = ButtonDefaults.buttonColors(containerColor = NegativeRed),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text("تنفيذ الإقفال", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

fun formatAmountInput(input: String): String {
  val cleaned = input.replace(",", "")
  val parts = cleaned.split(".", limit = 2)
  val intPart = parts[0].filter { it.isDigit() }
  val decPart = if (parts.size > 1) parts[1].filter { it.isDigit() } else null
  val formattedInt = if (intPart.isEmpty()) {
    ""
  } else {
    try {
      String.format(java.util.Locale.ENGLISH, "%,d", intPart.toLong())
    } catch (e: NumberFormatException) {
      intPart
    }
  }
  return if (decPart == null) formattedInt else "$formattedInt.$decPart"
}


/**
 * حقل مبلغ بفواصل الآلاف مع إبقاء المؤشر في مكانه الصحيح أثناء الكتابة
 * (بدون القفز إلى منتصف الأرقام).
 */
@Composable
fun AmountTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  label: (@Composable () -> Unit)? = null,
  placeholder: (@Composable () -> Unit)? = null,
  shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(14.dp),
  keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
  singleLine: Boolean = true
) {
  var field by remember {
    mutableStateOf(androidx.compose.ui.text.input.TextFieldValue(value, androidx.compose.ui.text.TextRange(value.length)))
  }
  if (field.text != value) {
    field = androidx.compose.ui.text.input.TextFieldValue(value, androidx.compose.ui.text.TextRange(value.length))
  }
  OutlinedTextField(
    value = field,
    onValueChange = { new ->
      val formatted = formatAmountInput(new.text)
      val cursor = new.selection.end.coerceIn(0, new.text.length)
      val digitsBeforeCursor = new.text.take(cursor).count { it != ',' }
      var pos = 0
      var seen = 0
      while (pos < formatted.length && seen < digitsBeforeCursor) {
        if (formatted[pos] != ',') seen++
        pos++
      }
      field = androidx.compose.ui.text.input.TextFieldValue(formatted, androidx.compose.ui.text.TextRange(pos))
      onValueChange(formatted)
    },
    modifier = modifier,
    label = label,
    placeholder = placeholder,
    keyboardOptions = keyboardOptions,
    shape = shape,
    singleLine = singleLine
  )
}
