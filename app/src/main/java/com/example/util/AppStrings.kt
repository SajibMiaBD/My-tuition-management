package com.example.util

import com.example.domain.PaymentStatus
import com.example.domain.StudentFilter

enum class AppLanguage(val code: String, val displayNameBn: String, val displayNameEn: String) {
    BN("bn", "বাংলা", "Bangla"),
    EN("en", "English", "English")
}

object Strings {

    fun navDashboard(lang: AppLanguage) = if (lang == AppLanguage.BN) "ড্যাশবোর্ড" else "Dashboard"
    fun navStudents(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থী" else "Students"
    fun navPayments(lang: AppLanguage) = if (lang == AppLanguage.BN) "পেমেন্ট" else "Payments"
    fun navDues(lang: AppLanguage) = if (lang == AppLanguage.BN) "বকেয়া" else "Dues"
    fun navSettings(lang: AppLanguage) = if (lang == AppLanguage.BN) "সেটিংস" else "Settings"
    fun studentDetailsTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থীর বিবরণ" else "Student Details"

    // Dashboard
    fun welcomeTeacher(lang: AppLanguage) = if (lang == AppLanguage.BN) "স্বাগতম, শিক্ষক মহাশয় 👋" else "Welcome, Teacher 👋"
    fun dashboardSubtitle(lang: AppLanguage, dateFormatted: String) =
        if (lang == AppLanguage.BN) "আজ $dateFormatted • টিউশন হিসাব বিবরণী" else "Today $dateFormatted • Tuition Financials"
    fun newStudent(lang: AppLanguage) = if (lang == AppLanguage.BN) "নতুন শিক্ষার্থী" else "Add Student"
    fun recordPayment(lang: AppLanguage) = if (lang == AppLanguage.BN) "পেমেন্ট গ্রহণ" else "Receive Payment"
    fun totalStudents(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোট শিক্ষার্থী" else "Total Students"
    fun monthlyExpectedIncome(lang: AppLanguage) = if (lang == AppLanguage.BN) "প্রত্যাশিত মাসিক আয়" else "Monthly Expected Income"
    fun paymentsReceivedThisMonth(lang: AppLanguage) = if (lang == AppLanguage.BN) "চলতি মাসে আদায়কৃত" else "Received This Month"
    fun collectionInThisMonth(lang: AppLanguage) = if (lang == AppLanguage.BN) "চলতি মাসের আদায়" else "Collection in this month"
    fun tapToViewWhoPaid(lang: AppLanguage) = if (lang == AppLanguage.BN) "কে কে দিল দেখতে ট্যাপ করুন" else "Tap to see who paid"
    fun thisMonthCollectionTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "চলতি মাসের আদায়ের তালিকা" else "Collection in this month"
    fun noPaymentsThisMonthMsg(lang: AppLanguage) = if (lang == AppLanguage.BN) "এই মাসে এখনও কোনো শিক্ষার্থী পেমেন্ট দেয়নি" else "No payments received in this month yet"
    fun payingStudentsListLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "পরিশোধকারী শিক্ষার্থীদের তালিকা:" else "Students who paid this month:"
    fun totalCollectedThisMonthBadge(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোট আদায়" else "Total Collected"

    // Attendance Strings
    fun studentAttendanceTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থীদের উপস্থিতি" else "Student Attendance"
    fun attendanceSubtitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "তারিখভিত্তিক হাজিরা ও উপস্থিতি চার্ট" else "Daily Attendance & Visual Chart"
    fun attendanceCardTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থীদের উপস্থিতি" else "Student Attendance"
    fun attendanceCardSubtitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "হাজিরা দিতে ও চার্ট দেখতে ট্যাপ করুন" else "Tap to mark & view chart"
    fun presentCountUnit(lang: AppLanguage, present: String, total: String) =
        if (lang == AppLanguage.BN) "উপস্থিত: $present/$total জন" else "Present: $present/$total"
    fun attendanceRateUnit(lang: AppLanguage, rate: String) =
        if (lang == AppLanguage.BN) "$rate% উপস্থিতি" else "$rate% Present"
    fun markAllPresent(lang: AppLanguage) = if (lang == AppLanguage.BN) "সবাই উপস্থিত" else "Mark All Present"
    fun markAllAbsent(lang: AppLanguage) = if (lang == AppLanguage.BN) "সবাই অনুপস্থিত" else "Mark All Absent"
    fun presentLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "উপস্থিত" else "Present"
    fun absentLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "অনুপস্থিত" else "Absent"
    fun unmarkedLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "অনির্ধারিত" else "Unmarked"
    fun attendanceVisualChart(lang: AppLanguage) = if (lang == AppLanguage.BN) "উপস্থিতি ভিজ্যুয়াল অ্যানালিটিক্স" else "Attendance Visual Analytics"
    fun attendanceChartSubtitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "ট্রেন্ড গ্রাফ, ক্যালেন্ডার হিটম্যাপ ও শিক্ষার্থী র‍্যাঙ্কিং" else "Trend Graph, Calendar Heatmap & Student Ranking"
    fun attendanceHistoryLog(lang: AppLanguage) = if (lang == AppLanguage.BN) "তারিখভিত্তিক উপস্থিতির ইতিহাস" else "Date-wise Attendance History"
    fun last7Days(lang: AppLanguage) = if (lang == AppLanguage.BN) "গত ৭ দিন" else "Last 7 Days"
    fun last14Days(lang: AppLanguage) = if (lang == AppLanguage.BN) "গত ১৪ দিন" else "Last 14 Days"
    fun thisMonthTab(lang: AppLanguage) = if (lang == AppLanguage.BN) "চলতি মাস" else "This Month"
    fun allRecordsRange(lang: AppLanguage) = if (lang == AppLanguage.BN) "সব রেকর্ড" else "All Records"
    fun trendViewTab(lang: AppLanguage) = if (lang == AppLanguage.BN) "ট্রেন্ড বার" else "Trend Bar"
    fun heatmapViewTab(lang: AppLanguage) = if (lang == AppLanguage.BN) "হিটম্যাপ" else "Heatmap"
    fun studentRankingTab(lang: AppLanguage) = if (lang == AppLanguage.BN) "র‍্যাঙ্কিং" else "Rankings"
    fun avgAttendanceRateLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "গড় উপস্থিতি" else "Avg Attendance"
    fun totalClassesHeldLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোট হাজিরা দিন" else "Total Sessions"
    fun presentRatioLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "উপস্থিত বনাম অনুপস্থিত" else "Present vs Absent"
    fun inspectDayTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "নির্বাচিত দিনের বিস্তারিত" else "Selected Day Details"
    fun loadAndEditDayButton(lang: AppLanguage) = if (lang == AppLanguage.BN) "এই দিনের হাজিরা এডিট করুন" else "Edit This Day's Attendance"
    fun attendanceStatusExcellent(lang: AppLanguage) = if (lang == AppLanguage.BN) "চমৎকার (৮৫%+)" else "Excellent (85%+)"
    fun attendanceStatusGood(lang: AppLanguage) = if (lang == AppLanguage.BN) "ভালো (৭০-৮৪%)" else "Good (70-84%)"
    fun attendanceStatusAverage(lang: AppLanguage) = if (lang == AppLanguage.BN) "চলনসই (৫০-৬৯%)" else "Average (50-69%)"
    fun attendanceStatusNeedsAttention(lang: AppLanguage) = if (lang == AppLanguage.BN) "মনোযোগ প্রয়োজন (<৫০%)" else "Needs Attention (<50%)"
    fun regularStudentBadge(lang: AppLanguage) = if (lang == AppLanguage.BN) "নিয়মিত" else "Regular"
    fun irregularStudentBadge(lang: AppLanguage) = if (lang == AppLanguage.BN) "অনিয়মিত" else "Irregular"
    fun noAttendanceRecords(lang: AppLanguage) = if (lang == AppLanguage.BN) "এখনও কোনো উপস্থিতি রেকর্ড করা হয়নি।" else "No attendance records recorded yet."
    fun attendanceTabLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "উপস্থিতি" else "Attendance"
    fun totalPresentDays(lang: AppLanguage, count: String) = if (lang == AppLanguage.BN) "উপস্থিত: $count দিন" else "Present: $count days"
    fun totalAbsentDays(lang: AppLanguage, count: String) = if (lang == AppLanguage.BN) "অনুপস্থিত: $count দিন" else "Absent: $count days"
    fun attendanceRateLabel(lang: AppLanguage, rate: String) = if (lang == AppLanguage.BN) "উপস্থিতির হার: $rate%" else "Attendance Rate: $rate%"
    fun markAttendanceForToday(lang: AppLanguage) = if (lang == AppLanguage.BN) "আজকের উপস্থিতি দিন:" else "Mark Today's Attendance:"
    fun totalOutstandingDue(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোট বকেয়া টাকা" else "Total Outstanding Due"
    fun studentsWithDue(lang: AppLanguage) = if (lang == AppLanguage.BN) "বকেয়া শিক্ষার্থী" else "Students with Due"
    fun fullyPaidStudents(lang: AppLanguage) = if (lang == AppLanguage.BN) "পরিশোধিত শিক্ষার্থী" else "Fully Paid Students"
    fun reminderNeeded(lang: AppLanguage) = if (lang == AppLanguage.BN) "তাগাদা প্রয়োজন" else "Reminder Needed"
    fun noDues(lang: AppLanguage) = if (lang == AppLanguage.BN) "কোন বকেয়া নেই" else "No Dues"
    fun regularPayers(lang: AppLanguage) = if (lang == AppLanguage.BN) "নিয়মিত প্রদানকারী" else "Regular Payers"
    fun duesList(lang: AppLanguage, countStr: String) = if (lang == AppLanguage.BN) "বকেয়া তালিকা ($countStr জন)" else "Dues List ($countStr)"
    fun viewAllDues(lang: AppLanguage) = if (lang == AppLanguage.BN) "সকল বকেয়া দেখুন" else "View All Dues"
    fun viewAllPayments(lang: AppLanguage) = if (lang == AppLanguage.BN) "সকল পেমেন্ট দেখুন" else "View All Payments"
    fun recentPayments(lang: AppLanguage) = if (lang == AppLanguage.BN) "সর্বশেষ আদায়কৃত পেমেন্ট" else "Recent Payments"
    fun noPaymentHistory(lang: AppLanguage) = if (lang == AppLanguage.BN) "কোন পেমেন্ট হিস্ট্রি নেই" else "No Payment History"
    fun noPaymentHistorySubtitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "নতুন পেমেন্ট রেকর্ড করতে 'পেমেন্ট গ্রহণ' বাটনে চাপুন।" else "Tap 'Receive Payment' to record a new payment."
    fun monthsDueSuffix(lang: AppLanguage, countStr: String) =
        if (lang == AppLanguage.BN) "$countStr মাসের বকেয়া" else "$countStr months due"
    fun dueLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "বকেয়া" else "Due"
    fun paidLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "পরিশোধিত" else "Paid"
    fun studentUnit(lang: AppLanguage, countStr: String) =
        if (lang == AppLanguage.BN) "$countStr জন" else "$countStr students"

    // Bar Chart
    fun chartTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "বিগত ৬ মাসের ফি আদায়" else "Fee Collection (Last 6 Months)"
    fun chartSubtitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "মাসিক আদায়ের ভিজ্যুয়াল বার চার্ট" else "Monthly fee collection bar chart"
    fun chartTotalCollection(lang: AppLanguage, amountStr: String) =
        if (lang == AppLanguage.BN) "৬ মাসে মোট আদায়: $amountStr" else "6-Month Total: $amountStr"
    fun chartEmptyNotice(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পেমেন্ট রেকর্ড যুক্ত হলে এখানে প্রতি মাসের বার চার্ট দৃশ্যমান হবে।" else "Monthly collection bars will appear here as payments are received."

    // Students
    fun searchStudentPlaceholder(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "শিক্ষার্থীর নাম বা ফোন নম্বর দিয়ে খুঁজুন..." else "Search by student name or phone..."
    fun filterAll(lang: AppLanguage) = if (lang == AppLanguage.BN) "সকল শিক্ষার্থী" else "All Students"
    fun filterDue(lang: AppLanguage) = if (lang == AppLanguage.BN) "বকেয়া শিক্ষার্থী" else "Due Students"
    fun filterPaid(lang: AppLanguage) = if (lang == AppLanguage.BN) "পরিশোধিত" else "Fully Paid"
    fun filterPartial(lang: AppLanguage) = if (lang == AppLanguage.BN) "আংশিক পরিশোধিত" else "Partially Paid"
    fun totalShowing(lang: AppLanguage, countStr: String) =
        if (lang == AppLanguage.BN) "মোট দেখানো হচ্ছে: $countStr জন" else "Showing total: $countStr"
    fun noStudentsFound(lang: AppLanguage) = if (lang == AppLanguage.BN) "কোন শিক্ষার্থী পাওয়া যায়নি" else "No Students Found"
    fun studentListEmpty(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থীর তালিকা খালি" else "Student List is Empty"
    fun tryOtherName(lang: AppLanguage) = if (lang == AppLanguage.BN) "অন্য নাম দিয়ে খুঁজে দেখুন" else "Try searching with another name"
    fun tapFabToAdd(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "নিচের '+' বাটনে ট্যাপ করে নতুন শিক্ষার্থী যোগ করুন।" else "Tap '+' button below to add a student."
    fun monthlyFee(lang: AppLanguage) = if (lang == AppLanguage.BN) "মাসিক ফি" else "Monthly Fee"
    fun startDate(lang: AppLanguage) = if (lang == AppLanguage.BN) "শুরুর তারিখ" else "Start Date"
    fun lastPaymentDateLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "🗓️ সর্বশেষ পেমেন্টের তারিখ:" else "🗓️ Last Payment Date:"
    fun phoneLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "ফোন:" else "Phone:"
    fun editInfo(lang: AppLanguage) = if (lang == AppLanguage.BN) "তথ্য সংশোধন করুন" else "Edit Information"
    fun deleteStudent(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থী মুছে ফেলুন" else "Delete Student"
    fun call(lang: AppLanguage) = if (lang == AppLanguage.BN) "কল" else "Call"
    fun message(lang: AppLanguage) = if (lang == AppLanguage.BN) "এসএমএস" else "SMS"

    // Student Details
    fun totalPaid(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোট পরিশোধিত" else "Total Paid"
    fun paymentCountLabel(lang: AppLanguage, countStr: String) =
        if (lang == AppLanguage.BN) "মোট $countStr টি পেমেন্ট জমা" else "Total $countStr payments received"
    fun noPaymentsYet(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "কোন পেমেন্ট নেই" else "No payments yet"
    fun currentOngoingPeriod(lang: AppLanguage, numStr: String) =
        if (lang == AppLanguage.BN) "বর্তমান চলতি পিরিয়ড (পিরিয়ড $numStr)" else "Current Ongoing Period (Period $numStr)"
    fun feeColon(lang: AppLanguage) = if (lang == AppLanguage.BN) "ফি:" else "Fee:"
    fun paidColon(lang: AppLanguage) = if (lang == AppLanguage.BN) "প্রদান:" else "Paid:"
    fun remainingColon(lang: AppLanguage) = if (lang == AppLanguage.BN) "বাকি:" else "Remaining:"
    fun ongoingRuleNotice(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "💡 নিয়ম: শেষ তারিখ পার না হওয়া পর্যন্ত চলতি মাস বকেয়া ধরা হয় না।" else "💡 Rule: An ongoing month is not marked overdue until its end date passes."
    fun paidDateNotice(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "✅ পরিশোধের তারিখ:" else "✅ Payment Date:"
    fun monthlyBillingHistory(lang: AppLanguage, countStr: String) =
        if (lang == AppLanguage.BN) "মাসিক বিলিং হিস্ট্রি ($countStr)" else "Monthly Billing History ($countStr)"
    fun paymentRecords(lang: AppLanguage, countStr: String) =
        if (lang == AppLanguage.BN) "পেমেন্ট রেকর্ড ($countStr)" else "Payment Records ($countStr)"
    fun periodTitle(lang: AppLanguage, numStr: String) =
        if (lang == AppLanguage.BN) "পিরিয়ড $numStr" else "Period $numStr"
    fun paymentDatesSectionTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পেমেন্ট পরিশোধের তারিখ ও মাধ্যম:" else "Payment Dates & Method:"
    fun dateColon(lang: AppLanguage) = if (lang == AppLanguage.BN) "তারিখ:" else "Date:"
    fun methodColon(lang: AppLanguage) = if (lang == AppLanguage.BN) "মাধ্যম:" else "Method:"
    fun takePayment(lang: AppLanguage) = if (lang == AppLanguage.BN) "পেমেন্ট নিন" else "Take Payment"
    fun submitPayment(lang: AppLanguage) = if (lang == AppLanguage.BN) "পেমেন্ট জমা দিন" else "Submit Payment"
    fun studentNotFound(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থী পাওয়া যায়নি" else "Student Not Found"
    fun back(lang: AppLanguage) = if (lang == AppLanguage.BN) "পিছনে যান" else "Back"

    // Edit Period Dialog
    fun editPeriodDialogTitle(lang: AppLanguage, periodNum: String) =
        if (lang == AppLanguage.BN) "পিরিয়ড $periodNum এর তথ্য ও পেমেন্ট এডিট" else "Edit Period $periodNum Details & Payment"
    fun studyDatesSection(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পড়ার সময়কাল (তারিখ)" else "Study Period Dates"
    fun studyStartDateLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পড়ার শুরুর তারিখ" else "Study Start Date"
    fun studyEndDateLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পড়ার শেষের তারিখ" else "Study End Date"
    fun subsequentCycleNotice(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "💡 এই পিরিয়ডের শুরুর তারিখ পরিবর্তন করলে এই পিরিয়ড এবং এর পরবর্তী সকল পিরিয়ডের পড়ার মাসও সেই অনুযায়ী শুরু হবে।"
        else "💡 Changing this period's start date will shift the study cycle for this and all subsequent periods accordingly."
    fun paymentDetailsSection(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পেমেন্টের হিসাব ও এডিট" else "Payment Details & Edits"
    fun paymentAmountLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "কত টাকা পেমেন্ট করেছে" else "Amount Paid"
    fun paymentDateLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "কত তারিখে পেমেন্ট করেছে" else "Payment Date"
    fun paymentNotesLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পদ্ধতি / নোট (ঐচ্ছিক)" else "Method / Note (Optional)"
    fun addNewPaymentBtn(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "➕ এই পিরিয়ডে নতুন পেমেন্ট যোগ করুন" else "➕ Add New Payment for This Period"
    fun savePeriodChanges(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পরিবর্তন সংরক্ষণ করুন" else "Save Changes"
    fun tapToEditPeriodHint(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "ট্যাপ করে তারিখ ও পেমেন্ট এডিট করুন" else "Tap to edit dates & payments"
    fun paymentEntryTitle(lang: AppLanguage, index: String) =
        if (lang == AppLanguage.BN) "পেমেন্ট #$index" else "Payment #$index"
    fun noPaymentsForThisPeriod(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "এই পিরিয়ডের কোনো পেমেন্ট রেকর্ড নেই।" else "No payment records found for this period."
    fun deletePaymentAction(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পেমেন্ট মুছুন" else "Delete Payment"
    fun editPeriodAction(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "এডিট করুন" else "Edit Period"

    // Payments Screen
    fun collectedThisMonth(lang: AppLanguage) = if (lang == AppLanguage.BN) "চলতি মাসে আদায়" else "This Month's Collection"
    fun totalCollected(lang: AppLanguage) = if (lang == AppLanguage.BN) "সর্বমোট আদায়" else "Total Collection"
    fun tabThisMonth(lang: AppLanguage) = if (lang == AppLanguage.BN) "এই মাসের আদায়" else "This Month"
    fun tabAllYears(lang: AppLanguage) = if (lang == AppLanguage.BN) "সকল বছরের আদায়" else "All Years"
    fun thisMonthListSubtitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "এই মাসে যারা পেমেন্ট করেছে তাদের তারিখসহ তালিকা" else "Chronological list of payments received this month"
    fun allYearsListSubtitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "কার পর কে পেমেন্ট করেছে তার ক্রমানুসারী তালিকা" else "Sequential history: Who paid after whom across all years"
    fun paymentSeqLabel(lang: AppLanguage, numStr: String) = if (lang == AppLanguage.BN) "নং $numStr" else "#$numStr"
    fun searchPaymentPlaceholder(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "শিক্ষার্থীর নাম বা পেমেন্ট নোট দিয়ে খুঁজুন..." else "Search by student name or notes..."
    fun noPaymentMatched(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "কোন পেমেন্ট রেকর্ড মেলেনি" else "No Matching Payments Found"
    fun noPaymentThisMonth(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "চলতি মাসে এখনো কোন পেমেন্ট আদায় হয়নি" else "No payments received in this month yet"
    fun noPaymentAllYears(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "এখনো কোন পেমেন্ট রেকর্ড করা হয়নি" else "No payments recorded yet"

    // Dues Screen
    fun totalOutstandingDueBanner(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "মোট অনাদায়ী বকেয়া" else "Total Outstanding Dues"
    fun studentsWithDueCountSubtitle(lang: AppLanguage, countStr: String) =
        if (lang == AppLanguage.BN) "$countStr জন শিক্ষার্থীর বকেয়া রয়েছে" else "$countStr student(s) have pending dues"
    fun duesStudentListTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "বকেয়া শিক্ষার্থীদের তালিকা" else "Overdue Students List"
    fun congratulationsNoDues(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "কোন বকেয়া নেই!" else "No Dues Pending!"
    fun congratulationsSubtitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "অভিনন্দন! সকল শিক্ষার্থীর টিউশন ফি সম্পূর্ণ পরিশোধিত রয়েছে।" else "Congratulations! All students' tuition fees are fully cleared."
    fun sendReminderSms(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "তাগাদা" else "Reminder"
    fun smsBodyTemplate(lang: AppLanguage, studentName: String, dueAmount: String) =
        if (lang == AppLanguage.BN)
            "শ্রদ্ধেয় অভিভাবক, $studentName-এর টিউশন ফি বাবদ $dueAmount টাকা বকেয়া রয়েছে। অনুগ্রহ করে সুবিধাজনক সময়ে পরিশোধের অনুরোধ করছি। ধন্যবাদ।"
        else
            "Dear Guardian, Tuition fee of $dueAmount BDT is pending for $studentName. Please arrange the payment at your convenience. Thank you."

    // Settings Screen
    fun settingsTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "সেটিংস" else "Settings"
    fun appVersionSubtitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "টিউশন ও ফি হিসাব ব্যবস্থাপক • সংস্করণ ১.০" else "Tuition & Fee Manager • Version 1.0"
    fun languageSelectionTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "অ্যাপের ভাষা পরিবর্তন (Language)" else "App Language Selection"
    fun automaticBillingRules(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "স্বয়ংক্রিয় মাসিক বিলিং নিয়মাবলী" else "Automatic Monthly Billing Rules"
    fun rule1Title(lang: AppLanguage) = if (lang == AppLanguage.BN) "১. ব্যক্তিগত শুরুর তারিখ" else "1. Individual Start Date"
    fun rule1Desc(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "প্রতিটি শিক্ষার্থীর বিলিং পিরিয়ড তার যোগদানের তারিখ থেকে আলাদাভাবে হিসাব করা হয় (যেমন: ১৫ জানুয়ারি থেকে ১৪ ফেব্রুয়ারি)।"
        else "Each student's monthly billing period is calculated from their individual start date (e.g., 15 Jan - 14 Feb)."
    fun rule2Title(lang: AppLanguage) = if (lang == AppLanguage.BN) "২. শেষ তারিখের হিসাব" else "2. Exact End Date"
    fun rule2Desc(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "প্রতিটি বিলিং পিরিয়ড পরবর্তী মাসের একই তারিখের ঠিক আগের দিন শেষ হয়। বিভিন্ন দিনের মাস ও লিপ ইয়ার নির্ভুলভাবে সংরক্ষিত থাকে।"
        else "Each billing period ends one day prior to the same date next month. Leap years and varied month lengths are handled accurately."
    fun rule3Title(lang: AppLanguage) = if (lang == AppLanguage.BN) "৩. বকেয়ার নিখুঁত শর্ত" else "3. Overdue Criterion"
    fun rule3Desc(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "একটি বিলিং পিরিয়ড কেবল তখনই বকেয়া (DUE) হবে যখন এর সমাপ্তির তারিখ অতিবাহিত হয়ে যাবে। চলতি অসম্পূর্ণ মাস কখনো বকেয়া হিসেবে গণ্য হয় না।"
        else "A billing period becomes DUE strictly after its end date has passed. An ongoing incomplete period is never counted as overdue."
    fun rule4Title(lang: AppLanguage) = if (lang == AppLanguage.BN) "৪. পেমেন্ট স্ট্যাটাস" else "4. Payment Statuses"
    fun rule4Desc(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "সবুজ = সম্পূর্ণ পরিশোধিত, কমলা = আংশিক পরিশোধিত, লাল = বকেয়া, নীল = চলতি/আসন্ন মাস।"
        else "Green = Fully Paid, Orange = Partially Paid, Red = Overdue, Blue = Ongoing / Upcoming."
    fun appDatabaseSettings(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "অ্যাপ ও ডেটাবেজ সেটিংস" else "App & Database Settings"
    fun currencySymbol(lang: AppLanguage) = if (lang == AppLanguage.BN) "মুদ্রা প্রতীক" else "Currency Symbol"
    fun currencyValue(lang: AppLanguage) = if (lang == AppLanguage.BN) "বাংলাদেশি টাকা (৳)" else "Bangladeshi Taka (৳)"
    fun dateFormatLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "তারিখ ফরম্যাট" else "Date Format"
    fun databaseStorageLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "ডাটাবেজ স্টোরেজ" else "Database Storage"
    fun databaseStorageValue(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "স্থায়ী অফলাইন Room DB" else "Persistent Offline Room DB"
    fun dataManagement(lang: AppLanguage) = if (lang == AppLanguage.BN) "ডেটা ব্যবস্থাপনা" else "Data Management"
    fun loadSampleData(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "নমুনা ডেমো তথ্য লোড করুন" else "Load Demo Sample Data"
    fun clearAllData(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "সকল শিক্ষার্থী ও পেমেন্ট তথ্য মুছুন" else "Clear All Students & Payment Data"
    fun confirmClearTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "সব তথ্য মুছে ফেলতে চান?" else "Delete all data?"
    fun confirmClearText(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "এই অপশনটি নির্বাচন করলে সকল শিক্ষার্থী এবং তাদের সমস্ত পেমেন্ট রেকর্ড স্থায়ীভাবে মুছে যাবে।"
        else "Selecting this option will permanently delete all students and all payment records."
    fun confirmReloadTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "নমুনা তথ্য লোড করবেন?" else "Load sample data?"
    fun confirmReloadText(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "এটি বর্তমান ডেটাবেজ পরিষ্কার করে নমুনা শিক্ষার্থী ও বিভিন্ন পিরিয়ডের পেমেন্ট রেকর্ড যুক্ত করবে।"
        else "This will clear current database and load sample students with multi-period payment records."
    // Backup & Restore
    fun backupAndRestoreSectionTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "স্টোরেজ ব্যাকআপ ও রিস্টোর (Import / Export)" else "Storage Backup & Restore (Import / Export)"
    fun exportBackupButton(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "স্টোরেজে ব্যাকআপ সেভ করুন" else "Save Backup to Phone Storage"
    fun exportBackupSubtitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "সকল শিক্ষার্থী ও পেমেন্ট রেকর্ড JSON ফাইলে এক্সপোর্ট করুন" else "Export all student & payment records to a JSON file"
    fun importBackupButton(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "স্টোরেজ থেকে রিস্টোর করুন" else "Import Backup from Storage"
    fun importBackupSubtitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পূর্বে সংরক্ষিত JSON ফাইল থেকে সব তথ্য ফেরত আনুন" else "Restore data from a previously saved JSON file"
    fun shareBackupButton(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "ব্যাকআপ ফাইল শেয়ার করুন" else "Share Backup File"
    fun shareBackupSubtitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "ড্রাইভ, হোয়াটসঅ্যাপ বা অন্য অ্যাপে ব্যাকআপ পাঠান" else "Send backup to Drive, WhatsApp or other apps"

    // Google Backup
    fun googleBackupTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "গুগল ব্যাকআপ" else "Google backup"
    fun googleBackupConnected(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "সংযুক্ত" else "Connected"
    fun googleBackupNotConnected(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "জিমেইল সিলেক্ট করুন" else "Select Gmail"
    fun googleBackupDesc(lang: AppLanguage) =
        if (lang == AppLanguage.BN)
            "আপনার জিমেইল অ্যাকাউন্টের মাধ্যমে অ্যাপের সমস্ত ডেটা গুগলে সুরক্ষিত রাখুন। ফোন পরিবর্তন বা নতুন করে ইনস্টল করলেও আগের সব ডেটা ফেরত পাবেন।"
        else
            "Safely backup all app data to Google with your Gmail account. Restore your data anytime if you change devices or reinstall."
    fun connectedGmailLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "সংযুক্ত জিমেইল:" else "Connected Gmail:"
    fun lastBackupLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "সর্বশেষ ব্যাকআপ:" else "Last Backup:"
    fun neverBackedUp(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "এখনো ব্যাকআপ করা হয়নি" else "Never backed up"
    fun selectGmailButton(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "জিমেইল সিলেক্ট করুন" else "Select Gmail"
    fun changeGmailButton(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পরিবর্তন" else "Change"
    fun backupNowGoogleButton(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "এখন ব্যাকআপ করুন" else "Backup Now"
    fun restoreFromGoogleButton(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "গুগল থেকে রিস্টোর" else "Restore from Google"
    fun saveToGoogleDriveButton(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "গুগল ড্রাইভে ফাইল সেভ" else "Save to Google Drive"
    fun selectGmailDialogTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "গুগল অ্যাকাউন্ট / জিমেইল সিলেক্ট করুন" else "Select Google / Gmail Account"
    fun selectGmailDialogDesc(lang: AppLanguage) =
        if (lang == AppLanguage.BN)
            "আপনার টিউশন ডেটা ও পেমেন্ট হিস্ট্রি গুগলে সংরক্ষণ করতে জিমেইল নির্বাচন করুন:"
        else
            "Select your Gmail account to save your tuition data and payment history to Google:"
    fun suggestedAccountLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "প্রস্তাবিত অ্যাকাউন্ট (১-ট্যাপে নির্বাচন)" else "Suggested Account (1-Tap Select)"
    fun enterGmailLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "জিমেইল অ্যাড্রেস লিখুন" else "Enter Gmail Address"
    fun invalidEmailError(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "সঠিক জিমেইল অ্যাড্রেস দিন (যেমন user@gmail.com)" else "Enter a valid Gmail address (e.g. user@gmail.com)"
    fun connectAndBackupButton(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "কানেক্ট ও ব্যাকআপ করুন" else "Connect & Backup"
    fun googleBackupSuccess(lang: AppLanguage, email: String) =
        if (lang == AppLanguage.BN) "$email অ্যাকাউন্টে ডেটা সফলভাবে ব্যাকআপ করা হয়েছে!" else "Data successfully backed up to $email!"
    fun googleNoBackupFound(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "এই অ্যাকাউন্টের জন্য কোনো পূর্ববর্তী ক্লাউড ব্যাকআপ পাওয়া যায়নি!" else "No previous cloud backup found for this account!"

    // Theme Change
    fun themeChangeTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "থিম পরিবর্তন" else "Theme Change"
    fun themeChangeSubtitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "১০টি প্রিমিয়াম লাক্সারি কালার ও ফন্ট স্টাইল" else "10 Premium luxury colors & typography styles"
    fun themeSelectDialogTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "প্রিমিয়াম থিম নির্বাচন করুন" else "Select Premium Theme"
    fun activeThemeLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "বর্তমান সক্রিয় থিম" else "Currently Active Theme"
    fun themeAppliedNotification(lang: AppLanguage, themeName: String) =
        if (lang == AppLanguage.BN) "'$themeName' থিম সফলভাবে সক্রিয় করা হয়েছে!" else "'$themeName' theme applied successfully!"

    // Import Preview Dialog
    fun importDialogTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "ব্যাকআপ ফাইল পাওয়া গেছে" else "Backup File Detected"
    fun importFileStats(lang: AppLanguage, exportDate: String, studentCount: String, paymentCount: String) =
        if (lang == AppLanguage.BN)
            "ব্যাকআপ তারিখ: $exportDate\nশিক্ষার্থী: $studentCount জন\nপেমেন্ট রেকর্ড: $paymentCount টি\n\nআপনি কীভাবে এই ডেটা রিস্টোর করতে চান?"
        else
            "Backup Date: $exportDate\nStudents: $studentCount\nPayment Records: $paymentCount\n\nHow would you like to restore this data?"
    fun optionReplaceAll(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "সব মুছে প্রতিস্থাপন (Replace)" else "Replace All (Clean)"
    fun optionMergeData(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "বর্তমান তথ্যে যুক্ত করুন (Merge)" else "Merge with Existing"

    // Notifications
    fun exportSuccessMessage(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "ব্যাকআপ সফলভাবে ফোনে সংরক্ষণ করা হয়েছে!" else "Backup successfully saved to storage!"
    fun exportEmptyWarning(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "এক্সপোর্ট করার মতো কোনো শিক্ষার্থী বা পেমেন্ট নেই!" else "No students or payments to export!"
    fun importSuccessMessage(lang: AppLanguage, sCount: String, pCount: String) =
        if (lang == AppLanguage.BN)
            "সফলভাবে $sCount জন শিক্ষার্থী ও $pCount টি পেমেন্ট রেকর্ড রিস্টোর করা হয়েছে!"
        else
            "Successfully restored $sCount student(s) and $pCount payment(s)!"
    fun importErrorMessage(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "ব্যাকআপ ফাইল পড়তে ব্যর্থ হয়েছে! ফাইলটি সঠিক কিনা যাচাই করুন।" else "Failed to read backup file! Please verify the file format."

    fun load(lang: AppLanguage) = if (lang == AppLanguage.BN) "লোড করুন" else "Load"
    fun delete(lang: AppLanguage) = if (lang == AppLanguage.BN) "মুছে ফেলুন" else "Delete"
    fun cancel(lang: AppLanguage) = if (lang == AppLanguage.BN) "বাতিল" else "Cancel"
    fun save(lang: AppLanguage) = if (lang == AppLanguage.BN) "সংরক্ষণ করুন" else "Save"
    fun update(lang: AppLanguage) = if (lang == AppLanguage.BN) "আপডেট করুন" else "Update"
    fun ok(lang: AppLanguage) = if (lang == AppLanguage.BN) "ঠিক আছে" else "OK"

    // Dialogs
    fun addNewStudentTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "নতুন শিক্ষার্থী যোগ করুন" else "Add New Student"
    fun editStudentTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থীর তথ্য পরিবর্তন" else "Edit Student Information"
    fun studentNameRequired(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থীর নাম *" else "Student Name *"
    fun nameRequiredError(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থীর নাম আবশ্যক" else "Student name is required"
    fun phoneOptional(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোবাইল নম্বর (ঐচ্ছিক)" else "Mobile Number (Optional)"
    fun tuitionStartDateRequired(lang: AppLanguage) = if (lang == AppLanguage.BN) "টিউশন শুরুর তারিখ *" else "Tuition Start Date *"
    fun monthlyTuitionFeeRequired(lang: AppLanguage) = if (lang == AppLanguage.BN) "মাসিক টিউশন ফি (টাকা) *" else "Monthly Tuition Fee (BDT) *"
    fun feeRequiredError(lang: AppLanguage) = if (lang == AppLanguage.BN) "সঠিক ফি লিখুন" else "Enter a valid fee"
    fun notesOptional(lang: AppLanguage) = if (lang == AppLanguage.BN) "নোট / শ্রেণি / বিষয় (ঐচ্ছিক)" else "Notes / Class / Subject (Optional)"

    fun recordPaymentTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "পেমেন্ট গ্রহণ করুন" else "Receive Payment"
    fun selectStudentRequired(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থী নির্বাচন করুন *" else "Select Student *"
    fun selectBillingPeriodRequired(lang: AppLanguage) = if (lang == AppLanguage.BN) "বিলিং পিরিয়ড নির্বাচন করুন *" else "Select Billing Period *"
    fun paymentAmountRequired(lang: AppLanguage) = if (lang == AppLanguage.BN) "টাকার পরিমাণ (BDT) *" else "Payment Amount (BDT) *"
    fun amountRequiredError(lang: AppLanguage) = if (lang == AppLanguage.BN) "সঠিক পরিমাণ লিখুন" else "Enter a valid amount"
    fun periodRemainingHelper(lang: AppLanguage, amountStr: String) =
        if (lang == AppLanguage.BN) "এই পিরিয়ডে বাকি: $amountStr" else "Remaining in this period: $amountStr"
    fun fullRemainingButton(lang: AppLanguage) = if (lang == AppLanguage.BN) "সম্পূর্ণ বাকি" else "Full Due"
    fun halfButton(lang: AppLanguage) = if (lang == AppLanguage.BN) "অর্ধেক" else "Half"
    fun paymentDateRequired(lang: AppLanguage) = if (lang == AppLanguage.BN) "পেমেন্টের তারিখ *" else "Payment Date *"
    fun paymentMethodNotes(lang: AppLanguage) = if (lang == AppLanguage.BN) "পেমেন্ট মাধ্যম / নোট" else "Payment Method / Notes"
    fun notesPlaceholder(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "নোট লিখুন (যেমন: ট্রানজেকশন আইডি বা মন্তব্য)" else "Enter notes (e.g. Transaction ID or remarks)"
    fun addPaymentButton(lang: AppLanguage) = if (lang == AppLanguage.BN) "পেমেন্ট যুক্ত করুন" else "Add Payment"

    fun paymentSuccessReceiptTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পেমেন্ট সফলভাবে গৃহীত হয়েছে!" else "Payment Recorded Successfully!"
    fun paymentDateReceiptLabel(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পেমেন্ট পরিশোধের তারিখ" else "Payment Date"
    fun studentLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থী:" else "Student:"
    fun receivedAmountLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "গৃহীত টাকা:" else "Amount Received:"
    fun billingPeriodLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "বিলিং পিরিয়ড:" else "Billing Period:"
    fun paymentMethodLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "পেমেন্ট মাধ্যম:" else "Payment Method:"

    fun deleteStudentConfirmTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থী মুছে ফেলবেন?" else "Delete student?"
    fun deleteStudentConfirmText(lang: AppLanguage, studentName: String) =
        if (lang == AppLanguage.BN) "'$studentName'-এর সমস্ত তথ্য ও তার সমস্ত পেমেন্ট রেকর্ড স্থায়ীভাবে মুছে ফেলা হবে।"
        else "All information and all payment records of '$studentName' will be permanently deleted."
    fun deletePaymentConfirmTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "পেমেন্ট রেকর্ড মুছবেন?" else "Delete payment record?"
    fun deletePaymentConfirmText(lang: AppLanguage, amountStr: String) =
        if (lang == AppLanguage.BN) "$amountStr-এর এই পেমেন্ট এন্ট্রিটি মুছে ফেলতে চান?"
        else "Do you want to delete this payment entry of $amountStr?"

    // Status translations
    fun statusLabel(status: PaymentStatus, lang: AppLanguage): String {
        return if (lang == AppLanguage.BN) {
            status.labelBn
        } else {
            when (status) {
                PaymentStatus.PAID -> "Paid"
                PaymentStatus.PARTIALLY_PAID -> "Partially Paid"
                PaymentStatus.DUE -> "Due"
                PaymentStatus.UPCOMING -> "Ongoing / Upcoming"
            }
        }
    }

    // Filter translations
    fun filterLabel(filter: StudentFilter, lang: AppLanguage): String {
        return if (lang == AppLanguage.BN) {
            filter.labelBn
        } else {
            filter.labelEn
        }
    }

    fun pendingLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "পেন্ডিং" else "Pending"
    fun overdueLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "বকেয়া" else "Overdue"

    // Quick payment methods
    fun quickPaymentMethods(lang: AppLanguage): List<String> {
        return if (lang == AppLanguage.BN) {
            listOf("ক্যাশ", "বিকাশ", "নগদ", "রকেট", "ব্যাংক")
        } else {
            listOf("Cash", "bKash", "Nagad", "Rocket", "Bank")
        }
    }

    // Bill Pause & Resume Strings
    fun billPausedBadge(lang: AppLanguage) = if (lang == AppLanguage.BN) "বিল স্থগিত (Paused)" else "Bill Paused"
    fun billActiveBadge(lang: AppLanguage) = if (lang == AppLanguage.BN) "বিল সক্রিয় (Active)" else "Bill Active"
    fun pauseBillButton(lang: AppLanguage) = if (lang == AppLanguage.BN) "বিল স্থগিত করুন" else "Pause Bill"
    fun resumeBillButton(lang: AppLanguage) = if (lang == AppLanguage.BN) "বিল চালু করুন" else "Resume Bill"
    fun pauseDetailsButton(lang: AppLanguage) = if (lang == AppLanguage.BN) "পাউসের বিবরণ" else "Pause Details"
    fun pauseDetailsTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "স্থগিতের বিস্তারিত ইতিহাস" else "Pause & Resume History"
    fun confirmPauseTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "বিল স্থগিত (Pause) করবেন?" else "Pause Student Billing?"
    fun confirmPauseMsg(lang: AppLanguage, name: String) =
        if (lang == AppLanguage.BN) "$name-এর বিল স্থগিত রাখলে এই সময়ের মধ্যে কোনো নতুন বিল তৈরি হবে না। পুনরায় চালু করলে সেই তারিখ থেকে নতুন মাস হিসাব করা হবে।"
        else "Pausing billing for $name stops future bills. Resuming will start a new billing month from the resume date."
    fun confirmResumeTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "বিল পুনরায় চালু করবেন?" else "Resume Student Billing?"
    fun pauseDateLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "স্থগিতের তারিখ:" else "Pause Date:"
    fun resumeDateLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "চালুর তারিখ:" else "Resume Date:"
    fun durationPausedLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "মোট স্থগিত সময়:" else "Paused Duration:"
    fun daysUnit(lang: AppLanguage, count: String) = if (lang == AppLanguage.BN) "$count দিন" else "$count days"
    fun pauseRule3DaysNotice(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "✓ ৩ দিনের বেশি স্থগিত থাকায় এই বিরতি ইতিহাসে সংরক্ষিত হবে এবং চালুর তারিখ থেকে নতুন মাস শুরু হবে।"
        else "✓ Paused for more than 3 days. A new billing month starts from resume date and record is saved."
    fun pauseRuleShortNotice(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "ℹ️ ৩ দিন বা তার কম সময় স্থগিত থাকায় বিল সাইকেল অপরিবর্তিত থাকবে।"
        else "ℹ️ Paused for 3 days or less. Billing cycle continues without interruption."
    fun noPauseHistoryYet(lang: AppLanguage) = if (lang == AppLanguage.BN) "৩ দিনের বেশি কোনো স্থগিতের ইতিহাস নেই।" else "No pause records (over 3 days) yet."
    fun activePauseBanner(lang: AppLanguage, date: String) =
        if (lang == AppLanguage.BN) "⚠️ এই শিক্ষার্থীর বিল $date থেকে স্থগিত (Paused) রয়েছে।"
        else "⚠️ This student's billing is paused since $date."

    // Year Selector Strings
    fun yearSelectorTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "হিসাবের বছর নির্বাচন" else "Select Accounting Year"
    fun selectAccountingYearLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "হিসাব বর্ষ নির্বাচন" else "Select Accounting Year"
    fun selectYearDialogTitle(lang: AppLanguage) = if (lang == AppLanguage.BN) "হিসাব বর্ষ নির্বাচন করুন (২০০০-২০৯৯)" else "Select Accounting Year (2000-2099)"
    fun searchYearPlaceholder(lang: AppLanguage) = if (lang == AppLanguage.BN) "সাল খুঁজুন (যেমন: ২০২৬)..." else "Search year (e.g. 2026)..."
    fun currentYearTag(lang: AppLanguage) = if (lang == AppLanguage.BN) "চলতি সাল" else "Current Year"
    fun yearNotice(lang: AppLanguage, yearStr: String) =
        if (lang == AppLanguage.BN) "শুধুমাত্র $yearStr সালের সম্পূর্ণ আলাদা হিসাব ও আদায় (অন্য বছরের হিসাব অন্তর্ভুক্ত নয়)"
        else "Showing isolated data exclusively for year $yearStr"
    fun yearlyTotalCollectedBadge(lang: AppLanguage, yearStr: String) =
        if (lang == AppLanguage.BN) "$yearStr সালের মোট আদায়" else "Total Collected in $yearStr"

    // Academic Year isolation strings
    fun academicYearLabel(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষাবর্ষ / হিসাব বর্ষ" else "Academic / Accounting Year"
    fun studentListForYear(lang: AppLanguage, yearStr: String) =
        if (lang == AppLanguage.BN) "$yearStr শিক্ষাবর্ষের শিক্ষার্থী তালিকা" else "Student List for $yearStr"
    fun noStudentsInYear(lang: AppLanguage, yearStr: String) =
        if (lang == AppLanguage.BN) "$yearStr শিক্ষাবর্ষে এখনও কোনো শিক্ষার্থী যোগ করা হয়নি।"
        else "No students enrolled in academic year $yearStr yet."
    fun addStudentForYear(lang: AppLanguage, yearStr: String) =
        if (lang == AppLanguage.BN) "$yearStr সালের নতুন শিক্ষার্থী যোগ করুন" else "Add Student for $yearStr"
    fun copyStudentsFromPrevYear(lang: AppLanguage, prevYearStr: String) =
        if (lang == AppLanguage.BN) "$prevYearStr সাল থেকে শিক্ষার্থী কপি করুন" else "Import Students from $prevYearStr"
    fun copyStudentsDialogTitle(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "পূর্ববর্তী বছরের শিক্ষার্থী আমদানি" else "Import Students from Previous Year"
    fun copyStudentsDialogMsg(lang: AppLanguage, prevYearStr: String, currYearStr: String) =
        if (lang == AppLanguage.BN) "আপনি কি $prevYearStr সালের সকল শিক্ষার্থীকে $currYearStr শিক্ষাবর্ষে নতুন করে তালিকাভুক্ত করতে চান? তাদের $currYearStr সালের হিসাব সম্পূর্ণ আলাদাভাবে শুরু হবে এবং $prevYearStr সালের পূর্ববর্তী তথ্য সংরক্ষিত থাকবে।"
        else "Do you want to enroll all students from $prevYearStr into academic year $currYearStr? Their accounts for $currYearStr will start completely fresh and $prevYearStr data will remain intact."
    fun confirmCopy(lang: AppLanguage) = if (lang == AppLanguage.BN) "শিক্ষার্থী আনুন" else "Import Students"
    fun copySuccessMsg(lang: AppLanguage, countStr: String, yearStr: String) =
        if (lang == AppLanguage.BN) "$countStr জন শিক্ষার্থী $yearStr শিক্ষাবর্ষে সফলভাবে যুক্ত হয়েছে!"
        else "$countStr student(s) successfully enrolled in $yearStr!"
    fun activeAcademicYearBadge(lang: AppLanguage, yearStr: String) =
        if (lang == AppLanguage.BN) "শিক্ষাবর্ষ: $yearStr" else "Academic Year: $yearStr"
    fun switchYear(lang: AppLanguage) = if (lang == AppLanguage.BN) "বছর পরিবর্তন" else "Switch Year"
    fun selectYearToEnroll(lang: AppLanguage) =
        if (lang == AppLanguage.BN) "কোন শিক্ষাবর্ষে ভর্তি করছেন?" else "Enroll in which Academic Year?"
}
