package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BorderColor
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NegativeRed
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue

@Composable
fun PinLockScreen(
  expectedPin: String,
  isFingerprintEnabled: Boolean,
  onUnlockSuccess: () -> Unit,
  onFingerprintClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var enteredPin by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }

  // طلب بصمة الجهاز تلقائياً عند ظهور شاشة القفل
  LaunchedEffect(isFingerprintEnabled) {
    if (isFingerprintEnabled) onFingerprintClick()
  }

  LaunchedEffect(enteredPin) {
    if (enteredPin.length == expectedPin.length) {
      if (enteredPin == expectedPin) {
        onUnlockSuccess()
      } else {
        isError = true
        kotlinx.coroutines.delay(400)
        enteredPin = ""
        isError = false
      }
    }
  }

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .testTag("pin_lock_screen"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App Logo
        Image(
          painter = painterResource(id = R.drawable.app_logo),
          contentDescription = "شعار التطبيق",
          modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(20.dp))
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
          text = "مدونة الحسابات",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = PrimaryText
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (isError) "رمز PIN غير صحيح، يرجى المحاولة مجدداً" else "أدخل رمز PIN لتسجيل الدخول إلى حساباتك بأمان",
          fontSize = 13.sp,
          color = if (isError) NegativeRed else SecondaryText,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // PIN Indicator Dots
        Row(
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (i in 0 until expectedPin.length) {
            val isFilled = i < enteredPin.length
            val dotColor by animateColorAsState(
              targetValue = when {
                isError -> NegativeRed
                isFilled -> RoyalBlue
                else -> BorderColor
              },
              label = "dot_color"
            )

            Box(
              modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(dotColor)
            )
          }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Keypad Grid (1-9, Fingerprint, 0, Backspace)
        Column(
          modifier = Modifier.fillMaxWidth(0.85f),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9")
          ).forEach { row ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly
            ) {
              row.forEach { digit ->
                KeypadButton(text = digit) {
                  if (enteredPin.length < expectedPin.length) {
                    enteredPin += digit
                  }
                }
              }
            }
          }

          // Bottom Keypad Row: Fingerprint, 0, Backspace
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            // Fingerprint / Biometric Button
            if (isFingerprintEnabled) {
              Box(
                modifier = Modifier
                  .size(68.dp)
                  .clip(CircleShape)
                  .background(SoftBlue)
                  .clickable { onFingerprintClick() },
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Fingerprint, contentDescription = "بصمة الإصبع", tint = RoyalBlue, modifier = Modifier.size(30.dp))
              }
            } else {
              Spacer(modifier = Modifier.size(68.dp))
            }

            KeypadButton(text = "0") {
              if (enteredPin.length < expectedPin.length) {
                enteredPin += "0"
              }
            }

            // Backspace Button
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(SecondaryBackground)
                .clickable {
                  if (enteredPin.isNotEmpty()) {
                    enteredPin = enteredPin.dropLast(1)
                  }
                },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "حذف",
                tint = PrimaryText,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun KeypadButton(
  text: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(68.dp)
      .clip(CircleShape)
      .background(MaterialTheme.colorScheme.surface)
      .clickable { onClick() }
      .testTag("keypad_$text"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = PrimaryText
    )
  }
}
