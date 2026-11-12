package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val ArabicFontFamily = FontFamily(
  Font(R.font.noto_sans_arabic, FontWeight.Normal),
  Font(R.font.noto_sans_arabic, FontWeight.Medium),
  Font(R.font.cairo_black, FontWeight.Bold),
  Font(R.font.cairo_black, FontWeight.ExtraBold)
)

val Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    color = PrimaryText
  ),
  displayMedium = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    color = PrimaryText
  ),
  headlineLarge = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    color = PrimaryText
  ),
  headlineMedium = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 28.sp,
    color = PrimaryText
  ),
  headlineSmall = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    color = PrimaryText
  ),
  titleLarge = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 17.sp,
    lineHeight = 24.sp,
    color = PrimaryText
  ),
  titleMedium = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 15.sp,
    lineHeight = 22.sp,
    color = PrimaryText
  ),
  titleSmall = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    color = SecondaryText
  ),
  bodyLarge = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp,
    color = PrimaryText
  ),
  bodyMedium = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    color = SecondaryText
  ),
  bodySmall = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    color = SecondaryText
  ),
  labelLarge = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    color = PrimaryText
  ),
  labelMedium = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    color = SecondaryText
  ),
  labelSmall = TextStyle(
    fontFamily = ArabicFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    color = SecondaryText
  )
)
