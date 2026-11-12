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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Account
import com.example.data.model.Category
import com.example.ui.components.AccountCard
import com.example.ui.components.FintechHeader
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue

@Composable
fun AccountsScreen(
  accounts: List<Account>,
  categories: List<Category>,
  selectedCategoryNo: Long?,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  onCategorySelect: (Long?) -> Unit,
  onAccountClick: (Long) -> Unit,
  onAccountLongClick: (Account) -> Unit,
  onAddAccountClick: () -> Unit,
  onSortClick: () -> Unit,
  onNotificationClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .testTag("accounts_screen")
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 110.dp)
      ) {
        // Top Header
        item {
          FintechHeader(
            onNotificationClick = onNotificationClick,
            onLogoClick = {}
          )
        }

        // Title Row
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 22.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "الحسابات",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryText
            )
          }
        }

        // Search Bar and Quick Controls Row
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 6.dp)
          ) {
            // Search Input
            OutlinedTextField(
              value = searchQuery,
              onValueChange = onSearchChange,
              placeholder = { Text("ابحث عن حساب بالاسم أو الهاتف...", color = SecondaryText, fontSize = 14.sp) },
              trailingIcon = {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = "بحث",
                  tint = RoyalBlue
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("search_account_input"),
              shape = RoundedCornerShape(18.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = RoyalBlue,
                unfocusedBorderColor = BorderColor
              ),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Pills Row: Accounts Count, Sort, Add
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Right: Count Badge
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .background(SoftBlue)
                  .padding(horizontal = 12.dp, vertical = 7.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = null,
                    tint = RoyalBlue,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "${accounts.size} حساباً",
                    color = RoyalBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              // Left: Control icons (Sort, Filter, Add)
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onSortClick() }
                    .testTag("sort_accounts_button"),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "ترتيب",
                    tint = PrimaryText,
                    modifier = Modifier.size(20.dp)
                  )
                }

                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(RoyalBlue)
                    .clickable { onAddAccountClick() }
                    .testTag("add_account_fab"),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة حساب",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                  )
                }
              }
            }
          }
        }

        // Category Filter Chips Row matching Screenshot 2
        item {
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // "الكل" Chip
            val isAllSelected = selectedCategoryNo == null
            item {
              CategoryChip(
                label = "الكل",
                isSelected = isAllSelected,
                onClick = { onCategorySelect(null) }
              )
            }

            items(categories) { cat ->
              val isSelected = selectedCategoryNo == cat.ctgryNo
              CategoryChip(
                label = cat.ctgryName,
                isSelected = isSelected,
                onClick = { onCategorySelect(cat.ctgryNo) }
              )
            }
          }
        }

        // Accounts List
        if (accounts.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "لا توجد حسابات مطابقة",
                  color = SecondaryText,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(SoftBlue)
                    .clickable { onAddAccountClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                  Text("إضافة حساب الآن", color = RoyalBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
              }
            }
          }
        } else {
          items(accounts, key = { it.accountNo }) { account ->
            AccountCard(
              account = account,
              onClick = { onAccountClick(account.accountNo) },
              onLongClick = { onAccountLongClick(account) },
              onReminderClick = { onAccountLongClick(account) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CategoryChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(14.dp))
      .background(if (isSelected) RoyalBlue else MaterialTheme.colorScheme.surface)
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 9.dp)
      .testTag("cat_chip_$label"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      color = if (isSelected) Color.White else PrimaryText,
      fontSize = 13.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
    )
  }
}
