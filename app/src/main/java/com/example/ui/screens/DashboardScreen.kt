package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.DashboardStats
import com.example.domain.MonthlyCollection
import com.example.domain.StudentBillingSummary
import com.example.ui.PaymentWithStudent
import com.example.ui.components.AppOutlinedTextField
import com.example.ui.components.EmptyState
import com.example.ui.components.MonthlyCollectionBarChart
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusPaid
import com.example.util.AppLanguage
import com.example.util.BengaliFormatter
import com.example.util.Strings
import java.time.LocalDate

@Composable
fun DashboardScreen(
    stats: DashboardStats,
    studentSummaries: List<StudentBillingSummary>,
    recentPayments: List<PaymentWithStudent>,
    monthlyCollections: List<MonthlyCollection> = emptyList(),
    todayAttendanceSummary: Pair<Int, Int> = Pair(0, 0),
    currentLanguage: AppLanguage = AppLanguage.BN,
    selectedYear: Int = LocalDate.now().year,
    availableYears: List<Int> = listOf(2027, 2026, 2025, 2024),
    onYearSelected: (Int) -> Unit = {},
    onAddStudentClick: () -> Unit,
    onRecordPaymentClick: () -> Unit,
    onViewStudentsClick: () -> Unit,
    onViewDuesClick: () -> Unit,
    onViewPaymentsClick: () -> Unit,
    onAttendanceClick: () -> Unit = {},
    onStudentClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val todayFormatted = BengaliFormatter.formatDateWithMonthName(today, currentLanguage)
    val yrDigits = BengaliFormatter.formatDigits(selectedYear, currentLanguage)
    var showYearPickerDialog by remember { mutableStateOf(false) }
    var showThisMonthPaymentsDialog by remember { mutableStateOf(false) }
    var showFullyPaidStudentsDialog by remember { mutableStateOf(false) }

    // Fully paid students: no dues and cleared up to end date in selectedYear
    val fullyPaidStudentList = remember(studentSummaries, selectedYear) {
        studentSummaries.filter { summary ->
            summary.periods.none { it.endDate.year == selectedYear && it.overdueAmount > 0.0 } &&
                summary.totalDue == 0.0
        }
    }

    // Filter payments strictly belonging to the current month in selectedYear
    val thisMonthPayments = remember(recentPayments, selectedYear) {
        val targetMonth = today.monthValue
        recentPayments.filter { item ->
            try {
                val d = LocalDate.parse(item.payment.paymentDate)
                d.year == selectedYear && d.monthValue == targetMonth
            } catch (e: Exception) {
                false
            }
        }.sortedByDescending { it.payment.paymentDate }
    }

    val thisMonthCollectionTotal = remember(thisMonthPayments) {
        thisMonthPayments.sumOf { it.payment.amount }
    }

    val currentMonthName = remember(currentLanguage, selectedYear) {
        val ym = java.time.YearMonth.of(selectedYear, today.monthValue)
        val monthBn = arrayOf(
            "", "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
        ).getOrElse(today.monthValue) { "" }
        val yrStr = BengaliFormatter.formatDigits(selectedYear, currentLanguage)
        if (currentLanguage == AppLanguage.BN) "$monthBn $yrStr" else "${ym.month.name.lowercase().replaceFirstChar { it.uppercase() }} $selectedYear"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Welcome Header & Date with "Select Accounting year" right underneath
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = Strings.welcomeTeacher(currentLanguage),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Strings.dashboardSubtitle(currentLanguage, todayFormatted),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Select Accounting Year Bar (under the date) - Unified single solid color
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_accounting_year_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = Strings.selectAccountingYearLabel(currentLanguage),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = Strings.yearNotice(currentLanguage, yrDigits),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Year Button showing only the single selected year
                        Surface(
                            onClick = { showYearPickerDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.22f),
                            modifier = Modifier.testTag("select_year_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = yrDigits,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Year",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAddStudentClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_add_student_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(Strings.newStudent(currentLanguage), fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onRecordPaymentClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_record_payment_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(Strings.recordPayment(currentLanguage), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // 6 Dashboard Stat Cards
        item {
            // Exactly matches bottom NavigationBar background (dashboard, students, payments, dues, settings)
            val statCardBg = MaterialTheme.colorScheme.surface

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1: Total Students & Monthly Expected Income
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = Strings.totalStudents(currentLanguage),
                        value = Strings.studentUnit(currentLanguage, BengaliFormatter.formatDigits(stats.totalStudents, currentLanguage)),
                        icon = Icons.Default.Groups,
                        iconTint = MaterialTheme.colorScheme.primary,
                        containerColor = statCardBg,
                        subtitle = if (currentLanguage == AppLanguage.BN) "নথিভুক্ত শিক্ষার্থী" else "Enrolled students",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = onViewStudentsClick
                    )
                    StatCard(
                        title = Strings.monthlyExpectedIncome(currentLanguage),
                        value = BengaliFormatter.formatCurrency(stats.monthlyExpectedIncome, currentLanguage),
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        iconTint = Color(0xFF0284C7),
                        containerColor = statCardBg,
                        subtitle = if (currentLanguage == AppLanguage.BN) "আনুমানিক মাসিক আয়" else "Monthly projected",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }

                // Row 2: Attendance Card & Total Outstanding Due In Year
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val presentCount = todayAttendanceSummary.first
                    val totalCount = todayAttendanceSummary.second
                    val attRate = if (totalCount > 0) (presentCount.toFloat() / totalCount.toFloat() * 100f).toInt() else 0
                    val attValue = if (totalCount > 0) {
                        Strings.presentCountUnit(currentLanguage, BengaliFormatter.formatDigits(presentCount, currentLanguage), BengaliFormatter.formatDigits(totalCount, currentLanguage))
                    } else {
                        if (currentLanguage == AppLanguage.BN) "হাজিরা দিন" else "Mark Attendance"
                    }
                    val attSubtitle = if (totalCount > 0) {
                        "${BengaliFormatter.formatDigits(attRate, currentLanguage)}% ${if (currentLanguage == AppLanguage.BN) "উপস্থিতি • চার্ট দেখুন" else "Present • View Chart"}"
                    } else {
                        Strings.attendanceCardSubtitle(currentLanguage)
                    }

                    StatCard(
                        title = Strings.attendanceCardTitle(currentLanguage),
                        value = attValue,
                        icon = Icons.Default.FactCheck,
                        iconTint = StatusPaid,
                        containerColor = statCardBg,
                        subtitle = attSubtitle,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("dashboard_attendance_card"),
                        onClick = onAttendanceClick
                    )
                    StatCard(
                        title = if (currentLanguage == AppLanguage.BN) "$yrDigits সালের মোট বকেয়া" else "Outstanding Due $selectedYear",
                        value = BengaliFormatter.formatCurrency(stats.totalOutstandingDue, currentLanguage),
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = StatusDue,
                        containerColor = statCardBg,
                        subtitle = if (currentLanguage == AppLanguage.BN) "পরিশোধযোগ্য বকেয়া" else "Total pending due",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = onViewDuesClick
                    )
                }

                // Row 3: Students With Due & Fully Paid Students
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = Strings.studentsWithDue(currentLanguage),
                        value = Strings.studentUnit(currentLanguage, BengaliFormatter.formatDigits(stats.studentsWithDueCount, currentLanguage)),
                        icon = Icons.Default.ErrorOutline,
                        iconTint = StatusDue,
                        containerColor = statCardBg,
                        subtitle = if (stats.studentsWithDueCount > 0) Strings.reminderNeeded(currentLanguage) else Strings.noDues(currentLanguage),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = onViewDuesClick
                    )
                    StatCard(
                        title = Strings.fullyPaidStudents(currentLanguage),
                        value = Strings.studentUnit(currentLanguage, BengaliFormatter.formatDigits(stats.fullyPaidStudentsCount, currentLanguage)),
                        icon = Icons.Default.CheckCircle,
                        iconTint = StatusPaid,
                        containerColor = statCardBg,
                        subtitle = Strings.regularPayers(currentLanguage),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { showFullyPaidStudentsDialog = true }
                    )
                }
            }
        }

        // Overdue Alert Section strictly for selectedYear (if dues exist)
        val dueStudents = studentSummaries.filter { summary ->
            summary.periods.any { it.endDate.year == selectedYear && it.overdueAmount > 0.0 }
        }
        if (dueStudents.isNotEmpty()) {
            item {
                SectionHeader(
                    title = Strings.duesList(currentLanguage, BengaliFormatter.formatDigits(dueStudents.size, currentLanguage)),
                    actionText = Strings.viewAllDues(currentLanguage),
                    onActionClick = onViewDuesClick
                )
            }

            items(dueStudents.take(3)) { summary ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStudentClick(summary.student.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            StudentAvatar(name = summary.student.name)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = summary.student.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = Strings.monthsDueSuffix(currentLanguage, BengaliFormatter.formatDigits(summary.duePeriodsCount, currentLanguage)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = BengaliFormatter.formatCurrency(summary.totalDue, currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = Strings.dueLabel(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Recent Payments Section strictly for selectedYear
        val yearPayments = recentPayments.filter {
            it.payment.paymentDate.startsWith("$selectedYear-")
        }
        item {
            SectionHeader(
                title = if (currentLanguage == AppLanguage.BN) "$yrDigits সালের সাম্প্রতিক আদায়" else "Recent Payments in $selectedYear",
                actionText = if (yearPayments.isNotEmpty()) Strings.viewAllPayments(currentLanguage) else null,
                onActionClick = onViewPaymentsClick
            )
        }

        if (yearPayments.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.Payments,
                    title = Strings.noPaymentHistory(currentLanguage),
                    subtitle = Strings.noPaymentHistorySubtitle(currentLanguage)
                )
            }
        } else {
            items(yearPayments.take(4)) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(StatusPaid.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = StatusPaid,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = item.studentName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                val pDate = try {
                                    LocalDate.parse(item.payment.paymentDate)
                                } catch (e: Exception) {
                                    null
                                }
                                val dateStr = if (pDate != null) BengaliFormatter.formatDateDdMmYyyy(pDate, currentLanguage) else item.payment.paymentDate
                                val notesText = if (item.payment.notes.isNotBlank()) " • ${item.payment.notes}" else ""
                                Text(
                                    text = "${Strings.dateColon(currentLanguage)} $dateStr$notesText",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "+${BengaliFormatter.formatCurrency(item.payment.amount, currentLanguage)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StatusPaid
                        )
                    }
                }
            }
        }

        // Monthly Collection Bar Chart restored at the bottom of the dashboard
        item {
            MonthlyCollectionBarChart(
                monthlyCollections = monthlyCollections,
                currentLanguage = currentLanguage,
                selectedYear = selectedYear
            )
        }
    }

    if (showThisMonthPaymentsDialog) {
        ThisMonthPaymentsDialog(
            monthName = currentMonthName,
            payments = thisMonthPayments,
            totalAmount = thisMonthCollectionTotal,
            currentLanguage = currentLanguage,
            onDismiss = { showThisMonthPaymentsDialog = false },
            onStudentClick = { studentId ->
                showThisMonthPaymentsDialog = false
                onStudentClick(studentId)
            }
        )
    }

    if (showFullyPaidStudentsDialog) {
        FullyPaidStudentsDialog(
            selectedYear = selectedYear,
            students = fullyPaidStudentList,
            currentLanguage = currentLanguage,
            onDismiss = { showFullyPaidStudentsDialog = false },
            onStudentClick = { studentId ->
                showFullyPaidStudentsDialog = false
                onStudentClick(studentId)
            }
        )
    }

    if (showYearPickerDialog) {
        AccountingYearPickerDialog(
            selectedYear = selectedYear,
            currentLanguage = currentLanguage,
            onDismiss = { showYearPickerDialog = false },
            onYearSelected = { yr ->
                onYearSelected(yr)
                showYearPickerDialog = false
            }
        )
    }
}

/**
 * Dialog displaying accounting years from 2000 to 2099 with search and quick presets.
 * When a year is picked, it selects that single year and closes immediately.
 */
@Composable
fun AccountingYearPickerDialog(
    selectedYear: Int,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onDismiss: () -> Unit,
    onYearSelected: (Int) -> Unit
) {
    val allYears = remember { (2099 downTo 2000).toList() }
    var searchQuery by remember { mutableStateOf("") }

    val filteredYears = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allYears
        } else {
            val q = BengaliFormatter.toEnglishDigits(searchQuery.trim())
            allYears.filter { it.toString().contains(q) }
        }
    }

    val currentYear = LocalDate.now().year

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Strings.selectYearDialogTitle(currentLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                // Search Bar
                AppOutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(Strings.searchYearPlaceholder(currentLanguage)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Preset Chips (Current, Last, Next)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickYears = listOf(currentYear, currentYear - 1, currentYear + 1)
                    quickYears.forEach { yr ->
                        val isSelected = yr == selectedYear
                        FilterChip(
                            selected = isSelected,
                            onClick = { onYearSelected(yr) },
                            label = {
                                Text(
                                    text = if (yr == currentYear) {
                                        "${BengaliFormatter.formatDigits(yr, currentLanguage)} (${Strings.currentYearTag(currentLanguage)})"
                                    } else {
                                        BengaliFormatter.formatDigits(yr, currentLanguage)
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Grid of Years from 2000 to 2099 (3 columns)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredYears, key = { it }) { yr ->
                        val isSelected = yr == selectedYear
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onYearSelected(yr) }
                                .testTag("dialog_year_item_$yr"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = BengaliFormatter.formatDigits(yr, currentLanguage),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(Strings.cancel(currentLanguage))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Dialog displaying all payments received in the current month, showing
 * the total collection, payer list, dates, payment amounts, and notes.
 */
@Composable
fun ThisMonthPaymentsDialog(
    monthName: String,
    payments: List<PaymentWithStudent>,
    totalAmount: Double,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onDismiss: () -> Unit,
    onStudentClick: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(StatusPaid.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = StatusPaid,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = Strings.collectionInThisMonth(currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = monthName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                // Summary Header Banner: Total amount & student count
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = Strings.totalCollectedThisMonthBadge(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = BengaliFormatter.formatCurrency(totalAmount, currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = Strings.studentUnit(currentLanguage, BengaliFormatter.formatDigits(payments.size, currentLanguage)),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (payments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                modifier = Modifier.size(38.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = Strings.noPaymentsThisMonthMsg(currentLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Text(
                        text = Strings.payingStudentsListLabel(currentLanguage),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(payments) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onStudentClick(item.payment.studentId)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        StudentAvatar(name = item.studentName, size = 36)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = item.studentName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            val pDate = try {
                                                LocalDate.parse(item.payment.paymentDate)
                                            } catch (e: Exception) {
                                                null
                                            }
                                            val dateStr = if (pDate != null) BengaliFormatter.formatDateDdMmYyyy(pDate, currentLanguage) else item.payment.paymentDate
                                            val notesStr = if (item.payment.notes.isNotBlank()) " • ${item.payment.notes}" else ""
                                            Text(
                                                text = "$dateStr$notesStr",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Text(
                                        text = "+${BengaliFormatter.formatCurrency(item.payment.amount, currentLanguage)}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusPaid
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(Strings.cancel(currentLanguage))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Dialog displaying fully paid students (with zero overdue and completely cleared payments)
 * for the selected academic year.
 */
@Composable
fun FullyPaidStudentsDialog(
    selectedYear: Int,
    students: List<StudentBillingSummary>,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onDismiss: () -> Unit,
    onStudentClick: (Long) -> Unit
) {
    val yrDigits = BengaliFormatter.formatDigits(selectedYear, currentLanguage)
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(students, searchQuery) {
        if (searchQuery.isBlank()) {
            students
        } else {
            val q = searchQuery.trim()
            students.filter { it.student.name.contains(q, ignoreCase = true) || it.student.phone.contains(q) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(StatusPaid.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusPaid,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (currentLanguage == AppLanguage.BN) "$yrDigits সালের পরিশোধিত শিক্ষার্থী" else "Fully Paid Students $selectedYear",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.BN) "কোনো বকেয়া নেই • সকল ফি পরিশোধিত" else "Zero dues • Completely cleared",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
            ) {
                // Summary Header Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusPaid.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.BN) "পরিশোধিত শিক্ষার্থী সংখ্যা" else "Cleared Students Count",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = StatusPaid
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = Strings.studentUnit(currentLanguage, BengaliFormatter.formatDigits(students.size, currentLanguage)),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = StatusPaid
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                AppOutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(Strings.searchStudentPlaceholder(currentLanguage)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StatusPaid) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                modifier = Modifier.size(38.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    if (currentLanguage == AppLanguage.BN) "কোনো শিক্ষার্থী খুঁজে পাওয়া যায়নি" else "No matching students found"
                                } else {
                                    if (currentLanguage == AppLanguage.BN) "$yrDigits সালে কোনো পরিশোধিত শিক্ষার্থী পাওয়া যায়নি" else "No fully paid students found in $selectedYear"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredList) { summary ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onStudentClick(summary.student.id)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        StudentAvatar(name = summary.student.name, size = 36)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = summary.student.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                softWrap = false,
                                                overflow = TextOverflow.Ellipsis,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (summary.student.phone.isNotBlank()) summary.student.phone else (if (currentLanguage == AppLanguage.BN) "ফোন নম্বর নেই" else "No phone"),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = StatusPaid.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = if (currentLanguage == AppLanguage.BN) "পরিশোধিত" else "Paid",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = StatusPaid,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${Strings.totalPaid(currentLanguage)}: ${BengaliFormatter.formatCurrency(summary.totalPaid, currentLanguage)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(Strings.cancel(currentLanguage))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}


