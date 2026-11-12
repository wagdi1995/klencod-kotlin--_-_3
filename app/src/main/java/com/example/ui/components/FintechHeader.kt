package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CardBackground
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText

@Composable
fun FintechHeader(
  title: String = "مدونة الحسابات",
  subtitle: String = "مستقبل مالي أفضل",
  currentDate: String = "الثلاثاء، 3 نوفمبر 2026",
  onNotificationClick: () -> Unit = {},
  onLogoClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Notification Icon with unread red badge (Right side in LTR, Left in RTL)
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surface)
        .clickable { onNotificationClick() }
        .testTag("notification_button"),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Outlined.Notifications,
        contentDescription = "الإشعارات",
        tint = PrimaryText,
        modifier = Modifier.size(24.dp)
      )
      // Red dot for unread notification
      Box(
        modifier = Modifier
          .size(9.dp)
          .align(Alignment.TopEnd)
          .padding(top = 10.dp, end = 10.dp)
          .clip(CircleShape)
          .background(NegativeRed)
      )
    }

    // App Branding (Logo + Title)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.End,
      modifier = Modifier.clickable { onLogoClick() }
    ) {
      Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier.padding(end = 12.dp)
      ) {
        Text(
          text = title,
          fontWeight = FontWeight.ExtraBold,
          fontSize = 19.sp,
          color = PrimaryText
        )
        Text(
          text = subtitle,
          fontWeight = FontWeight.Normal,
          fontSize = 12.sp,
          color = SecondaryText
        )
      }
      Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = "شعار التطبيق",
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(12.dp))
      )
    }
  }
}
