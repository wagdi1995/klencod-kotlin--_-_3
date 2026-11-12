package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.example.ui.BiometricHelper
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Code
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
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.example.data.model.SecuritySettings
import com.example.ui.components.AnnualClosingDialog
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBackground
import com.example.ui.theme.DividerColor
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PositiveGreenSoft
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue
import com.example.ui.theme.WarningOrange
import com.example.ui.theme.WarningOrangeSoft
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
  securitySettings: SecuritySettings,
  restorePoints: List<File>,
  onUpdateSecurity: (SecuritySettings) -> Unit,
  onNavigateToAccounts: () -> Unit,
  onNavigateToCategories: () -> Unit,
  onNavigateToCurrencies: () -> Unit,
  onCreateLocalBackup: () -> Unit,
  onChooseBackupLocation: () -> Unit,
  onRestoreBackup: (File) -> Unit,
  onChooseRestoreFile: () -> Unit,
  onDeleteBackup: (File) -> Unit,
  onAnnualClosing: (String) -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showPinDialog by remember { mutableStateOf(false) }
  var showAnnualClosingDialog by remember { mutableStateOf(false) }
  var selectedRestorePoint by remember { mutableStateOf<File?>(null) }
  var successMsg by remember { mutableStateOf<String?>(null) }

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .testTag("settings_screen")
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 110.dp)
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
              text = "الإعدادات والأمان",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryText,
              modifier = Modifier.padding(start = 8.dp)
            )
          }
        }

        // 1. Security Section (الأمان والحماية)
        item {
          SettingsSectionHeader(title = "الأمان وقفل التطبيق")
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              // PIN Lock Switch
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Lock, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(22.dp))
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(text = "قفل التطبيق برمز PIN", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text(text = if (securitySettings.isPinEnabled) "مفعل (اضغط للتغيير)" else "غير مفعل", fontSize = 11.sp, color = SecondaryText)
                  }
                }
                Switch(
                  checked = securitySettings.isPinEnabled,
                  onCheckedChange = { isChecked ->
                    if (isChecked) {
                      showPinDialog = true
                    } else {
                      onUpdateSecurity(securitySettings.copy(isPinEnabled = false, pinCode = ""))
                    }
                  },
                  colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalBlue)
                )
              }

              HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

              // Fingerprint Switch
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Fingerprint, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(22.dp))
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(text = "المصادقة ببصمة الإصبع", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text(text = "فتح القفل السريع باستخدام البصمة البيومترية", fontSize = 11.sp, color = SecondaryText)
                  }
                }
                Switch(
                  checked = securitySettings.isFingerprintEnabled,
                  onCheckedChange = { isChecked ->
                    if (!isChecked) {
                      onUpdateSecurity(securitySettings.copy(isFingerprintEnabled = false))
                    } else {
                      val reason = BiometricHelper.unavailableReason(context)
                      val act = context as? FragmentActivity
                      if (reason != null || act == null) {
                        Toast.makeText(context, reason ?: "تعذر تفعيل البصمة", Toast.LENGTH_LONG).show()
                      } else {
                        // تأكيد ببصمة الجهاز قبل التفعيل
                        BiometricHelper.authenticate(
                          activity = act,
                          title = "تفعيل القفل بالبصمة",
                          subtitle = "تحقق ببصمتك لتأكيد التفعيل",
                          negativeText = "إلغاء",
                          onSuccess = {
                            onUpdateSecurity(securitySettings.copy(isFingerprintEnabled = true))
                          },
                          onFailure = { cancelled, msg ->
                            if (!cancelled && msg.isNotBlank()) {
                              Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                          }
                        )
                      }
                    }
                  },
                  colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalBlue)
                )
              }
            }
          }
        }

        // 2. Financial Settings (الإعدادات المالية)
        item {
          Spacer(modifier = Modifier.height(14.dp))
          SettingsSectionHeader(title = "الإعدادات المالية")
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
              SettingsRowItem(
                icon = Icons.Default.Info,
                title = "إدارة الحسابات",
                subtitle = "إضافة وحذف أي حساب",
                onClick = onNavigateToAccounts
              )
              HorizontalDivider(color = DividerColor.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))
              SettingsRowItem(
                icon = Icons.Default.Category,
                title = "إدارة التصنيفات",
                subtitle = "إضافة وتعديل وحذف تصنيفات الحسابات",
                onClick = onNavigateToCategories
              )
              HorizontalDivider(color = DividerColor.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))
              SettingsRowItem(
                icon = Icons.Default.AttachMoney,
                title = "إدارة العملات",
                subtitle = "تخصيص العملة المحلية والعملات الأجنبية",
                onClick = onNavigateToCurrencies
              )
            }
          }
        }

        // 3. Backup and Synchronization (النسخ الاحتياطي والاستعادة)
        item {
          Spacer(modifier = Modifier.height(14.dp))
          SettingsSectionHeader(title = "النسخ الاحتياطي وحفظ البيانات")
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              // Create Backup Button
              Button(
                onClick = {
                  onCreateLocalBackup()
                  successMsg = "تم إنشاء نقطة استعادة جديدة بنجاح في ذاكرة الجهاز"
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(14.dp)
              ) {
                Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("إنشاء نسخة احتياطية محلية جديدة", color = Color.White, fontWeight = FontWeight.Bold)
              }

              Spacer(modifier = Modifier.height(8.dp))

              // حفظ النسخة الاحتياطية في مكان يختاره المستخدم (مثل ملف التنزيلات)
              Button(
                onClick = onChooseBackupLocation,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(14.dp)
              ) {
                Icon(Icons.Default.FolderZip, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("اختر مكان لحفظ النسخة الاحتياطية", color = Color.White, fontWeight = FontWeight.Bold)
              }

              Spacer(modifier = Modifier.height(8.dp))

              // استعادة من ملف النسخة الاحتياطية المحفوظ (يختاره المستخدم)
              Button(
                onClick = onChooseRestoreFile,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280)),
                shape = RoundedCornerShape(14.dp)
              ) {
                Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("استعادة من ملف محفوظ (اختر الموقع)", color = Color.White, fontWeight = FontWeight.Bold)
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Google Drive Info Card
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(SoftBlue)
                  .padding(12.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.CloudDone, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(24.dp))
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(text = "مزامنة Google Drive", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryText)
                    Text(text = "الحفظ التلقائي لقاعدة بيانات myaccounts.db على السحابة لحمايتها من الفقدان", fontSize = 11.sp, color = SecondaryText)
                  }
                }
              }

              if (successMsg != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = successMsg ?: "", color = PositiveGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }

              // Restore points list
              if (restorePoints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "نقاط الاستعادة المحفوظة (${restorePoints.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryText)
                Spacer(modifier = Modifier.height(8.dp))

                restorePoints.forEach { file ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(12.dp))
                      .background(SecondaryBackground)
                      .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      IconButton(onClick = { onDeleteBackup(file) }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = NegativeRed, modifier = Modifier.size(16.dp))
                      }
                      IconButton(onClick = { shareDatabaseBackup(context, file) }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = RoyalBlue, modifier = Modifier.size(16.dp))
                      }
                      Button(
                        onClick = { selectedRestorePoint = file },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                      ) {
                        Text("استعادة", fontSize = 11.sp, color = Color.White)
                      }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text(text = file.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                      Text(text = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH).format(Date(file.lastModified())), fontSize = 10.sp, color = SecondaryText)
                    }
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                }
              }
            }
          }
        }

        // 4. Annual Closing (الإقفال السنوي)
        item {
          Spacer(modifier = Modifier.height(14.dp))
          SettingsSectionHeader(title = "الإقفال المالي السنوي")
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "الإقفال السنوي يقوم بإنهاء الدورة المحاسبية وأرشفة العمليات السابقة وترحيل الأرصدة الحالية كأرصدة افتتاحية جديدة لجميع الحسابات.",
                fontSize = 12.sp,
                color = SecondaryText,
                lineHeight = 18.sp
              )
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = { showAnnualClosingDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = NegativeRed),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("open_annual_closing_button")
              ) {
                Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("بدء إجراءات الإقفال السنوي", color = Color.White, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // 5. About Application
        item {
          Spacer(modifier = Modifier.height(14.dp))
          SettingsSectionHeader(title = "حول التطبيق")
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(text = "مدونة الحسابات - الإصدار 3.4.1", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryText)
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "تطبيق محاسبي متكامل وبسيط لإدارة المصاريف والديون والحسابات والسندات والتقارير المالية. جميع البيانات مخزنة بأمان ومحمية محلياً وسحابياً.",
                fontSize = 12.sp,
                color = SecondaryText,
                lineHeight = 18.sp
              )
            }
          }

          // مربع المبرمج
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Code, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "برمجة وتعديل", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryText)
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(text = "م/ وجدي جمال محمد", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryText)
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .clickable {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:776581908"))
                    context.startActivity(intent)
                  }
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "هاتف: 776581908", fontSize = 13.sp, color = RoyalBlue, fontWeight = FontWeight.Medium)
              }
            }
          }
        }
      }

      // PIN Setup Dialog
      if (showPinDialog) {
        PinSetupDialog(
          onDismiss = { showPinDialog = false },
          onConfirm = { pin ->
            onUpdateSecurity(securitySettings.copy(isPinEnabled = true, pinCode = pin))
            showPinDialog = false
          }
        )
      }

      // Annual Closing Dialog
      if (showAnnualClosingDialog) {
        AnnualClosingDialog(
          onDismiss = { showAnnualClosingDialog = false },
          onConfirm = { cutoff ->
            onAnnualClosing(cutoff)
            showAnnualClosingDialog = false
            successMsg = "تم تنفيذ الإقفال السنوي وترحيل الأرصدة بنجاح"
          }
        )
      }

      // Restore Confirm Dialog
      selectedRestorePoint?.let { file ->
        Dialog(onDismissRequest = { selectedRestorePoint = null }) {
          Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(20.dp)) {
              Text(text = "تأكيد استعادة النسخة الاحتياطية", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PrimaryText)
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "سيتم استبدال البيانات الحالية بالبيانات المحفوظة في ملف '${file.name}'. هل تود المتابعة؟",
                fontSize = 13.sp,
                color = SecondaryText
              )
              Spacer(modifier = Modifier.height(18.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { selectedRestorePoint = null }) { Text("إلغاء", color = SecondaryText) }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                  onClick = {
                    onRestoreBackup(file)
                    selectedRestorePoint = null
                    successMsg = "تمت استعادة قاعدة البيانات بنجاح"
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Text("استعادة الآن", color = Color.White)
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
private fun SettingsSectionHeader(title: String) {
  Text(
    text = title,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    color = PrimaryText,
    modifier = Modifier.padding(horizontal = 22.dp, vertical = 6.dp)
  )
}

@Composable
private fun SettingsRowItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(24.dp))
    Spacer(modifier = Modifier.width(14.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
      Text(text = subtitle, fontSize = 11.sp, color = SecondaryText)
    }
  }
}

@Composable
private fun PinSetupDialog(
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit
) {
  var pin by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(22.dp)) {
          Text(text = "تعيين رمز PIN للحماية", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PrimaryText)
          Spacer(modifier = Modifier.height(10.dp))
          Text(text = "أدخل رمز مرور مكون من 4 أو 6 أرقام لقفل التطبيق:", fontSize = 13.sp, color = SecondaryText)
          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 6) pin = it },
            label = { Text("رمز PIN") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(18.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = SecondaryText) }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = { if (pin.length >= 4) onConfirm(pin) },
              colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text("حفظ الرمز", color = Color.White)
            }
          }
        }
      }
    }
  }
}

private fun shareDatabaseBackup(context: Context, file: File) {
  val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "application/octet-stream"
    putExtra(Intent.EXTRA_STREAM, uri)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
  }
  context.startActivity(Intent.createChooser(intent, "مشاركة ملف النسخة الاحتياطية"))
}
