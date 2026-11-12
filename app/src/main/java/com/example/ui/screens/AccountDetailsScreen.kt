package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountDetailsSummary
import com.example.data.model.Currency
import com.example.data.model.TransactionItem
import com.example.ui.components.TransactionRow
import com.example.ui.theme.CardBackground
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.NegativeRedSoft
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PositiveGreenSoft
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue
import java.util.Locale
import kotlin.math.abs

@Composable
fun AccountDetailsScreen(
  summary: AccountDetailsSummary,
  transactions: List<TransactionItem>,
  currencies: List<Currency>,
  selectedCurrencyId: Long?,
  selectedFilterType: String, // "ALL", "INCOMING", "OUTGOING"
  onCurrencySelect: (Long?) -> Unit,
  onFilterTypeSelect: (String) -> Unit,
  onBackClick: () -> Unit,
  onAddTransactionClick: () -> Unit,
  onTransactionClick: (TransactionItem) -> Unit,
  modifier: Modifier = Modifier
) {
  var filterMenuExpanded by remember { mutableStateOf(false) }
  var currencyMenuExpanded by remember { mutableStateOf(false) }

  val account = summary.account

  val icon = when {
    account.accountName.contains("شركة") -> Icons.Default.Business
    account.accountName.contains("مؤسسة") || account.accountName.contains("متجر") -> Icons.Default.Storefront
    else -> Icons.Default.Person
  }

  val formattedBalance = String.format(Locale.ENGLISH, "%,.2f", summary.currentBalance) + " ر.ي"

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .testTag("account_details_screen")
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 90.dp)
      ) {
        // Top Bar
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = onBackClick,
              modifier = Modifier.testTag("back_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "رجوع",
                tint = PrimaryText
              )
            }
            Text(
              text = "كشف حساب",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryText,
              modifier = Modifier.padding(start = 8.dp)
            )
          }
        }

        // Account Main Header Card matching Screenshot 3
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 6.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
              ) {
                Column(
                  horizontalAlignment = Alignment.End,
                  modifier = Modifier.padding(end = 14.dp)
                ) {
                  Text(
                    text = account.accountName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                  )
                  Text(
                    text = account.ctgryName.ifBlank { "عملاء" },
                    fontSize = 12.sp,
                    color = SecondaryText
                  )
                  if (account.mobile.isNotBlank()) {
                    Text(
                      text = account.mobile,
                      fontSize = 12.sp,
                      color = SecondaryText
                    )
                  }
                }

                Box(
                  modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoftBlue),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RoyalBlue,
                    modifier = Modifier.size(26.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(18.dp))

              // Current Balance Row
              Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
              ) {
                Text(
                  text = "الرصيد الحالي",
                  fontSize = 12.sp,
                  color = SecondaryText,
                  fontWeight = FontWeight.Medium
                )
                Text(
                  text = formattedBalance,
                  fontSize = 26.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (summary.currentBalance < 0) NegativeRed else PrimaryText
                )
              }
            }
          }
        }

        // 4-pill Balance Summary Row matching Screenshot 3
        item {
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Opening balance
            SummaryPill(
              title = "رصيد افتتاحي",
              amount = String.format(Locale.ENGLISH, "%,.0f", summary.openingBalance),
              icon = Icons.Default.AccountBalanceWallet,
              iconBg = PositiveGreenSoft,
              iconTint = PositiveGreen,
              modifier = Modifier.weight(1f)
            )

            // Incoming (وارد)
            SummaryPill(
              title = "وارد",
              amount = String.format(Locale.ENGLISH, "%,.0f", summary.totalIncoming),
              icon = Icons.Default.ArrowDownward,
              iconBg = SoftBlue,
              iconTint = RoyalBlue,
              modifier = Modifier.weight(1f)
            )

            // Outgoing (صادر)
            SummaryPill(
              title = "صادر",
              amount = String.format(Locale.ENGLISH, "%,.0f", summary.totalOutgoing),
              icon = Icons.Default.ArrowUpward,
              iconBg = NegativeRedSoft,
              iconTint = NegativeRed,
              modifier = Modifier.weight(1f)
            )

            // Currency breakdown pill (e.g. Dollar)
            val secondaryCur = summary.currencyBreakdowns.firstOrNull { it.currencyId != 1L }
            SummaryPill(
              title = secondaryCur?.currencyName ?: "دولار",
              amount = String.format(Locale.ENGLISH, "%,.0f", secondaryCur?.balance ?: 0.0),
              icon = Icons.Default.AttachMoney,
              iconBg = SoftBlue,
              iconTint = RoyalBlue,
              modifier = Modifier.weight(1f)
            )
          }
        }

        // Filters row (الفترة, النوع, العملة)
        item {
          Spacer(modifier = Modifier.height(14.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Filter by Type
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { filterMenuExpanded = true }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              val label = when (selectedFilterType) {
                "INCOMING" -> "وارد فقط"
                "OUTGOING" -> "صادر فقط"
                else -> "النوع: الكل"
              }
              Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = PrimaryText)

              DropdownMenu(
                expanded = filterMenuExpanded,
                onDismissRequest = { filterMenuExpanded = false }
              ) {
                DropdownMenuItem(text = { Text("الكل") }, onClick = { onFilterTypeSelect("ALL"); filterMenuExpanded = false })
                DropdownMenuItem(text = { Text("وارد فقط") }, onClick = { onFilterTypeSelect("INCOMING"); filterMenuExpanded = false })
                DropdownMenuItem(text = { Text("صادر فقط") }, onClick = { onFilterTypeSelect("OUTGOING"); filterMenuExpanded = false })
              }
            }

            // Filter by Currency
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { currencyMenuExpanded = true }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              val curName = currencies.find { it.cId == selectedCurrencyId }?.cName ?: "العملة: الكل"
              Text(text = curName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = PrimaryText)

              DropdownMenu(
                expanded = currencyMenuExpanded,
                onDismissRequest = { currencyMenuExpanded = false }
              ) {
                DropdownMenuItem(text = { Text("كل العملات") }, onClick = { onCurrencySelect(null); currencyMenuExpanded = false })
                currencies.forEach { cur ->
                  DropdownMenuItem(text = { Text(cur.cName) }, onClick = { onCurrencySelect(cur.cId); currencyMenuExpanded = false })
                }
              }
            }
          }
        }

        // Movements Header
        item {
          Spacer(modifier = Modifier.height(14.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 22.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "الحركات (${transactions.size})",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryText
            )
          }
        }

        // Transactions List
        if (transactions.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "لا توجد حركات مسجلة لهذا الحساب", color = SecondaryText, fontSize = 14.sp)
            }
          }
        } else {
          items(transactions, key = { it.transId }) { tx ->
            TransactionRow(
              item = tx,
              showAccountName = false,
              onClick = { onTransactionClick(tx) }
            )
          }
        }
      }

      // Add Transaction FAB
      FloatingActionButton(
        onClick = onAddTransactionClick,
        containerColor = RoyalBlue,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(start = 24.dp, bottom = 24.dp)
          .testTag("add_transaction_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = "إضافة حركة")
      }
    }
  }
}

@Composable
private fun SummaryPill(
  title: String,
  amount: String,
  icon: ImageVector,
  iconBg: Color,
  iconTint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        fontSize = 10.sp,
        color = SecondaryText,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = amount,
        fontSize = 14.sp,
        color = PrimaryText,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(6.dp))
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(iconBg),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}
