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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DashboardOverview
import com.example.ui.components.FintechHeader
import com.example.ui.components.MonthlyChartCard
import com.example.ui.components.PrimaryBalanceCard
import com.example.ui.components.QuickActionsGrid
import com.example.ui.components.TransactionRow
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SecondaryText

@Composable
fun HomeScreen(
  overview: DashboardOverview,
  onQuickAction: (String) -> Unit,
  onViewAllTransactions: () -> Unit,
  onTransactionClick: (Long) -> Unit,
  onNotificationClick: () -> Unit,
  onAccountClick: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .testTag("home_screen")
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
      ) {
        // 1. Header
        item {
          FintechHeader(
            onNotificationClick = onNotificationClick,
            onLogoClick = {}
          )
        }

        // 2. Primary Balance Card (Deep navy to royal blue)
        item {
          PrimaryBalanceCard(
            totalBalance = overview.totalBalance,
            totalCredit = overview.totalCredit,
            totalDebit = overview.totalDebit,
            accountsCount = overview.accountsCount
          )
        }

        // 3. Monthly Chart Card (وارد / صادر comparison)
        item {
          Spacer(modifier = Modifier.height(10.dp))
          MonthlyChartCard(stats = overview.monthlyStats)
        }

        // 4. Quick Actions
        item {
          Spacer(modifier = Modifier.height(8.dp))
          QuickActionsGrid(onActionClick = onQuickAction)
        }

        // 5. Recent Activity Header
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 22.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "عرض الكل",
              color = RoyalBlue,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier
                .clickable { onViewAllTransactions() }
                .testTag("view_all_transactions_link")
            )

            Text(
              text = "آخر العمليات",
              color = PrimaryText,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // 6. Recent Operations List
        if (overview.recentTransactions.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "لا توجد معاملات مسجلة حتى الآن",
                color = SecondaryText,
                fontSize = 14.sp
              )
            }
          }
        } else {
          items(overview.recentTransactions) { tx ->
            TransactionRow(
              item = tx,
              showAccountName = true,
              onClick = { onAccountClick(tx.accountNo) }
            )
          }
        }
      }
    }
  }
}
