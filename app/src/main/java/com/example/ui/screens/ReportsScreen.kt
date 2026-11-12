package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.Account
import com.example.data.model.Category
import com.example.data.model.Currency
import com.example.data.model.ReportResult
import com.example.data.model.ReportType
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.DividerColor
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
  accounts: List<Account>,
  categories: List<Category>,
  currencies: List<Currency>,
  reportResult: ReportResult?,
  onGenerateReport: (type: ReportType, from: String?, to: String?, accNo: Long?, catNo: Long?, curId: Long?) -> Unit,
  onExportCsv: (ReportResult) -> File?,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedType by remember { mutableStateOf(ReportType.ACCOUNTS_BALANCE_SUMMARY) }
  var selectedAccount by remember { mutableStateOf<Account?>(null) }
  var selectedCategory by remember { mutableStateOf<Category?>(null) }
  var selectedCurrency by remember { mutableStateOf<Currency?>(null) }
  var fromDate by remember { mutableStateOf("") }
  var toDate by remember { mutableStateOf("") }

  var accountMenuExpanded by remember { mutableStateOf(false) }

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .testTag("reports_screen")
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
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(onClick = onBackClick) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = PrimaryText)
            }
            Text(
              text = "التقارير والتحليلات المالية",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryText,
              modifier = Modifier.padding(start = 8.dp)
            )
          }
        }

        // Report Type Horizontal Selector
        item {
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(ReportType.values()) { type ->
              val isSelected = type == selectedType
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .background(if (isSelected) RoyalBlue else MaterialTheme.colorScheme.surface)
                  .clickable {
                    selectedType = type
                    onGenerateReport(type, fromDate.ifBlank { null }, toDate.ifBlank { null }, selectedAccount?.accountNo, selectedCategory?.ctgryNo, selectedCurrency?.cId)
                  }
                  .padding(horizontal = 16.dp, vertical = 10.dp)
                  .testTag("report_tab_${type.name}")
              ) {
                Text(
                  text = type.titleAr,
                  color = if (isSelected) Color.White else PrimaryText,
                  fontSize = 13.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              }
            }
          }
        }

        // Filter Controls Card
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(text = "خيارات التصفية", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryText)
              Spacer(modifier = Modifier.height(10.dp))

              // If report requires specific account (like Account Statement)
              if (selectedType == ReportType.ACCOUNT_STATEMENT) {
                ExposedDropdownMenuBox(
                  expanded = accountMenuExpanded,
                  onExpandedChange = { accountMenuExpanded = it },
                  modifier = Modifier.fillMaxWidth()
                ) {
                  OutlinedTextField(
                    value = selectedAccount?.accountName ?: "اختر الحساب",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("الحساب") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountMenuExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(14.dp)
                  )
                  ExposedDropdownMenu(
                    expanded = accountMenuExpanded,
                    onDismissRequest = { accountMenuExpanded = false }
                  ) {
                    accounts.forEach { acc ->
                      DropdownMenuItem(
                        text = { Text(acc.accountName) },
                        onClick = {
                          selectedAccount = acc
                          accountMenuExpanded = false
                        }
                      )
                    }
                  }
                }
                Spacer(modifier = Modifier.height(10.dp))
              }

              // Date range row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedTextField(
                  value = fromDate,
                  onValueChange = { fromDate = it },
                  label = { Text("من تاريخ") },
                  placeholder = { Text("YYYY-MM-DD") },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(14.dp),
                  singleLine = true
                )
                OutlinedTextField(
                  value = toDate,
                  onValueChange = { toDate = it },
                  label = { Text("إلى تاريخ") },
                  placeholder = { Text("YYYY-MM-DD") },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(14.dp),
                  singleLine = true
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = {
                  onGenerateReport(
                    selectedType,
                    fromDate.ifBlank { null },
                    toDate.ifBlank { null },
                    selectedAccount?.accountNo,
                    selectedCategory?.ctgryNo,
                    selectedCurrency?.cId
                  )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(14.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
              ) {
                Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("تحديث التقرير", fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        // Report Results Section
        if (reportResult == null || reportResult.rows.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "لا توجد نتائج مسجلة لهذا التقرير",
                color = SecondaryText,
                fontSize = 14.sp
              )
            }
          }
        } else {
          // Report Header & Export Actions
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
              shape = RoundedCornerShape(20.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  // Export action buttons
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                      onClick = {
                        val file = onExportCsv(reportResult)
                        if (file != null) shareReportFile(context, file)
                      },
                      modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SoftBlue)
                    ) {
                      Icon(Icons.Default.FileDownload, contentDescription = "تصدير CSV", tint = RoyalBlue, modifier = Modifier.size(20.dp))
                    }

                    IconButton(
                      onClick = {
                        shareReportText(context, reportResult)
                      },
                      modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SoftBlue)
                    ) {
                      Icon(Icons.Default.Share, contentDescription = "مشاركة التقرير", tint = RoyalBlue, modifier = Modifier.size(20.dp))
                    }
                  }

                  Text(
                    text = reportResult.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrimaryText
                  )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Table Layout
                val scrollState = rememberScrollState()
                Box(modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState)) {
                  Column {
                    // Header Row
                    Row(
                      modifier = Modifier
                        .background(SecondaryBackground, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                      reportResult.headers.forEach { header ->
                        Text(
                          text = header,
                          fontWeight = FontWeight.Bold,
                          fontSize = 12.sp,
                          color = PrimaryText,
                          modifier = Modifier.width(100.dp),
                          textAlign = TextAlign.Center
                        )
                      }
                    }

                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 4.dp))

                    // Data Rows
                    reportResult.rows.forEach { row ->
                      Row(
                        modifier = Modifier
                          .padding(horizontal = 8.dp, vertical = 8.dp)
                      ) {
                        listOf(row.col1, row.col2, row.col3, row.col4, row.col5, row.col6)
                          .take(reportResult.headers.size)
                          .forEachIndexed { idx, col ->
                            val textColor = if (idx == 2 || idx == 3) {
                              when (row.isPositive) {
                                true -> PositiveGreen
                                false -> NegativeRed
                                else -> PrimaryText
                              }
                            } else PrimaryText

                            Text(
                              text = col,
                              fontSize = 12.sp,
                              color = textColor,
                              fontWeight = if (idx == 2 || idx == 3) FontWeight.Bold else FontWeight.Normal,
                              modifier = Modifier.width(100.dp),
                              textAlign = TextAlign.Center
                            )
                          }
                      }
                      HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Summary Totals
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SecondaryBackground)
                    .padding(12.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "الصافي: ${String.format(Locale.ENGLISH, "%,.2f", reportResult.netBalance)} ر.ي",
                      fontWeight = FontWeight.ExtraBold,
                      color = if (reportResult.netBalance >= 0) PositiveGreen else NegativeRed,
                      fontSize = 14.sp
                    )
                    Text(
                      text = "وارد: ${String.format(Locale.ENGLISH, "%,.0f", reportResult.totalIncoming)} | صادر: ${String.format(Locale.ENGLISH, "%,.0f", reportResult.totalOutgoing)}",
                      fontSize = 12.sp,
                      color = SecondaryText
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

private fun shareReportText(context: Context, report: ReportResult) {
  val text = buildString {
    appendLine("*${report.title}*")
    report.rows.take(20).forEach { r ->
      appendLine("• ${r.col1} | ${r.col2}: ${r.col3} (${r.col4})")
    }
    appendLine("الصافي الإجمالي: ${String.format(Locale.ENGLISH, "%,.2f", report.netBalance)}")
    appendLine("تم الإصدار عبر مدونة الحسابات")
  }
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, text)
  }
  context.startActivity(Intent.createChooser(intent, "مشاركة التقرير"))
}

private fun shareReportFile(context: Context, file: File) {
  val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/csv"
    putExtra(Intent.EXTRA_STREAM, uri)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
  }
  context.startActivity(Intent.createChooser(intent, "تصدير التقرير"))
}
