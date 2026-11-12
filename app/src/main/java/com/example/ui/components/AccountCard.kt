package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Account
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
fun AccountCard(
  account: Account,
  onClick: () -> Unit,
  onLongClick: () -> Unit = {},
  onReminderClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val icon = when {
    account.accountName.contains("شركة") -> Icons.Default.Business
    account.accountName.contains("مؤسسة") || account.accountName.contains("متجر") -> Icons.Default.Storefront
    else -> Icons.Default.Person
  }

  val avatarBg = when {
    account.isCredit -> PositiveGreenSoft
    account.isDebit -> NegativeRedSoft
    else -> SoftBlue
  }

  val avatarTint = when {
    account.isCredit -> PositiveGreen
    account.isDebit -> NegativeRed
    else -> RoyalBlue
  }

  val bannerBg = when {
    account.isCredit -> PositiveGreenSoft
    account.isDebit -> NegativeRedSoft
    else -> SecondaryBackground
  }

  val bannerTextColor = when {
    account.isCredit -> PositiveGreen
    account.isDebit -> NegativeRed
    else -> SecondaryText
  }

  val statusLabel = when {
    account.isCredit -> "لك"
    account.isDebit -> "عليك"
    else -> "متزن"
  }

  val formattedBalance = when {
    account.isDebit -> "-" + String.format(Locale.ENGLISH, "%,.2f", abs(account.totalBalance)) + " ر.ي"
    else -> String.format(Locale.ENGLISH, "%,.2f", abs(account.totalBalance)) + " ر.ي"
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 7.dp)
      .clickable { onClick() }
      .testTag("account_card_${account.accountNo}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Top info row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        // Reminder bell icon on left in RTL
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(if (account.alarmStatus) SoftBlue else SecondaryBackground)
            .clickable { onReminderClick() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (account.alarmStatus) Icons.Default.Notifications else Icons.Default.NotificationsNone,
            contentDescription = "تذكير",
            tint = if (account.alarmStatus) RoyalBlue else SecondaryText,
            modifier = Modifier.size(19.dp)
          )
        }

        // Account Name & Category on right in RTL
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.End
        ) {
          Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(end = 12.dp)
          ) {
            Text(
              text = account.accountName,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryText
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (account.mobile.isNotBlank()) {
                Text(
                  text = account.mobile,
                  fontSize = 11.sp,
                  color = SecondaryText,
                  modifier = Modifier.padding(end = 8.dp)
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(SecondaryBackground)
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text(
                  text = account.ctgryName.ifBlank { "عام" },
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = SecondaryText
                )
              }
            }
          }

          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(avatarBg),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = avatarTint,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Bottom colored balance container matching Screenshot 2
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(bannerBg)
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = formattedBalance,
            color = bannerTextColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = statusLabel,
            color = bannerTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
