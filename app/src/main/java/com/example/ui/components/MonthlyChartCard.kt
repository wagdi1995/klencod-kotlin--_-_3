package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MonthlyStat
import com.example.ui.theme.DividerColor
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SecondaryText

@Composable
fun MonthlyChartCard(
  stats: List<MonthlyStat>,
  modifier: Modifier = Modifier
) {
  val maxAmount = stats.maxOfOrNull { maxOf(it.incoming, it.outgoing) }?.coerceAtLeast(30000.0) ?: 30000.0

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .testTag("monthly_chart_card"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      // Header: Title and Legend
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Legend
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(RoyalBlue)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "وارد",
              color = SecondaryText,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(Color(0xFFE45757).copy(alpha = 0.85f))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "صادر",
              color = SecondaryText,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Title
        Text(
          text = "الحركة الشهرية",
          color = PrimaryText,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Chart Canvas & Grid
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
      ) {
        // Horizontal Grid Lines
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          listOf("30K", "20K", "10K", "0").forEach { label ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = label,
                color = SecondaryText.copy(alpha = 0.6f),
                fontSize = 9.sp,
                modifier = Modifier.width(26.dp)
              )
              HorizontalDivider(
                color = DividerColor.copy(alpha = 0.7f),
                thickness = 0.8.dp,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }

        // Bars
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 32.dp, end = 8.dp, bottom = 4.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.Bottom
        ) {
          stats.forEach { stat ->
            val inRatio = (stat.incoming / maxAmount).coerceIn(0.08, 1.0).toFloat()
            val outRatio = (stat.outgoing / maxAmount).coerceIn(0.06, 1.0).toFloat()

            Row(
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              verticalAlignment = Alignment.Bottom
            ) {
              // Incoming Bar (Blue)
              Box(
                modifier = Modifier
                  .width(13.dp)
                  .fillMaxHeight(inRatio)
                  .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                  .background(RoyalBlue)
              )
              // Outgoing Bar (Coral)
              Box(
                modifier = Modifier
                  .width(13.dp)
                  .fillMaxHeight(outRatio)
                  .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                  .background(Color(0xFFF37878))
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Month Labels
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 32.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        stats.forEach { stat ->
          Text(
            text = stat.monthLabel,
            color = SecondaryText,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(36.dp)
          )
        }
      }
    }
  }
}
