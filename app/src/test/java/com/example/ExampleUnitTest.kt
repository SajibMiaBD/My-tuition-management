package com.example

import com.example.data.PaymentEntity
import com.example.data.StudentEntity
import com.example.domain.BillingEngine
import com.example.domain.PaymentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {

  @Test
  fun testBillingPeriodCalculationMatchesPromptExample() {
    val startDate = LocalDate.of(2026, 1, 15)

    // Period 1: 15 Jan 2026 - 14 Feb 2026
    val (p1Start, p1End) = BillingEngine.getPeriodDateRange(startDate, 0)
    assertEquals(LocalDate.of(2026, 1, 15), p1Start)
    assertEquals(LocalDate.of(2026, 2, 14), p1End)

    // Period 2: 15 Feb 2026 - 14 Mar 2026
    val (p2Start, p2End) = BillingEngine.getPeriodDateRange(startDate, 1)
    assertEquals(LocalDate.of(2026, 2, 15), p2Start)
    assertEquals(LocalDate.of(2026, 3, 14), p2End)

    // Period 3: 15 Mar 2026 - 14 Apr 2026
    val (p3Start, p3End) = BillingEngine.getPeriodDateRange(startDate, 2)
    assertEquals(LocalDate.of(2026, 3, 15), p3Start)
    assertEquals(LocalDate.of(2026, 4, 14), p3End)

    // Period 4: 15 Apr 2026 - 14 May 2026
    val (p4Start, p4End) = BillingEngine.getPeriodDateRange(startDate, 3)
    assertEquals(LocalDate.of(2026, 4, 15), p4Start)
    assertEquals(LocalDate.of(2026, 5, 14), p4End)
  }

  @Test
  fun testBillingRulesOngoingNotOverdue() {
    val student = StudentEntity(
      id = 1L,
      name = "রহিম",
      startDate = "2026-01-15",
      monthlyFee = 1000.0
    )

    // Suppose today is 2026-01-25 (mid-period 1)
    val midPeriod1 = LocalDate.of(2026, 1, 25)
    val summaryMid = BillingEngine.computeStudentSummary(student, emptyList(), midPeriod1)

    assertEquals(1, summaryMid.periods.size)
    val p1 = summaryMid.periods[0]
    assertFalse("Ongoing period must not have ended", p1.hasEnded)
    assertEquals(PaymentStatus.UPCOMING, p1.status)
    assertEquals(0.0, p1.overdueAmount, 0.001)
    assertEquals(0.0, summaryMid.totalDue, 0.001)

    // Suppose today is 2026-02-15 (period 1 ended yesterday on 14 Feb)
    val afterPeriod1 = LocalDate.of(2026, 2, 15)
    val summaryAfter = BillingEngine.computeStudentSummary(student, emptyList(), afterPeriod1)

    assertEquals(2, summaryAfter.periods.size)
    val p1After = summaryAfter.periods[0]
    assertTrue("Period 1 must have ended", p1After.hasEnded)
    assertEquals(PaymentStatus.DUE, p1After.status)
    assertEquals(1000.0, p1After.overdueAmount, 0.001)

    val p2Ongoing = summaryAfter.periods[1]
    assertFalse("Period 2 is ongoing, must not have ended", p2Ongoing.hasEnded)
    assertEquals(PaymentStatus.UPCOMING, p2Ongoing.status)
    assertEquals(0.0, p2Ongoing.overdueAmount, 0.001)

    // Total due is ONLY from period 1
    assertEquals(1000.0, summaryAfter.totalDue, 0.001)
  }

  @Test
  fun testPaymentStatusTransitions() {
    val student = StudentEntity(
      id = 1L,
      name = "করিম",
      startDate = "2026-01-15",
      monthlyFee = 1000.0
    )
    val today = LocalDate.of(2026, 3, 1)

    // Partial payment for period 0 (ended), full payment for period 1 (ended)
    val payments = listOf(
      PaymentEntity(studentId = 1L, periodIndex = 0, periodStartDate = "2026-01-15", periodEndDate = "2026-02-14", amount = 600.0, paymentDate = "2026-01-20"),
      PaymentEntity(studentId = 1L, periodIndex = 1, periodStartDate = "2026-02-15", periodEndDate = "2026-03-14", amount = 1000.0, paymentDate = "2026-02-18")
    )

    val summary = BillingEngine.computeStudentSummary(student, payments, today)
    val p0 = summary.periods.find { it.index == 0 }!!
    assertEquals(PaymentStatus.PARTIALLY_PAID, p0.status)
    assertEquals(400.0, p0.overdueAmount, 0.001)

    val p1 = summary.periods.find { it.index == 1 }!!
    assertEquals(PaymentStatus.PAID, p1.status)
    assertEquals(0.0, p1.overdueAmount, 0.001)

    assertEquals(400.0, summary.totalDue, 0.001)

    // Verify last payment date is captured
    assertEquals("2026-02-18", summary.lastPayment?.paymentDate)
    assertEquals(1000.0, summary.lastPayment?.amount ?: 0.0, 0.001)

    // Verify period payments list
    assertEquals(1, p0.payments.size)
    assertEquals("2026-01-20", p0.payments[0].paymentDate)
  }

  @Test
  fun testYearlyMonthlyCollections() {
    val payments = listOf(
      PaymentEntity(id = 1L, studentId = 1L, periodIndex = 0, periodStartDate = "2026-01-15", periodEndDate = "2026-02-14", amount = 1500.0, paymentDate = "2026-01-20"),
      PaymentEntity(id = 2L, studentId = 1L, periodIndex = 1, periodStartDate = "2026-02-15", periodEndDate = "2026-03-14", amount = 2500.0, paymentDate = "2026-02-22")
    )
    val collections = BillingEngine.computeYearlyMonthlyCollections(payments, 2026)
    assertEquals(12, collections.size)
    assertEquals(1500.0, collections[0].amount, 0.001)
    assertEquals("Jan", collections[0].labelEn)
    assertEquals("Feb", collections[1].labelEn)
    assertEquals(2500.0, collections[1].amount, 0.001)
    assertEquals(0.0, collections[2].amount, 0.001)
  }
}

