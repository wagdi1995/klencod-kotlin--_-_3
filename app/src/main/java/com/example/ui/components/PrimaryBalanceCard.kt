package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.RoyalBlue
import java.util.Locale

@Composable
fun PrimaryBalanceCard(
  totalBalance: Double,
  totalCredit: Double, // لك
  totalDebit: Double,  // عليك
  accountsCount: Int,
  currencyCode: String = "ر.ي",
  modifier: Modifier = Modifier
) {
  val formattedBalance = String.format(Locale.ENGLISH, "%,.2f", totalBalance)
  val formattedCredit = String.format(Locale.ENGLISH, "%,.0f", totalCredit)
  val formattedDebit = String.format(Locale.ENGLISH, "%,.0f", totalDebit)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .testTag("primary_balance_card"),
    shape = RoundedCornerShape(26.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              DeepNavy,
              Color(0xFF1B3B66),
              RoyalBlue
            )
          )
        )
        .padding(22.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Top Row: Accounts count badge and "إجمالي الأرصدة" label
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Accounts badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(Color.White.copy(alpha = 0.16f))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "$accountsCount حساباً",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }

          Text(
            text = "إجمالي الأرصدة",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Balance Amount (Tabular, Large)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.Bottom
        ) {
          Text(
            text = currencyCode,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp, end = 8.dp)
          )
          Text(
            text = formattedBalance,
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom sub-cards row: لك (Credit) on right, عليك (Debit) on left (RTL)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // عليك (Debit)
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF382332).copy(alpha = 0.85f))
              .padding(horizontal = 14.dp, vertical = 10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFE45757).copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Remove,
                  contentDescription = "عليك",
                  tint = Color(0xFFFF8585),
                  modifier = Modifier.size(16.dp)
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "عليك",
                  color = Color.White.copy(alpha = 0.75f),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Normal
                )
                Text(
                  text = formattedDebit,
                  color = Color(0xFFFF8585),
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          // لك (Credit)
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF14474B).copy(alpha = 0.85f))
              .padding(horizontal = 14.dp, vertical = 10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(PositiveGreen.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "لك",
                  tint = Color(0xFF4EE3A8),
                  modifier = Modifier.size(16.dp)
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "لك",
                  color = Color.White.copy(alpha = 0.75f),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Normal
                )
                Text(
                  text = formattedCredit,
                  color = Color(0xFF4EE3A8),
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}
