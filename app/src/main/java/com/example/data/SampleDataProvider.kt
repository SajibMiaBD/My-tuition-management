package com.example.data

import java.time.LocalDate

object SampleDataProvider {

    suspend fun populateSampleData(repository: TuitionRepository) {
        val today = LocalDate.now()

        // Student 1: রহিম আহমেদ - joined 3 months ago on day 15, fully paid
        val s1Start = today.minusMonths(3).withDayOfMonth(15).toString()
        val s1Id = repository.insertStudent(
            StudentEntity(
                name = "রহিম আহমেদ",
                phone = "01712345678",
                startDate = s1Start,
                monthlyFee = 1500.0,
                notes = "১০ম শ্রেণি, পদার্থ ও গণিত",
                academicYear = today.year
            )
        )
        // Payments for Rahim for completed periods
        repository.insertPayment(
            PaymentEntity(
                studentId = s1Id,
                periodIndex = 0,
                periodStartDate = today.minusMonths(3).withDayOfMonth(15).toString(),
                periodEndDate = today.minusMonths(2).withDayOfMonth(14).toString(),
                amount = 1500.0,
                paymentDate = today.minusMonths(3).withDayOfMonth(20).toString(),
                notes = "বিকাশ"
            )
        )
        repository.insertPayment(
            PaymentEntity(
                studentId = s1Id,
                periodIndex = 1,
                periodStartDate = today.minusMonths(2).withDayOfMonth(15).toString(),
                periodEndDate = today.minusMonths(1).withDayOfMonth(14).toString(),
                amount = 1500.0,
                paymentDate = today.minusMonths(2).withDayOfMonth(18).toString(),
                notes = "নগদ"
            )
        )
        repository.insertPayment(
            PaymentEntity(
                studentId = s1Id,
                periodIndex = 2,
                periodStartDate = today.minusMonths(1).withDayOfMonth(15).toString(),
                periodEndDate = today.withDayOfMonth(14).toString(),
                amount = 1500.0,
                paymentDate = today.minusMonths(1).withDayOfMonth(22).toString(),
                notes = "ক্যাশ"
            )
        )

        // Student 2: সুমাইয়া আক্তার - joined 2 months ago on day 5, 1 month due
        val s2Start = today.minusMonths(2).withDayOfMonth(5).toString()
        val s2Id = repository.insertStudent(
            StudentEntity(
                name = "সুমাইয়া আক্তার",
                phone = "01898765432",
                startDate = s2Start,
                monthlyFee = 1200.0,
                notes = "৮ম শ্রেণি, ইংরেজি ও বিজ্ঞান",
                academicYear = today.year
            )
        )
        // Only paid period 0, period 1 ended and is DUE
        repository.insertPayment(
            PaymentEntity(
                studentId = s2Id,
                periodIndex = 0,
                periodStartDate = today.minusMonths(2).withDayOfMonth(5).toString(),
                periodEndDate = today.minusMonths(1).withDayOfMonth(4).toString(),
                amount = 1200.0,
                paymentDate = today.minusMonths(2).withDayOfMonth(10).toString(),
                notes = "বিকাশ"
            )
        )

        // Student 3: তানভীর হাসান - joined 3 months ago on day 10, partially paid
        val s3Start = today.minusMonths(3).withDayOfMonth(10).toString()
        val s3Id = repository.insertStudent(
            StudentEntity(
                name = "তানভীর হাসান",
                phone = "01911223344",
                startDate = s3Start,
                monthlyFee = 2000.0,
                notes = "দ্বাদশ শ্রেণি, উচ্চতর গণিত",
                academicYear = today.year
            )
        )
        repository.insertPayment(
            PaymentEntity(
                studentId = s3Id,
                periodIndex = 0,
                periodStartDate = today.minusMonths(3).withDayOfMonth(10).toString(),
                periodEndDate = today.minusMonths(2).withDayOfMonth(9).toString(),
                amount = 2000.0,
                paymentDate = today.minusMonths(3).withDayOfMonth(12).toString(),
                notes = "ক্যাশ"
            )
        )
        // Partial payment of 1000 out of 2000 for period 1
        repository.insertPayment(
            PaymentEntity(
                studentId = s3Id,
                periodIndex = 1,
                periodStartDate = today.minusMonths(2).withDayOfMonth(10).toString(),
                periodEndDate = today.minusMonths(1).withDayOfMonth(9).toString(),
                amount = 1000.0,
                paymentDate = today.minusMonths(2).withDayOfMonth(15).toString(),
                notes = "আংশিক প্রদান (রকেট)"
            )
        )

        // Student 4: নাফিসা কামাল - new student joined 10 days ago (current period ongoing, no dues yet)
        val s4Start = today.minusDays(10).toString()
        repository.insertStudent(
            StudentEntity(
                name = "নাফিসা কামাল",
                phone = "01655443322",
                startDate = s4Start,
                monthlyFee = 1000.0,
                notes = "৭ম শ্রেণি, সাধারণ গণিত",
                academicYear = today.year
            )
        )

        // Student 5: ফারহান চৌধুরী - joined 4 months ago on day 1, 2 months due
        val s5Start = today.minusMonths(4).withDayOfMonth(1).toString()
        val s5Id = repository.insertStudent(
            StudentEntity(
                name = "ফারহান চৌধুরী",
                phone = "01788776655",
                startDate = s5Start,
                monthlyFee = 1800.0,
                notes = "৯ম শ্রেণি, রসায়ন ও পদার্থ",
                academicYear = today.year
            )
        )
        repository.insertPayment(
            PaymentEntity(
                studentId = s5Id,
                periodIndex = 0,
                periodStartDate = today.minusMonths(4).withDayOfMonth(1).toString(),
                periodEndDate = today.minusMonths(3).withDayOfMonth(today.minusMonths(3).lengthOfMonth()).toString(),
                amount = 1800.0,
                paymentDate = today.minusMonths(4).withDayOfMonth(5).toString(),
                notes = "ক্যাশ"
            )
        )
        repository.insertPayment(
            PaymentEntity(
                studentId = s5Id,
                periodIndex = 1,
                periodStartDate = today.minusMonths(3).withDayOfMonth(1).toString(),
                periodEndDate = today.minusMonths(2).withDayOfMonth(today.minusMonths(2).lengthOfMonth()).toString(),
                amount = 1800.0,
                paymentDate = today.minusMonths(3).withDayOfMonth(6).toString(),
                notes = "বিকাশ"
            )
        )
    }
}
