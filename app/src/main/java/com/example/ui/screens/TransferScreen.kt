package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import kotlin.math.abs
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Account
import com.example.data.model.Currency
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.NegativeRed
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

@Composable
fun TransferScreen(
  accounts: List<Account>,
  currencies: List<Currency>,
  onBackClick: () -> Unit,
  onExecuteTransfer: (sourceNo: Long, destNo: Long, amount: Double, currencyId: Long, date: String, details: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var sourceAccount by remember { mutableStateOf(accounts.firstOrNull()) }
  var destAccount by remember {
    mutableStateOf(accounts.getOrNull(1) ?: accounts.firstOrNull())
  }
  var selectedCurrency by remember { mutableStateOf(currencies.firstOrNull() ?: Currency(1, "ريال يمني")) }
  var amountStr by remember { mutableStateOf("1500.00") }
  val currentDate = remember { SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date()) }
  var dateStr by remember { mutableStateOf(currentDate) }
  var description by remember { mutableStateOf("") }

  var showSourcePicker by remember { mutableStateOf(false) }
  var showDestPicker by remember { mutableStateOf(false) }
  var showCurrencyMenu by remember { mutableStateOf(false) }
  var showConfirmDialog by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .testTag("transfer_screen")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(bottom = 24.dp)
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
            text = "تحويل بين الحسابات",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            modifier = Modifier.padding(start = 8.dp)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Accounts Selection Container with Center Swap Icon
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Source Account ("تحويل من")
            TransferAccountSelectorCard(
              title = "تحويل من",
              account = sourceAccount,
              onClick = { showSourcePicker = true },
              avatarBg = SoftBlue,
              avatarTint = RoyalBlue,
              tag = "transfer_source_card"
            )

            // Dest Account ("تحويل إلى")
            TransferAccountSelectorCard(
              title = "تحويل إلى",
              account = destAccount,
              onClick = { showDestPicker = true },
              avatarBg = PositiveGreenSoft,
              avatarTint = PositiveGreen,
              tag = "transfer_dest_card"
            )
          }

          // Center Floating Swap Icon Button
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surface)
              .border(2.dp, SoftBlue, CircleShape)
              .clickable {
                // Swap accounts
                val temp = sourceAccount
                sourceAccount = destAccount
                destAccount = temp
              }
              .testTag("swap_accounts_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.SwapVert,
              contentDescription = "تبديل الحسابات",
              tint = RoyalBlue,
              modifier = Modifier.size(24.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transfer Form Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            // Amount
            Text(text = "المبلغ", fontSize = 12.sp, color = SecondaryText, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = amountStr,
              onValueChange = { amountStr = it },
              placeholder = { Text("0.00") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_transfer_amount"),
              shape = RoundedCornerShape(14.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Currency
            Text(text = "العملة", fontSize = 12.sp, color = SecondaryText, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                .clickable { showCurrencyMenu = true }
                .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = SecondaryText)
                Text(text = selectedCurrency.cName, color = PrimaryText, fontSize = 14.sp)
              }

              DropdownMenu(
                expanded = showCurrencyMenu,
                onDismissRequest = { showCurrencyMenu = false }
              ) {
                currencies.forEach { cur ->
                  DropdownMenuItem(
                    text = { Text(cur.cName) },
                    onClick = {
                      selectedCurrency = cur
                      showCurrencyMenu = false
                    }
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Date
            Text(text = "التاريخ", fontSize = 12.sp, color = SecondaryText, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = dateStr,
              onValueChange = { dateStr = it },
              leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SecondaryText) },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Description
            Text(text = "وصف التحويل", fontSize = 12.sp, color = SecondaryText, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = description,
              onValueChange = { description = it },
              placeholder = { Text("أدخل وصف التحويل أو رقم الفاتورة...", fontSize = 13.sp) },
              leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = SecondaryText) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_transfer_desc"),
              shape = RoundedCornerShape(14.dp),
              singleLine = true
            )

            if (errorMessage != null) {
              Spacer(modifier = Modifier.height(10.dp))
              Text(text = errorMessage ?: "", color = NegativeRed, fontSize = 12.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Primary Confirm Button
        Button(
          onClick = {
            val src = sourceAccount
            val dst = destAccount
            val amt = amountStr.toDoubleOrNull()

            when {
              src == null || dst == null -> errorMessage = "يرجى تحديد حسابي التحويل"
              src.accountNo == dst.accountNo -> errorMessage = "لا يمكن التحويل إلى نفس الحساب"
              amt == null || amt <= 0.0 -> errorMessage = "يرجى إدخال مبلغ تحويل صالح"
              else -> {
                errorMessage = null
                showConfirmDialog = true
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(54.dp)
            .testTag("review_transfer_button"),
          colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
          shape = RoundedCornerShape(18.dp)
        ) {
          Text("مراجعة وتأكيد التحويل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
      }

      // Dialog for Source Picker
      if (showSourcePicker) {
        AccountPickerDialog(
          title = "اختر حساب الإرسال (تحويل من)",
          accounts = accounts,
          selectedAccountNo = sourceAccount?.accountNo,
          onSelect = { sourceAccount = it; showSourcePicker = false },
          onDismiss = { showSourcePicker = false }
        )
      }

      // Dialog for Dest Picker
      if (showDestPicker) {
        AccountPickerDialog(
          title = "اختر حساب الاستلام (تحويل إلى)",
          accounts = accounts,
          selectedAccountNo = destAccount?.accountNo,
          onSelect = { destAccount = it; showDestPicker = false },
          onDismiss = { showDestPicker = false }
        )
      }

      // Confirmation Bottom Dialog
      if (showConfirmDialog && sourceAccount != null && destAccount != null) {
        val amt = amountStr.toDoubleOrNull() ?: 0.0
        val stdDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date())

        TransferConfirmDialog(
          sourceAccount = sourceAccount!!,
          destAccount = destAccount!!,
          amount = amt,
          currency = selectedCurrency.cName,
          date = dateStr,
          description = description,
          onDismiss = { showConfirmDialog = false },
          onConfirm = {
            onExecuteTransfer(
              sourceAccount!!.accountNo,
              destAccount!!.accountNo,
              amt,
              selectedCurrency.cId,
              stdDateTime,
              description
            )
            showConfirmDialog = false
            onBackClick()
          }
        )
      }
    }
  }
}

@Composable
private fun TransferAccountSelectorCard(
  title: String,
  account: Account?,
  onClick: () -> Unit,
  avatarBg: Color,
  avatarTint: Color,
  tag: String
) {
  val icon = when {
    account?.accountName?.contains("شركة") == true -> Icons.Default.Business
    account?.accountName?.contains("مؤسسة") == true -> Icons.Default.Storefront
    else -> Icons.Default.Person
  }

  val formattedBalance = if (account != null) {
    if (account.isDebit) "-" + String.format(Locale.ENGLISH, "%,.2f", abs(account.totalBalance)) + " ر.ي"
    else String.format(Locale.ENGLISH, "%,.2f", abs(account.totalBalance)) + " ر.ي"
  } else "0.00 ر.ي"

  val balColor = when {
    account?.isCredit == true -> PositiveGreen
    account?.isDebit == true -> NegativeRed
    else -> PrimaryText
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag(tag),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Text(text = title, fontSize = 12.sp, color = SecondaryText, fontWeight = FontWeight.Medium)
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Arrow indicator
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(18.dp))

        // Info & Avatar
        Row(verticalAlignment = Alignment.CenterVertically) {
          Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 12.dp)) {
            Text(
              text = account?.accountName ?: "اختر الحساب",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryText
            )
            Text(
              text = formattedBalance,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = balColor
            )
            Text(
              text = "حساب رقم •• ${account?.accountNo ?: "0000"}",
              fontSize = 11.sp,
              color = SecondaryText
            )
          }

          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(avatarBg),
            contentAlignment = Alignment.Center
          ) {
            Icon(icon, contentDescription = null, tint = avatarTint, modifier = Modifier.size(24.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun AccountPickerDialog(
  title: String,
  accounts: List<Account>,
  selectedAccountNo: Long?,
  onSelect: (Account) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth().height(420.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(text = title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PrimaryText)
          Spacer(modifier = Modifier.height(12.dp))

          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(accounts) { acc ->
              val isSelected = acc.accountNo == selectedAccountNo
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(if (isSelected) SoftBlue else SecondaryBackground)
                  .clickable { onSelect(acc) }
                  .padding(12.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = String.format(Locale.ENGLISH, "%,.2f", acc.totalBalance) + " ر.ي",
                    fontWeight = FontWeight.Bold,
                    color = if (acc.isDebit) NegativeRed else PositiveGreen,
                    fontSize = 13.sp
                  )
                  Column(horizontalAlignment = Alignment.End) {
                    Text(text = acc.accountName, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
                    Text(text = acc.ctgryName, color = SecondaryText, fontSize = 11.sp)
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
            Text("إلغاء", color = SecondaryText)
          }
        }
      }
    }
  }
}

@Composable
private fun TransferConfirmDialog(
  sourceAccount: Account,
  destAccount: Account,
  amount: Double,
  currency: String,
  date: String,
  description: String,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      Surface(
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(22.dp)) {
          Text(
            text = "تأكيد التحويل المالي",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = PrimaryText
          )
          Spacer(modifier = Modifier.height(14.dp))

          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SecondaryBackground),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "من حساب:", color = SecondaryText, fontSize = 12.sp)
                Text(text = sourceAccount.accountName, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "إلى حساب:", color = SecondaryText, fontSize = 12.sp)
                Text(text = destAccount.accountName, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "المبلغ المحول:", color = SecondaryText, fontSize = 12.sp)
                Text(text = "${String.format(Locale.ENGLISH, "%,.2f", amount)} $currency", fontWeight = FontWeight.ExtraBold, color = RoyalBlue, fontSize = 15.sp)
              }
              if (description.isNotBlank()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "البيان:", color = SecondaryText, fontSize = 12.sp)
                  Text(text = description, color = PrimaryText, fontSize = 12.sp)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(onClick = onDismiss) {
              Text("تعديل", color = SecondaryText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = onConfirm,
              colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.testTag("confirm_transfer_final_button")
            ) {
              Text("تأكيد التحويل الآن", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
