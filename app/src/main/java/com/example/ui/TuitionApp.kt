package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AddEditStudentDialog
import com.example.ui.components.RecordPaymentDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DuesScreen
import com.example.ui.screens.PaymentsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudentDetailsScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.theme.StatusPaid
import com.example.util.BengaliFormatter
import com.example.util.Strings

data class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val screen: Screen,
    val testTag: String
)

@Composable
fun TuitionApp(
    viewModel: TuitionViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    val stats by viewModel.dashboardStats.collectAsState()
    val monthlyCollections by viewModel.monthlyCollections.collectAsState()
    val studentSummaries by viewModel.studentSummaries.collectAsState()
    val filteredStudents by viewModel.filteredStudents.collectAsState()
    val paymentsWithStudents by viewModel.paymentsWithStudents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.studentFilter.collectAsState()

    // Dialog states
    val showAddEditStudent by viewModel.showAddEditStudentDialog.collectAsState()
    val studentToEdit by viewModel.studentToEdit.collectAsState()
    val showRecordPayment by viewModel.showRecordPaymentDialog.collectAsState()
    val preselectedStudentId by viewModel.preselectedStudentId.collectAsState()
    val preselectedPeriodIndex by viewModel.preselectedPeriodIndex.collectAsState()
    val studentToDelete by viewModel.studentToDelete.collectAsState()
    val paymentToDelete by viewModel.paymentToDelete.collectAsState()
    val paymentSuccessReceipt by viewModel.paymentSuccessReceipt.collectAsState()
    val pendingBackupData by viewModel.pendingBackupData.collectAsState()
    val backupStatusMessage by viewModel.backupStatusMessage.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val availableYears by viewModel.availableYears.collectAsState()

    val navItems = listOf(
        NavItem(Strings.navDashboard(currentLanguage), Icons.Filled.Dashboard, Icons.Outlined.Dashboard, Screen.Dashboard, "nav_dashboard"),
        NavItem(Strings.navStudents(currentLanguage), Icons.Filled.People, Icons.Outlined.People, Screen.Students, "nav_students"),
        NavItem(Strings.navPayments(currentLanguage), Icons.Filled.Payments, Icons.Outlined.Payments, Screen.Payments, "nav_payments"),
        NavItem(Strings.navDues(currentLanguage), Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, Screen.Dues, "nav_dues"),
        NavItem(Strings.navSettings(currentLanguage), Icons.Filled.Settings, Icons.Outlined.Settings, Screen.Settings, "nav_settings")
    )

    // Handle system back button
    BackHandler(enabled = currentScreen !is Screen.Dashboard) {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(Screen.Dashboard)
        }
    }

    Scaffold(
        bottomBar = {
            if (currentScreen !is Screen.StudentDetails) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentScreen::class == item.screen::class
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item.screen) },
                            icon = {
                                if (item.screen is Screen.Dues && stats.studentsWithDueCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.error,
                                                contentColor = Color.White
                                            ) {
                                                Text(BengaliFormatter.formatDigits(stats.studentsWithDueCount, currentLanguage))
                                            }
                                        }
                                    ) {
                                        Icon(
                                            if (isSelected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    is Screen.Dashboard -> {
                        DashboardScreen(
                            stats = stats,
                            monthlyCollections = monthlyCollections,
                            studentSummaries = studentSummaries,
                            recentPayments = paymentsWithStudents,
                            selectedYear = selectedYear,
                            availableYears = availableYears,
                            currentLanguage = currentLanguage,
                            onYearSelected = { viewModel.setSelectedYear(it) },
                            onAddStudentClick = { viewModel.openAddStudentDialog() },
                            onRecordPaymentClick = { viewModel.openRecordPaymentDialog() },
                            onViewStudentsClick = { viewModel.navigateTo(Screen.Students) },
                            onViewDuesClick = { viewModel.navigateTo(Screen.Dues) },
                            onViewPaymentsClick = { viewModel.navigateTo(Screen.Payments) },
                            onStudentClick = { id -> viewModel.navigateTo(Screen.StudentDetails(id)) }
                        )
                    }

                    is Screen.Students -> {
                        StudentsScreen(
                            students = filteredStudents,
                            allStudentSummaries = studentSummaries,
                            searchQuery = searchQuery,
                            selectedFilter = selectedFilter,
                            currentLanguage = currentLanguage,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onFilterChange = { viewModel.setStudentFilter(it) },
                            onStudentClick = { id -> viewModel.navigateTo(Screen.StudentDetails(id)) },
                            onAddStudentClick = { viewModel.openAddStudentDialog() },
                            onEditStudentClick = { student -> viewModel.openEditStudentDialog(student) },
                            onDeleteStudentClick = { student -> viewModel.confirmDeleteStudent(student) },
                            onRecordPaymentClick = { id -> viewModel.openRecordPaymentDialog(studentId = id) }
                        )
                    }

                    is Screen.StudentDetails -> {
                        val summary = studentSummaries.find { it.student.id == targetScreen.studentId }
                        StudentDetailsScreen(
                            summary = summary,
                            currentLanguage = currentLanguage,
                            onBackClick = { viewModel.navigateBack() },
                            onEditStudentClick = { student -> viewModel.openEditStudentDialog(student) },
                            onDeleteStudentClick = { student -> viewModel.confirmDeleteStudent(student) },
                            onRecordPaymentClick = { studentId, periodIndex ->
                                viewModel.openRecordPaymentDialog(studentId, periodIndex)
                            },
                            onDeletePaymentClick = { payment -> viewModel.confirmDeletePayment(payment) },
                            onPauseBillClick = { studentId, pauseDate ->
                                viewModel.pauseStudentBilling(studentId, pauseDate)
                            },
                            onResumeBillClick = { studentId, resumeDate ->
                                viewModel.resumeStudentBilling(studentId, resumeDate)
                            }
                        )
                    }

                    is Screen.Payments -> {
                        PaymentsScreen(
                            payments = paymentsWithStudents,
                            currentLanguage = currentLanguage,
                            onAddPaymentClick = { viewModel.openRecordPaymentDialog() },
                            onDeletePaymentClick = { payment -> viewModel.confirmDeletePayment(payment) },
                            onStudentClick = { id -> viewModel.navigateTo(Screen.StudentDetails(id)) }
                        )
                    }

                    is Screen.Dues -> {
                        DuesScreen(
                            students = studentSummaries,
                            currentLanguage = currentLanguage,
                            onStudentClick = { id -> viewModel.navigateTo(Screen.StudentDetails(id)) },
                            onRecordPaymentClick = { id -> viewModel.openRecordPaymentDialog(studentId = id) }
                        )
                    }

                    is Screen.Settings -> {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        SettingsScreen(
                            currentLanguage = currentLanguage,
                            onLanguageChange = { viewModel.setLanguage(it) },
                            currentTheme = currentTheme,
                            onThemeChange = { viewModel.setTheme(it) },
                            onReloadSampleData = { viewModel.reloadSampleData() },
                            onClearAllData = { viewModel.clearAllData() },
                            pendingBackupData = pendingBackupData,
                            backupStatusMessage = backupStatusMessage,
                            onPrepareExport = { onReady -> viewModel.prepareExportContent(onReady) },
                            onWriteBackupToUri = { uri -> viewModel.writeBackupToUri(context, uri) },
                            onPrepareImportFromUri = { uri -> viewModel.prepareImportFromUri(context, uri) },
                            onExecuteImport = { replace -> viewModel.executeImport(replace) },
                            onCancelImport = { viewModel.cancelImport() },
                            onDismissBackupStatus = { viewModel.dismissBackupStatus() },
                            onShareBackup = { viewModel.shareBackup(context) }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Student Dialog
    if (showAddEditStudent) {
        AddEditStudentDialog(
            studentToEdit = studentToEdit,
            currentLanguage = currentLanguage,
            onDismiss = { viewModel.closeAddEditStudentDialog() },
            onSave = { name, phone, startDate, monthlyFee, notes, id ->
                viewModel.saveStudent(name, phone, startDate, monthlyFee, notes, id)
            }
        )
    }

    // Record Payment Dialog
    if (showRecordPayment) {
        RecordPaymentDialog(
            studentSummaries = studentSummaries,
            initialStudentId = preselectedStudentId,
            initialPeriodIndex = preselectedPeriodIndex,
            currentLanguage = currentLanguage,
            onDismiss = { viewModel.closeRecordPaymentDialog() },
            onConfirm = { studentId, periodIndex, periodStartDate, periodEndDate, amount, paymentDate, notes ->
                viewModel.recordPayment(
                    studentId,
                    periodIndex,
                    periodStartDate,
                    periodEndDate,
                    amount,
                    paymentDate,
                    notes
                )
            }
        )
    }

    // Delete Student Confirmation Dialog
    studentToDelete?.let { student ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteStudent() },
            title = { Text(Strings.deleteStudentConfirmTitle(currentLanguage)) },
            text = { Text(Strings.deleteStudentConfirmText(currentLanguage, student.name)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.executeDeleteStudent() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(Strings.delete(currentLanguage))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.dismissDeleteStudent() },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(Strings.cancel(currentLanguage))
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Delete Payment Confirmation Dialog
    paymentToDelete?.let { payment ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeletePayment() },
            title = { Text(Strings.deletePaymentConfirmTitle(currentLanguage)) },
            text = { Text(Strings.deletePaymentConfirmText(currentLanguage, BengaliFormatter.formatCurrency(payment.amount, currentLanguage))) },
            confirmButton = {
                Button(
                    onClick = { viewModel.executeDeletePayment() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(Strings.delete(currentLanguage))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.dismissDeletePayment() },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(Strings.cancel(currentLanguage))
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Instant Payment Date & Confirmation Receipt Dialog
    paymentSuccessReceipt?.let { receipt ->
        val pDate = try { java.time.LocalDate.parse(receipt.paymentDate) } catch (e: Exception) { null }
        val dateFormatted = if (pDate != null) BengaliFormatter.formatDateDdMmYyyy(pDate, currentLanguage) else receipt.paymentDate
        val dateWithMonth = if (pDate != null) BengaliFormatter.formatDateWithMonthName(pDate, currentLanguage) else ""

        AlertDialog(
            onDismissRequest = { viewModel.dismissPaymentSuccessReceipt() },
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(StatusPaid.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusPaid,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = Strings.paymentSuccessReceiptTitle(currentLanguage),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Prominent Payment Date Banner
                    Surface(
                        color = StatusPaid.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = Strings.paymentDateReceiptLabel(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = dateFormatted,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = StatusPaid
                            )
                            if (dateWithMonth.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dateWithMonth,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StatusPaid
                                )
                            }
                        }
                    }

                    // Details Card
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(Strings.studentLabel(currentLanguage), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                                Text(receipt.studentName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(Strings.receivedAmountLabel(currentLanguage), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    BengaliFormatter.formatCurrency(receipt.amount, currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    color = StatusPaid,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(Strings.billingPeriodLabel(currentLanguage), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                                Text(receipt.periodRange, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            }
                            if (receipt.notes.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(Strings.paymentMethodLabel(currentLanguage), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                                    Text(receipt.notes, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissPaymentSuccessReceipt() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(Strings.ok(currentLanguage), fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
