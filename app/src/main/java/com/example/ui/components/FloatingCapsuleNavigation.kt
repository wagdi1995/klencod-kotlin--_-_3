package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBackground
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SecondaryText

enum class NavDestination(
  val route: String,
  val labelAr: String,
  val activeIcon: ImageVector,
  val inactiveIcon: ImageVector
) {
  HOME("home", "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
  ACCOUNTS("accounts", "الحسابات", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
  REPORTS("reports", "التقارير", Icons.Filled.BarChart, Icons.Outlined.BarChart),
  MORE("more", "المزيد", Icons.Filled.GridView, Icons.Outlined.GridView)
}

@Composable
fun FloatingCapsuleNavigation(
  currentDestination: NavDestination,
  onDestinationSelected: (NavDestination) -> Unit,
  modifier: Modifier = Modifier
) {
  // Always render bottom navigation in Arabic RTL
  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Surface(
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 14.dp)
        .shadow(
          elevation = 16.dp,
          shape = RoundedCornerShape(32.dp),
          ambientColor = DeepNavy.copy(alpha = 0.12f),
          spotColor = RoyalBlue.copy(alpha = 0.22f)
        )
        .testTag("floating_capsule_nav"),
      shape = RoundedCornerShape(32.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        NavDestination.values().forEach { destination ->
          val isSelected = destination == currentDestination
          val interactionSource = remember { MutableInteractionSource() }

          val targetBg = if (isSelected) RoyalBlue else Color.Transparent
          val targetContentColor = if (isSelected) Color.White else SecondaryText

          val backgroundColor by animateColorAsState(
            targetValue = targetBg,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "tab_bg"
          )
          val contentColor by animateColorAsState(
            targetValue = targetContentColor,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "tab_content"
          )

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(24.dp))
              .background(backgroundColor)
              .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onDestinationSelected(destination) }
              )
              .padding(horizontal = if (isSelected) 18.dp else 14.dp, vertical = 10.dp)
              .testTag("nav_tab_${destination.route}"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = if (isSelected) destination.activeIcon else destination.inactiveIcon,
                contentDescription = destination.labelAr,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
              )
              if (isSelected) {
                Text(
                  text = destination.labelAr,
                  color = contentColor,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(start = 8.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}
