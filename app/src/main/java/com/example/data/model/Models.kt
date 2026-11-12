package com.example.data.model

data class Account(
  val accountNo: Long,
  val accountName: String,
  val ctgryNo: Long,
  val ctgryName: String = "",
  val totalBalance: Double = 0.0,
  val mobile: String = "",
  val alarmDate: String? = null,
  val alarmReqId: Int = 0,
  val alarmStatus: Boolean = false,
  val transDate: String = "",
  val creditLimit: Double = 0.0,
  val debitLimit: Double = 0.0,
  val creditLimitEnabled: Boolean = false,
  val debitLimitEnabled: Boolean = false
) {
  val isCredit: Boolean get() = totalBalance > 0.0001
  val isDebit: Boolean get() = totalBalance < -0.0001
  val isZero: Boolean get() = !isCredit && !isDebit
}

data class Category(
  val ctgryNo: Long,
  val ctgryName: String,
  val isEnabled: Boolean = true,
  val accountCount: Int = 0,
  val totalBalance: Double = 0.0
)

data class Currency(
  val cId: Long,
  val cName: String,
  val transCount: Int = 0
)

data class TransactionItem(
  val transId: Long,
  val accountNo: Long,
  val accountName: String = "",
  val amount: Double, // positive = Credit / وارد / لك, negative = Debit / صادر / عليك
  val transDate: String,
  val details: String,
  val totalBalance: Double = 0.0, // running balance
  val transferId: Long? = null,
  val transferAccountNo: Long? = null,
  val transferAccountName: String? = null,
  val imgPath: String? = null,
  val cId: Long = 1,
  val cName: String = "محلي"
) {
  val isIncoming: Boolean get() = amount > 0.0
  val isOutgoing: Boolean get() = amount < 0.0
  val isTransfer: Boolean get() = (transferId ?: 0) > 0
}

data class CurrencyBalance(
  val currencyId: Long,
  val currencyName: String,
  val balance: Double
)

data class AccountDetailsSummary(
  val account: Account,
  val openingBalance: Double = 0.0,
  val totalIncoming: Double = 0.0,
  val totalOutgoing: Double = 0.0,
  val currentBalance: Double = 0.0,
  val currencyBreakdowns: List<CurrencyBalance> = emptyList()
)

data class MonthlyStat(
  val monthKey: String, // "01-2025" or "نوفمبر"
  val monthLabel: String,
  val incoming: Double, // وارد
  val outgoing: Double, // صادر
  val net: Double       // رصيد
)

data class DashboardOverview(
  val totalBalance: Double,
  val totalCredit: Double, // لك
  val totalDebit: Double,  // عليك
  val accountsCount: Int,
  val categoriesCount: Int,
  val currenciesCount: Int,
  val transactionsCount: Int,
  val monthlyStats: List<MonthlyStat>,
  val recentTransactions: List<TransactionItem>
)

enum class VoucherType(val titleAr: String, val isCredit: Boolean) {
  RECEIPT("سند قبض", true),
  PAYMENT("سند صرف", false),
  CREDIT_NOTICE("سند إشعار دائن", true),
  DEBIT_NOTICE("سند إشعار مدين", false)
}

data class Voucher(
  val voucherNumber: String,
  val voucherType: VoucherType,
  val accountNo: Long,
  val accountName: String,
  val amount: Double,
  val currencyName: String,
  val date: String,
  val details: String
)

enum class ReportType(val titleAr: String) {
  ACCOUNT_STATEMENT("كشف حساب"),
  MONTHLY_ACCOUNT_STATEMENT("كشف حساب شهري"),
  ACCOUNTS_BALANCE_SUMMARY("ملخص أرصدة الحسابات"),
  TOTAL_REPORT("تقرير إجمالي"),
  DETAILED_MOVEMENTS("تفصيلي حركة الحسابات"),
  MONTHLY_TOTALS("إجمالي المبالغ شهرياً"),
  MONTHLY_DETAILED("تفصيلي المبالغ شهرياً")
}

data class ReportRow(
  val col1: String,
  val col2: String,
  val col3: String,
  val col4: String,
  val col5: String = "",
  val col6: String = "",
  val isPositive: Boolean? = null
)

data class ReportResult(
  val title: String,
  val accountName: String? = null,
  val categoryName: String? = null,
  val currencyName: String? = null,
  val fromDate: String? = null,
  val toDate: String? = null,
  val headers: List<String>,
  val rows: List<ReportRow>,
  val totalIncoming: Double = 0.0,
  val totalOutgoing: Double = 0.0,
  val netBalance: Double = 0.0
)

data class SecuritySettings(
  val isPinEnabled: Boolean = false,
  val pinCode: String = "",
  val isFingerprintEnabled: Boolean = false
)
