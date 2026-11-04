package com.example.util

import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object BengaliFormatter {

    private val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBengaliDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(banglaDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Converts Bengali digits (০-৯) to English ASCII digits (0-9).
     */
    fun toEnglishDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            val idx = banglaDigits.indexOf(ch)
            if (idx != -1) {
                sb.append(('0' + idx))
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Parses amount from string accepting English (5000) or Bengali (৫০০০) digits,
     * stripping currency symbols, commas, and whitespace safely.
     */
    fun parseAmount(input: String?): Double? {
        if (input.isNullOrBlank()) return null
        val normalized = toEnglishDigits(input)
            .replace("৳", "")
            .replace(",", "")
            .trim()
        return normalized.toDoubleOrNull()
    }

    fun toBengaliDigits(number: Long): String = toBengaliDigits(number.toString())
    fun toBengaliDigits(number: Int): String = toBengaliDigits(number.toString())

    fun formatDigits(number: Int, lang: AppLanguage = AppLanguage.BN): String {
        return if (lang == AppLanguage.BN) toBengaliDigits(number) else number.toString()
    }

    fun formatDigits(number: Long, lang: AppLanguage = AppLanguage.BN): String {
        return if (lang == AppLanguage.BN) toBengaliDigits(number) else number.toString()
    }

    fun formatDigits(str: String, lang: AppLanguage = AppLanguage.BN): String {
        return if (lang == AppLanguage.BN) toBengaliDigits(str) else str
    }

    /**
     * Formats currency as "৳ ১,০০০" (BN) or "৳ 1,000" (EN)
     */
    fun formatCurrency(amount: Double, useBanglaDigits: Boolean = true): String {
        val formatter = DecimalFormat("#,##0")
        val formattedNumber = formatter.format(amount)
        val numStr = if (useBanglaDigits) toBengaliDigits(formattedNumber) else formattedNumber
        return "৳ $numStr"
    }

    fun formatCurrency(amount: Double, lang: AppLanguage): String {
        return formatCurrency(amount, useBanglaDigits = (lang == AppLanguage.BN))
    }

    /**
     * Formats date as DD-MM-YYYY (e.g. 15-01-2026 or ১৫-০১-২০২৬)
     */
    fun formatDateDdMmYyyy(date: LocalDate, useBanglaDigits: Boolean = false): String {
        val day = String.format(Locale.US, "%02d", date.dayOfMonth)
        val month = String.format(Locale.US, "%02d", date.monthValue)
        val year = date.year.toString()
        val result = "$day-$month-$year"
        return if (useBanglaDigits) toBengaliDigits(result) else result
    }

    fun formatDateDdMmYyyy(date: LocalDate, lang: AppLanguage): String {
        return formatDateDdMmYyyy(date, useBanglaDigits = (lang == AppLanguage.BN))
    }

    /**
     * Formats date with month name, e.g. "১৫ জানুয়ারি ২০২৬" or "15 January 2026"
     */
    fun formatDateWithMonthName(date: LocalDate, lang: AppLanguage = AppLanguage.BN): String {
        return if (lang == AppLanguage.BN) {
            val monthsBn = arrayOf(
                "", "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
                "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
            )
            val dayBn = toBengaliDigits(date.dayOfMonth.toString())
            val monthBn = monthsBn.getOrElse(date.monthValue) { "" }
            val yearBn = toBengaliDigits(date.year.toString())
            "$dayBn $monthBn $yearBn"
        } else {
            val monthsEn = arrayOf(
                "", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
            val monthEn = monthsEn.getOrElse(date.monthValue) { "" }
            "${date.dayOfMonth} $monthEn ${date.year}"
        }
    }

    /**
     * Formats date period range, e.g. "15-01-2026 থেকে 14-02-2026" or "15-01-2026 to 14-02-2026"
     */
    fun formatPeriodRange(startDate: LocalDate, endDate: LocalDate, lang: AppLanguage = AppLanguage.BN): String {
        val startStr = formatDateDdMmYyyy(startDate, lang == AppLanguage.BN)
        val endStr = formatDateDdMmYyyy(endDate, lang == AppLanguage.BN)
        val separator = if (lang == AppLanguage.BN) "থেকে" else "to"
        return "$startStr $separator $endStr"
    }

    /**
     * Short day of week: সোম, মঙ্গল, বুধ, বৃহঃ, শুক্র, শনি, রবি / Mon, Tue, Wed, Thu, Fri, Sat, Sun
     */
    fun formatShortDayOfWeek(date: LocalDate, lang: AppLanguage = AppLanguage.BN): String {
        return if (lang == AppLanguage.BN) {
            when (date.dayOfWeek.value) {
                1 -> "সোম"
                2 -> "মঙ্গল"
                3 -> "বুধ"
                4 -> "বৃহঃ"
                5 -> "শুক্র"
                6 -> "শনি"
                7 -> "রবি"
                else -> ""
            }
        } else {
            when (date.dayOfWeek.value) {
                1 -> "Mon"
                2 -> "Tue"
                3 -> "Wed"
                4 -> "Thu"
                5 -> "Fri"
                6 -> "Sat"
                7 -> "Sun"
                else -> ""
            }
        }
    }

    /**
     * Full day of week: সোমবার, মঙ্গলবার ... / Monday, Tuesday ...
     */
    fun formatFullDayOfWeek(date: LocalDate, lang: AppLanguage = AppLanguage.BN): String {
        return if (lang == AppLanguage.BN) {
            when (date.dayOfWeek.value) {
                1 -> "সোমবার"
                2 -> "মঙ্গলবার"
                3 -> "বুধবার"
                4 -> "বৃহস্পতিবার"
                5 -> "শুক্রবার"
                6 -> "শনিবার"
                7 -> "রবিবার"
                else -> ""
            }
        } else {
            when (date.dayOfWeek.value) {
                1 -> "Monday"
                2 -> "Tuesday"
                3 -> "Wednesday"
                4 -> "Thursday"
                5 -> "Friday"
                6 -> "Saturday"
                7 -> "Sunday"
                else -> ""
            }
        }
    }
}
