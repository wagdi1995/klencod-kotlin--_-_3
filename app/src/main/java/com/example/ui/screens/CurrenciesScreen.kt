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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.Currency
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue

@Composable
fun CurrenciesScreen(
  currencies: List<Currency>,
  onBackClick: () -> Unit,
  onAddCurrency: (String) -> Unit,
  onUpdateCurrency: (Long, String) -> Unit,
  onDeleteCurrency: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddDialog by remember { mutableStateOf(false) }
  var editingCurrency by remember { mutableStateOf<Currency?>(null) }
  var deletingCurrency by remember { mutableStateOf<Currency?>(null) }

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .testTag("currencies_screen")
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
      ) {
        // Top Bar
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = PrimaryText)
              }
              Text(
                text = "إدارة العملات",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(start = 8.dp)
              )
            }

            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(RoyalBlue)
                .clickable { showAddDialog = true }
                .testTag("add_currency_button"),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Add, contentDescription = "إضافة عملة", tint = Color.White, modifier = Modifier.size(22.dp))
            }
          }
        }

        item {
          Text(
            text = "تتيح لك إدارة العملات تحديد العملة المحلية والعملات الأجنبية الأخرى. يتم احتساب أرصدة كل عملة بشكل مستقل ودقيق دون خلط.",
            fontSize = 13.sp,
            color = SecondaryText,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
        }

        items(currencies, key = { it.cId }) { cur ->
          CurrencyCard(
            currency = cur,
            onEdit = { editingCurrency = cur },
            onDelete = { deletingCurrency = cur }
          )
        }
      }

      // Add Currency Dialog
      if (showAddDialog) {
        AddEditCurrencyDialog(
          title = "إضافة عملة جديدة",
          initialName = "",
          onDismiss = { showAddDialog = false },
          onConfirm = { name ->
            onAddCurrency(name)
            showAddDialog = false
          }
        )
      }

      // Edit Currency Dialog
      editingCurrency?.let { cur ->
        AddEditCurrencyDialog(
          title = "تعديل العملة",
          initialName = cur.cName,
          onDismiss = { editingCurrency = null },
          onConfirm = { newName ->
            onUpdateCurrency(cur.cId, newName)
            editingCurrency = null
          }
        )
      }

      // Delete Confirm Dialog
      deletingCurrency?.let { cur ->
        Dialog(onDismissRequest = { deletingCurrency = null }) {
          Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(20.dp)) {
              Text(text = "حذف العملة", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PrimaryText)
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "هل أنت متأكد من حذف عملة '${cur.cName}'؟",
                color = SecondaryText,
                fontSize = 13.sp
              )
              Spacer(modifier = Modifier.height(18.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { deletingCurrency = null }) { Text("إلغاء", color = SecondaryText) }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                  onClick = {
                    onDeleteCurrency(cur.cId)
                    deletingCurrency = null
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = NegativeRed),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Text("حذف", color = Color.White)
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CurrencyCard(
  currency: Currency,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        if (currency.cId != 1L) {
          IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = NegativeRed, modifier = Modifier.size(18.dp))
          }
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
          Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = RoyalBlue, modifier = Modifier.size(18.dp))
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 12.dp)) {
          Text(text = currency.cName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
          Text(
            text = "${currency.transCount} حركة مرتبطة ${if (currency.cId == 1L) "(العملة المحلية الرئيسية)" else ""}",
            fontSize = 11.sp,
            color = SecondaryText
          )
        }

        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SoftBlue),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.AttachMoney, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(22.dp))
        }
      }
    }
  }
}

@Composable
private fun AddEditCurrencyDialog(
  title: String,
  initialName: String,
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit
) {
  var name by remember { mutableStateOf(initialName) }

  Dialog(onDismissRequest = onDismiss) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(22.dp)) {
          Text(text = title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PrimaryText)
          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("اسم العملة") },
            placeholder = { Text("مثال: يورو (€)") },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(18.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = SecondaryText) }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = { if (name.isNotBlank()) onConfirm(name.trim()) },
              colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text("حفظ", color = Color.White)
            }
          }
        }
      }
    }
  }
}
