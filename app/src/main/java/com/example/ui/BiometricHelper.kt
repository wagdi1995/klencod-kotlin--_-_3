package com.example.ui

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/** مصادقة ببصمة الجهاز نفسه (نظام أندرويد) بدلاً من فتح القفل مباشرة. */
object BiometricHelper {

  private const val AUTHENTICATORS = BIOMETRIC_STRONG or BIOMETRIC_WEAK

  /** null = متاح، وإلا رسالة توضح السبب */
  fun unavailableReason(context: Context): String? {
    return when (BiometricManager.from(context).canAuthenticate(AUTHENTICATORS)) {
      BiometricManager.BIOMETRIC_SUCCESS -> null
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
        "لا توجد بصمة مسجلة في الجهاز، أضف بصمتك من إعدادات الجهاز أولاً"
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
        "هذا الجهاز لا يدعم البصمة"
      BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
        "مستشعر البصمة غير متاح حالياً"
      else -> "المصادقة البيومترية غير متاحة"
    }
  }

  fun authenticate(
    activity: FragmentActivity,
    title: String = "فتح مدونة الحسابات",
    subtitle: String = "ضع إصبعك على مستشعر البصمة",
    negativeText: String = "استخدام رمز PIN",
    onSuccess: () -> Unit,
    onFailure: (userCancelled: Boolean, message: String) -> Unit = { _, _ -> }
  ) {
    if (unavailableReason(activity) != null) {
      onFailure(false, unavailableReason(activity) ?: "")
      return
    }
    val executor = ContextCompat.getMainExecutor(activity)
    val prompt = BiometricPrompt(
      activity,
      executor,
      object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
          onSuccess()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
          val cancelled = errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
            errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
            errorCode == BiometricPrompt.ERROR_CANCELED
          onFailure(cancelled, errString.toString())
        }
      }
    )
    val info = BiometricPrompt.PromptInfo.Builder()
      .setTitle(title)
      .setSubtitle(subtitle)
      .setNegativeButtonText(negativeText)
      .setAllowedAuthenticators(AUTHENTICATORS)
      .setConfirmationRequired(false)
      .build()
    prompt.authenticate(info)
  }
}
