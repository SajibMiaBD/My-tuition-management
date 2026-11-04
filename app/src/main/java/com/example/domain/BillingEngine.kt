package com.example.domain

import com.example.data.PaymentEntity
import com.example.data.StudentEntity
import java.time.LocalDate
import java.time.format.DateTimeParseException

object BillingEngine {

    /**
     * Parse date safely from ISO format YYYY-MM-DD
     */
    fun parseDate(dateStr: String, fallback: LocalDate = LocalDate.now()): LocalDate {
        return try {
            LocalDate.parse(dateStr)
        } catch (e: Exception) {
            fallback
        }
    }

    /**
     * Calculates the start and end date for a given billing period index.
     * Period 0: startDate to (startDate + 1 month - 1 day)
     * Period 1: (startDate + 1 month) to (startDate + 2 months - 1 day)
     * and so on.
     */
    fun getPeriodDateRange(startDate: LocalDate, periodIndex: Int): Pair<LocalDate, LocalDate> {
        val pStart = startDate.plusMonths(periodIndex.toLong())
        val pEnd = startDate.plusMonths((periodIndex + 1).toLong()).minusDays(1)
        return Pair(pStart, pEnd)
    }

    /**
     * Automatically generates all billing periods for a student up to today (including ongoing period),
     * and accurately calculates paid, due, and remaining amounts according to billing rules.
     */
    /**
     * Parse pause records JSON
     */
    fun parsePauseHistory(json: String?): List<PauseRecord> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val array = org.json.JSONArray(json)
            val list = mutableListOf<PauseRecord>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    PauseRecord(
                        id = obj.optLong("id", System.currentTimeMillis()),
                        pauseDate = obj.getString("pauseDate"),
                        resumeDate = if (obj.isNull("resumeDate")) null else obj.optString("resumeDate", null),
                        daysPaused = obj.optLong("daysPaused", 0L),
                        note = obj.optString("note", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Format pause records to JSON
     */
    fun formatPauseHistory(records: List<PauseRecord>): String {
        val array = org.json.JSONArray()
        for (r in records) {
            val obj = org.json.JSONObject()
            obj.put("id", r.id)
            obj.put("pauseDate", r.pauseDate)
            obj.put("resumeDate", r.resumeDate ?: org.json.JSONObject.NULL)
            obj.put("daysPaused", r.daysPaused)
            obj.put("note", r.note)
            array.put(obj)
        }
        return array.toString()
    }

    /**
     * Parse custom period starts JSON into a map of periodIndex to start date string
     */
    fun parseCustomPeriodStarts(json: String?): Map<Int, String> {
        if (json.isNullOrBlank() || json.trim() == "{}") return emptyMap()
        return try {
            val map = mutableMapOf<Int, String>()
            val trimmed = json.trim().removePrefix("{").removeSuffix("}").trim()
            if (trimmed.isNotEmpty()) {
                val entries = trimmed.split(",")
                for (entry in entries) {
                    val parts = entry.split(":")
                    if (parts.size == 2) {
                        val key = parts[0].trim().replace("\"", "").toIntOrNull()
                        val value = parts[1].trim().replace("\"", "")
                        if (key != null && value.isNotEmpty()) {
                            map[key] = value
                        }
                    }
                }
            }
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }

    /**
     * Format custom period starts map to JSON
     */
    fun formatCustomPeriodStarts(map: Map<Int, String>): String {
        if (map.isEmpty()) return "{}"
        val entries = map.entries.joinToString(",") { "\"${it.key}\":\"${it.value}\"" }
        return "{$entries}"
    }

    /**
     * Automatically generates all billing periods for a student up to today,
     * taking into account bill pause & resume cycles (> 3 days creates new month cycle),
     * custom period start date overrides (which shift subsequent cycles accordingly),
     * and accurately calculates paid, due, and remaining amounts according to billing rules.
     */
    fun computeStudentSummary(
        student: StudentEntity,
        studentPayments: List<PaymentEntity>,
        today: LocalDate = LocalDate.now(),
        selectedYear: Int? = null
    ): StudentBillingSummary {
        val startDate = parseDate(student.startDate, today)
        val pauseRecords = parsePauseHistory(student.pauseHistoryJson)
        val customStarts = parseCustomPeriodStarts(student.customPeriodStartsJson)

        // Filter pauses that lasted more than 3 days and were resumed
        val completedPauses = pauseRecords
            .filter { it.resumeDate != null && it.daysPaused > 3 }
            .sortedBy { parseDate(it.pauseDate, today) }

        // Define active billing segments
        data class BillingSegment(val start: LocalDate, val endBoundary: LocalDate?)
        val segments = mutableListOf<BillingSegment>()

        if (completedPauses.isEmpty()) {
            val endBoundary = if (student.isPaused && !student.pausedDate.isNullOrBlank()) {
                parseDate(student.pausedDate, today)
            } else null
            segments.add(BillingSegment(startDate, endBoundary))
        } else {
            var currentStart = startDate
            for (p in completedPauses) {
                val pDate = parseDate(p.pauseDate, today)
                segments.add(BillingSegment(currentStart, pDate))
                currentStart = parseDate(p.resumeDate!!, today)
            }
            val finalBoundary = if (student.isPaused && !student.pausedDate.isNullOrBlank()) {
                parseDate(student.pausedDate, today)
            } else null
            segments.add(BillingSegment(currentStart, finalBoundary))
        }

        val maxPaidIndex = studentPayments.maxOfOrNull { it.periodIndex } ?: -1
        val periods = mutableListOf<BillingPeriod>()
        var globalIndex = 0

        val maxYearLimit = if (selectedYear != null) LocalDate.of(selectedYear, 12, 31) else null
        val effectiveEvaluationDate = if (selectedYear != null && selectedYear < today.year) {
            LocalDate.of(selectedYear, 12, 31)
        } else {
            today
        }

        for (segment in segments) {
            val segStart = segment.start
            val boundary = segment.endBoundary
            var currentBaseStart = segStart
            var baseMonthOffset = 0

            while (true) {
                // If there is a custom start date override for this period, shift base start date
                if (customStarts.containsKey(globalIndex)) {
                    val customDate = parseDate(customStarts[globalIndex]!!, currentBaseStart)
                    currentBaseStart = customDate
                    baseMonthOffset = 0
                }

                val pStart = currentBaseStart.plusMonths(baseMonthOffset.toLong())
                val pEnd = currentBaseStart.plusMonths((baseMonthOffset + 1).toLong()).minusDays(1)

                // If segment has a pause boundary, do not generate periods that start after pause date
                if (boundary != null && pStart.isAfter(boundary) && globalIndex > maxPaidIndex) {
                    break
                }

                // If bounded by selectedYear (e.g. past year), stop when pStart exceeds year limit unless paid
                if (maxYearLimit != null && pStart.isAfter(maxYearLimit) && globalIndex > maxPaidIndex) {
                    break
                }

                // If no pause boundary (active segment), stop if starts in the future without payments
                if (boundary == null && globalIndex > 0 && pStart.isAfter(effectiveEvaluationDate) && globalIndex > maxPaidIndex) {
                    break
                }

                // A billing period becomes DUE only AFTER its end date has passed.
                // If currently paused before period end, ongoing period does not become overdue.
                val hasEnded = effectiveEvaluationDate.isAfter(pEnd) && (!student.isPaused || boundary == null || !effectiveEvaluationDate.isAfter(boundary) || !pEnd.isAfter(boundary))

                val periodPayments = studentPayments.filter { it.periodIndex == globalIndex }
                val paidAmount = periodPayments.sumOf { it.amount }

                val status = when {
                    paidAmount >= student.monthlyFee -> PaymentStatus.PAID
                    paidAmount > 0 -> PaymentStatus.PARTIALLY_PAID
                    hasEnded -> PaymentStatus.DUE
                    else -> PaymentStatus.UPCOMING
                }

                val overdueAmount = if (hasEnded) {
                    maxOf(0.0, student.monthlyFee - paidAmount)
                } else {
                    0.0
                }
                val remainingAmount = maxOf(0.0, student.monthlyFee - paidAmount)

                periods.add(
                    BillingPeriod(
                        index = globalIndex,
                        studentId = student.id,
                        periodNumber = globalIndex + 1,
                        startDate = pStart,
                        endDate = pEnd,
                        fee = student.monthlyFee,
                        paidAmount = paidAmount,
                        hasEnded = hasEnded,
                        status = status,
                        overdueAmount = overdueAmount,
                        remainingAmount = remainingAmount,
                        payments = periodPayments
                    )
                )

                globalIndex++
                baseMonthOffset++
                if (globalIndex > 240) break
            }
        }

        // Identify current period
        val currentPeriod = periods.find { !today.isBefore(it.startDate) && !today.isAfter(it.endDate) }
            ?: periods.lastOrNull()

        val totalPaid = studentPayments.sumOf { it.amount }
        val totalDue = periods.sumOf { it.overdueAmount }
        val duePeriodsCount = periods.count { it.hasEnded && it.overdueAmount > 0.0 }
        val isFullyPaid = totalDue == 0.0
        val hasPartialDue = periods.any { it.hasEnded && it.status == PaymentStatus.PARTIALLY_PAID }

        val lastPayment = studentPayments.maxByOrNull { it.paymentDate }

        return StudentBillingSummary(
            student = student,
            periods = periods,
            payments = studentPayments,
            totalPaid = totalPaid,
            totalDue = totalDue,
            duePeriodsCount = duePeriodsCount,
            currentPeriod = currentPeriod,
            isFullyPaid = isFullyPaid,
            hasPartialDue = hasPartialDue,
            lastPayment = lastPayment,
            isPaused = student.isPaused,
            pausedDate = student.pausedDate,
            pauseRecords = pauseRecords
        )
    }

    /**
     * Compute dashboard statistics across all students, optionally isolated for a selected financial year.
     * When selectedYear is specified, all calculations are strictly isolated so no other year's data leaks into it.
     */
    fun computeDashboardStats(
        studentSummaries: List<StudentBillingSummary>,
        allPayments: List<PaymentEntity>,
        today: LocalDate = LocalDate.now(),
        selectedYear: Int? = null
    ): DashboardStats {
        val totalStudents = studentSummaries.size
        val monthlyExpectedIncome = studentSummaries.sumOf { it.student.monthlyFee }

        val targetYear = selectedYear ?: today.year
        val currentYearMonth = "${targetYear}-${String.format(java.util.Locale.US, "%02d", today.monthValue)}"

        val paymentsInYear = allPayments.filter {
            try {
                LocalDate.parse(it.paymentDate).year == targetYear
            } catch (e: Exception) {
                false
            }
        }
        val paymentsReceivedInYear = paymentsInYear.sumOf { it.amount }
        val paymentsReceivedThisMonth = paymentsInYear
            .filter { it.paymentDate.startsWith(currentYearMonth) }
            .sumOf { it.amount }

        val periodsInTarget = if (selectedYear == null) {
            studentSummaries.flatMap { it.periods }
        } else {
            studentSummaries.flatMap { summary ->
                summary.periods.filter { it.endDate.year == selectedYear }
            }
        }
        val totalOutstandingDue = periodsInTarget.sumOf { it.overdueAmount }

        val studentsWithDueCount = if (selectedYear == null) {
            studentSummaries.count { it.totalDue > 0.0 }
        } else {
            studentSummaries.count { summary ->
                summary.periods.any { it.endDate.year == selectedYear && it.overdueAmount > 0.0 }
            }
        }

        val fullyPaidStudentsCount = if (selectedYear == null) {
            studentSummaries.count { it.totalDue == 0.0 }
        } else {
            studentSummaries.count { summary ->
                summary.periods.none { it.endDate.year == selectedYear && it.overdueAmount > 0.0 }
            }
        }

        return DashboardStats(
            totalStudents = totalStudents,
            monthlyExpectedIncome = monthlyExpectedIncome,
            paymentsReceivedThisMonth = paymentsReceivedThisMonth,
            paymentsReceivedInYear = paymentsReceivedInYear,
            totalOutstandingDue = totalOutstandingDue,
            studentsWithDueCount = studentsWithDueCount,
            fullyPaidStudentsCount = fullyPaidStudentsCount
        )
    }

    /**
     * Compute monthly collected tuition fees for the 12 months of a specific year.
     * Ensures completely isolated collection graph strictly for the chosen year.
     */
    fun computeYearlyMonthlyCollections(
        allPayments: List<PaymentEntity>,
        year: Int
    ): List<MonthlyCollection> {
        val shortMonthsBn = arrayOf(
            "", "জানু", "ফেব্রু", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টে", "অক্টো", "নভে", "ডিসে"
        )
        val shortMonthsEn = arrayOf(
            "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        val paymentsByMonth = mutableMapOf<Int, Double>()
        for (payment in allPayments) {
            try {
                val date = LocalDate.parse(payment.paymentDate)
                if (date.year == year) {
                    paymentsByMonth[date.monthValue] = (paymentsByMonth[date.monthValue] ?: 0.0) + payment.amount
                }
            } catch (_: Exception) {}
        }

        val result = mutableListOf<MonthlyCollection>()
        for (mVal in 1..12) {
            val ym = java.time.YearMonth.of(year, mVal)
            val amount = paymentsByMonth[mVal] ?: 0.0
            val labelBn = shortMonthsBn.getOrElse(mVal) { "" }
            val labelEn = shortMonthsEn.getOrElse(mVal) { "" }
            result.add(
                MonthlyCollection(
                    yearMonth = ym,
                    amount = amount,
                    labelBn = labelBn,
                    labelEn = labelEn
                )
            )
        }
        return result
    }

    /**
     * Compute monthly collected tuition fees over the last 6 months
     */
    fun computeLast6MonthsCollections(
        allPayments: List<PaymentEntity>,
        today: LocalDate = LocalDate.now()
    ): List<MonthlyCollection> {
        return computeYearlyMonthlyCollections(allPayments, today.year)
    }
}
