package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionItem
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue
import java.util.Locale
import kotlin.math.abs

@Composable
fun TransactionRow(
  item: TransactionItem,
  showAccountName: Boolean = true,
  onClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val isPositive = item.amount > 0.0
  val formattedAmount = if (isPositive) {
    "+" + String.format(Locale.ENGLISH, "%,.2f", item.amount) + " " + item.cName
  } else {
    "-" + String.format(Locale.ENGLISH, "%,.2f", abs(item.amount)) + " " + item.cName
  }

  val amountColor = if (isPositive) PositiveGreen else NegativeRed

  val iconInfo = when {
    item.isTransfer -> Icons.Default.SwapHoriz to SoftBlue
    item.details.contains("بضاعة") -> Icons.Default.ShoppingCart to Color(0xFFFFECEC)
    item.details.contains("سند") -> Icons.Default.Receipt to Color(0xFFF0F4FF)
    item.accountName.contains("شركة") || item.accountName.contains("مؤسسة") -> Icons.Default.Business to SoftBlue
    else -> Icons.Default.Description to Color(0xFFE8F7F0)
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 5.dp)
      .clickable { onClick() }
      .testTag("tx_item_${item.transId}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Amount and running balance / date (Left in RTL)
      Column(horizontalAlignment = Alignment.Start) {
        Text(
          text = formattedAmount,
          color = amountColor,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
        if (item.totalBalance != 0.0) {
          Text(
            text = "الرصيد: " + String.format(Locale.ENGLISH, "%,.2f", item.totalBalance),
            color = SecondaryText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
          )
        } else {
          Text(
            text = item.transDate.substringBefore(" "),
            color = SecondaryText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
          )
        }
      }

      // Title & Avatar (Right in RTL)
      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = if (showAccountName && item.accountName.isNotBlank()) item.accountName else item.details,
            color = PrimaryText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (showAccountName && item.accountName.isNotBlank()) item.details else item.transDate.substringBefore(" "),
            color = SecondaryText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(iconInfo.second),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = iconInfo.first,
            contentDescription = null,
            tint = if (isPositive) RoyalBlue else NegativeRed,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }
  }
}
