package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.NegativeRedSoft
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PositiveGreenSoft
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SoftBlue
import com.example.ui.theme.WarningOrange
import com.example.ui.theme.WarningOrangeSoft

data class QuickActionItem(
  val id: String,
  val label: String,
  val icon: ImageVector,
  val iconTint: Color,
  val containerColor: Color
)

@Composable
fun QuickActionsGrid(
  onActionClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val actions = listOf(
    QuickActionItem(
      id = "add_account",
      label = "إضافة حساب",
      icon = Icons.Default.Add,
      iconTint = RoyalBlue,
      containerColor = SoftBlue
    ),
    QuickActionItem(
      id = "add_transaction",
      label = "إضافة عملية",
      icon = Icons.Default.Description,
      iconTint = RoyalBlue,
      containerColor = SoftBlue
    ),
    QuickActionItem(
      id = "transfer",
      label = "تحويل",
      icon = Icons.Default.SwapHoriz,
      iconTint = RoyalBlue,
      containerColor = SoftBlue
    ),
    QuickActionItem(
      id = "receipt_voucher",
      label = "سند قبض",
      icon = Icons.Default.Receipt,
      iconTint = PositiveGreen,
      containerColor = PositiveGreenSoft
    ),
    QuickActionItem(
      id = "payment_voucher",
      label = "سند صرف",
      icon = Icons.Default.Receipt,
      iconTint = NegativeRed,
      containerColor = NegativeRedSoft
    ),
    QuickActionItem(
      id = "reports",
      label = "التقارير",
      icon = Icons.Default.BarChart,
      iconTint = WarningOrange,
      containerColor = WarningOrangeSoft
    )
  )

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    LazyRow(
      modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
    ) {
      items(actions) { action ->
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .width(68.dp)
            .clickable { onActionClick(action.id) }
            .testTag("quick_action_${action.id}")
        ) {
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(RoundedCornerShape(18.dp))
              .background(action.containerColor),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = action.icon,
              contentDescription = action.label,
              tint = action.iconTint,
              modifier = Modifier.size(26.dp)
            )
          }
          Spacer(modifier = Modifier.height(7.dp))
          Text(
            text = action.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryText,
            textAlign = TextAlign.Center,
            maxLines = 1
          )
        }
      }
    }
  }
}
