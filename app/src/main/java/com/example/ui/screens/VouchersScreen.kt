package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Account
import com.example.data.model.Currency
import com.example.data.model.Voucher
import com.example.data.model.VoucherType
import com.example.ui.components.VoucherPreviewDialog
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VouchersScreen(
  accounts: List<Account>,
  currencies: List<Currency>,
  initialType: VoucherType = VoucherType.RECEIPT,
  onBackClick: () -> Unit,
  onCreateVoucher: (type: VoucherType, accountNo: Long, amount: Double, currencyId: Long, date: String, details: String, onDone: (Voucher) -> Unit) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedType by remember { mutableStateOf(initialType) }
  var selectedAccount by remember { mutableStateOf(accounts.firstOrNull()) }
  var selectedCurrency by remember { mutableStateOf(currencies.firstOrNull() ?: Currency(1, "ر.ي")) }
  var amountStr by remember { mutableStateOf("") }
  val currentDate = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(Date()) }
  var dateStr by remember { mutableStateOf(currentDate) }
  var details by remember { mutableStateOf("") }

  var accountExpanded by remember { mutableStateOf(false) }
  var currencyExpanded by remember { mutableStateOf(false) }
  var createdVoucher by remember { mutableStateOf<Voucher?>(null) }
  var errorMsg by remember { mutableStateOf<String?>(null) }

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .testTag("vouchers_screen")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(bottom = 30.dp)
      ) {
        // Top Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = PrimaryText)
          }
          Text(
            text = "السندات المالية والإشعارات",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            modifier = Modifier.padding(start = 8.dp)
          )
        }

        // Voucher Types Filter Bar (All 4 types from original app)
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(VoucherType.values()) { type ->
            val isSelected = type == selectedType
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) RoyalBlue else MaterialTheme.colorScheme.surface)
                .clickable { selectedType = type }
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("voucher_tab_${type.name}")
            ) {
              Text(
                text = type.titleAr,
                color = if (isSelected) Color.White else PrimaryText,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Voucher Form Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            // Header Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (selectedType.isCredit) PositiveGreenSoft else NegativeRedSoft)
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(
                text = selectedType.titleAr,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedType.isCredit) PositiveGreen else NegativeRed
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Account Selector
            ExposedDropdownMenuBox(
              expanded = accountExpanded,
              onExpandedChange = { accountExpanded = it },
              modifier = Modifier.fillMaxWidth()
            ) {
              OutlinedTextField(
                value = selectedAccount?.accountName ?: "اختر الحساب",
                onValueChange = {},
                readOnly = true,
                label = { Text("الحساب / الطرف المعني *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
                modifier = Modifier
                  .fillMaxWidth()
                  .menuAnchor()
                  .testTag("voucher_account_input"),
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

            Spacer(modifier = Modifier.height(14.dp))

            // Amount and Currency
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("المبلغ *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                  .weight(1.5f)
                  .testTag("voucher_amount_input"),
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

            Spacer(modifier = Modifier.height(14.dp))

            // Date
            OutlinedTextField(
              value = dateStr,
              onValueChange = { dateStr = it },
              label = { Text("التاريخ") },
              trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = RoyalBlue) },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Notes / Description
            val defaultDescriptionPlaceholder = when (selectedType) {
              VoucherType.CREDIT_NOTICE -> "نود إشعاركم أننا قيدنا إلى حسابكم لدينا..."
              VoucherType.DEBIT_NOTICE -> "نود إشعاركم أننا قيدنا على حسابكم لدينا..."
              else -> "أدخل بيان السند والتفاصيل..."
            }

            OutlinedTextField(
              value = details,
              onValueChange = { details = it },
              label = { Text("البيان / الوصف") },
              placeholder = { Text(defaultDescriptionPlaceholder, fontSize = 12.sp) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("voucher_details_input"),
              shape = RoundedCornerShape(14.dp),
              minLines = 3
            )

            if (errorMsg != null) {
              Spacer(modifier = Modifier.height(10.dp))
              Text(text = errorMsg ?: "", color = NegativeRed, fontSize = 12.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Create Voucher Button
        Button(
          onClick = {
            val acc = selectedAccount
            val amt = amountStr.toDoubleOrNull()
            if (acc == null) {
              errorMsg = "يرجى اختيار الحساب أولاً"
            } else if (amt == null || amt <= 0) {
              errorMsg = "يرجى إدخال مبلغ صالح"
            } else {
              errorMsg = null
              onCreateVoucher(
                selectedType,
                acc.accountNo,
                amt,
                selectedCurrency.cId,
                dateStr,
                details.ifBlank { selectedType.titleAr }
              ) { v -> createdVoucher = v }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(54.dp)
            .testTag("create_voucher_button"),
          colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
          shape = RoundedCornerShape(18.dp)
        ) {
          Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "إصدار ${selectedType.titleAr}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        }
      }

      // Dialog showing issued voucher preview
      createdVoucher?.let { v ->
        VoucherPreviewDialog(
          voucher = v,
          onDismiss = { createdVoucher = null },
          onShare = {
            shareVoucher(context, v)
          }
        )
      }
    }
  }
}

private fun shareVoucher(context: Context, voucher: Voucher) {
  val shareText = """
    *${voucher.voucherType.titleAr}*
    رقم السند: ${voucher.voucherNumber}
    الحساب: ${voucher.accountName}
    المبلغ: ${String.format(Locale.ENGLISH, "%,.2f", voucher.amount)} ${voucher.currencyName}
    التاريخ: ${voucher.date}
    البيان: ${voucher.details}
    
    تم الإصدار عبر تطبيق مدونة الحسابات
  """.trimIndent()

  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, shareText)
  }
  context.startActivity(Intent.createChooser(intent, "مشاركة السند"))
}
