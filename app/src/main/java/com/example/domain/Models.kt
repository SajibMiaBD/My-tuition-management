package com.example.domain

import androidx.compose.ui.graphics.Color
import com.example.data.PaymentEntity
import com.example.data.StudentEntity
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusDueContainer
import com.example.ui.theme.StatusDueText
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.StatusPaidContainer
import com.example.ui.theme.StatusPaidText
import com.example.ui.theme.StatusPartial
import com.example.ui.theme.StatusPartialContainer
import com.example.ui.theme.StatusPartialText
import com.example.ui.theme.StatusUpcoming
import com.example.ui.theme.StatusUpcomingContainer
import com.example.ui.theme.StatusUpcomingText
import java.time.LocalDate

enum class PaymentStatus(
    val labelBn: String,
    val color: Color,
    val containerColor: Color,
    val textColor: Color
) {
    PAID("পরিশোধিত", StatusPaid, StatusPaidContainer, StatusPaidText),
    PARTIALLY_PAID("আংশিক পরিশোধিত", StatusPartial, StatusPartialContainer, StatusPartialText),
    DUE("বকেয়া", StatusDue, StatusDueContainer, StatusDueText),
    UPCOMING("চলতি / আসন্ন", StatusUpcoming, StatusUpcomingContainer, StatusUpcomingText)
}

enum class StudentFilter(val labelBn: String, val labelEn: String) {
    ALL("সকল শিক্ষার্থী", "All Students"),
    PAID("পরিশোধিত", "Paid"),
    PENDING("পেন্ডিং", "Pending"),
    OVERDUE("বকেয়া", "Overdue");

    companion object {
        val DUE = OVERDUE
    }
}

data class BillingPeriod(
    val index: Int,
    val studentId: Long,
    val periodNumber: Int, // 1-based (Period 1, 2, 3...)
    val startDate: LocalDate,
    val endDate: LocalDate,
    val fee: Double,
    val paidAmount: Double,
    val hasEnded: Boolean,
    val status: PaymentStatus,
    val overdueAmount: Double,
    val remainingAmount: Double,
    val payments: List<PaymentEntity> = emptyList()
)

data class PauseRecord(
    val id: Long = System.currentTimeMillis(),
    val pauseDate: String,          // ISO YYYY-MM-DD
    val resumeDate: String? = null, // null if currently active pause
    val daysPaused: Long = 0,
    val note: String = ""
)

data class StudentBillingSummary(
    val student: StudentEntity,
    val periods: List<BillingPeriod>,
    val payments: List<PaymentEntity>,
    val totalPaid: Double,
    val totalDue: Double,
    val duePeriodsCount: Int,
    val currentPeriod: BillingPeriod?,
    val isFullyPaid: Boolean,
    val hasPartialDue: Boolean,
    val lastPayment: PaymentEntity? = null,
    val isPaused: Boolean = student.isPaused,
    val pausedDate: String? = student.pausedDate,
    val pauseRecords: List<PauseRecord> = emptyList()
)

data class PaymentConfirmation(
    val studentName: String,
    val amount: Double,
    val paymentDate: String,
    val periodRange: String,
    val notes: String
)

data class DashboardStats(
    val totalStudents: Int = 0,
    val monthlyExpectedIncome: Double = 0.0,
    val paymentsReceivedThisMonth: Double = 0.0,
    val paymentsReceivedInYear: Double = 0.0,
    val totalOutstandingDue: Double = 0.0,
    val studentsWithDueCount: Int = 0,
    val fullyPaidStudentsCount: Int = 0
)

data class MonthlyCollection(
    val yearMonth: java.time.YearMonth,
    val amount: Double,
    val labelBn: String,
    val labelEn: String
)
