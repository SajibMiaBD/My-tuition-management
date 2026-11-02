package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PaymentEntity
import com.example.data.StudentEntity
import com.example.domain.BillingPeriod
import com.example.domain.StudentBillingSummary
import com.example.ui.components.EmptyState
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusPaid
import com.example.util.AppLanguage
import com.example.util.BengaliFormatter
import com.example.util.Strings
import java.time.LocalDate

import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailsScreen(
    summary: StudentBillingSummary?,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onBackClick: () -> Unit,
    onEditStudentClick: (StudentEntity) -> Unit,
    onDeleteStudentClick: (StudentEntity) -> Unit,
    onRecordPaymentClick: (Long, Int?) -> Unit,
    onDeletePaymentClick: (PaymentEntity) -> Unit,
    onPauseBillClick: (Long, String) -> Unit = { _, _ -> },
    onResumeBillClick: (Long, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBackClick()
    }

    val context = LocalContext.current
    var showPauseDialog by remember { mutableStateOf(false) }
    var showResumeDialog by remember { mutableStateOf(false) }
    var showPauseHistoryDialog by remember { mutableStateOf(false) }
    var pauseDateInput by remember { mutableStateOf(LocalDate.now().toString()) }
    var resumeDateInput by remember { mutableStateOf(LocalDate.now().toString()) }

    if (summary == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(Strings.studentNotFound(currentLanguage))
        }
        return
    }

    val student = summary.student
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Billing History, 1: Payment Records

    val startDateObj = try {
        LocalDate.parse(student.startDate)
    } catch (e: Exception) {
        null
    }
    val startDateStr = if (startDateObj != null) BengaliFormatter.formatDateDdMmYyyy(startDateObj, currentLanguage) else student.startDate

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.studentDetailsTitle(currentLanguage), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Strings.back(currentLanguage))
                    }
                },
                actions = {
                    IconButton(onClick = { onEditStudentClick(student) }) {
                        Icon(Icons.Default.Edit, contentDescription = Strings.editInfo(currentLanguage))
                    }
                    IconButton(onClick = { onDeleteStudentClick(student) }) {
                        Icon(Icons.Default.Delete, contentDescription = Strings.deleteStudent(currentLanguage), tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onRecordPaymentClick(student.id, null) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("student_details_record_payment"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(Strings.submitPayment(currentLanguage), fontWeight = FontWeight.Bold)
                    }

                    if (student.phone.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${student.phone}"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = Strings.call(currentLanguage))
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("student_details_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Student Profile Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StudentAvatar(name = student.name, size = 56)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = student.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                if (student.phone.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${Strings.phoneLabel(currentLanguage)} ${student.phone}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (student.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = student.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Metadata Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = Strings.startDate(currentLanguage),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = startDateStr,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = Strings.monthlyFee(currentLanguage),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = BengaliFormatter.formatCurrency(student.monthlyFee, currentLanguage),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Bill Status & Pause / Resume Action Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_bill_pause_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (student.isPaused) Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (student.isPaused) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (student.isPaused) Color(0xFFFF8F00).copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (student.isPaused) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                        contentDescription = null,
                                        tint = if (student.isPaused) Color(0xFFE65100) else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (student.isPaused) Strings.billPausedBadge(currentLanguage) else Strings.billActiveBadge(currentLanguage),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (student.isPaused) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (student.isPaused && !student.pausedDate.isNullOrBlank()) {
                                        val pDateObj = try { LocalDate.parse(student.pausedDate) } catch (e: Exception) { null }
                                        val pDateStr = if (pDateObj != null) BengaliFormatter.formatDateDdMmYyyy(pDateObj, currentLanguage) else student.pausedDate
                                        Text(
                                            text = "${Strings.pauseDateLabel(currentLanguage)} $pDateStr",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFB78103)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: [ Pause / Resume ] and beside it [ Pause Details / History ]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (student.isPaused) {
                                Button(
                                    onClick = {
                                        resumeDateInput = LocalDate.now().toString()
                                        showResumeDialog = true
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("resume_bill_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(Strings.resumeBillButton(currentLanguage), fontWeight = FontWeight.Bold)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        pauseDateInput = LocalDate.now().toString()
                                        showPauseDialog = true
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("pause_bill_button"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(Strings.pauseBillButton(currentLanguage), fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = { showPauseHistoryDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pause_details_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(Strings.pauseDetailsButton(currentLanguage), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Stat Cards: Total Paid & Total Due (Equal size and height)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = Strings.totalPaid(currentLanguage),
                        value = BengaliFormatter.formatCurrency(summary.totalPaid, currentLanguage),
                        icon = Icons.Default.CheckCircle,
                        iconTint = StatusPaid,
                        subtitle = if (summary.payments.isNotEmpty()) {
                            Strings.paymentCountLabel(currentLanguage, BengaliFormatter.formatDigits(summary.payments.size, currentLanguage))
                        } else {
                            Strings.noPaymentsYet(currentLanguage)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    StatCard(
                        title = Strings.totalOutstandingDue(currentLanguage),
                        value = BengaliFormatter.formatCurrency(summary.totalDue, currentLanguage),
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = StatusDue,
                        containerColor = if (summary.totalDue > 0) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface,
                        subtitle = if (summary.duePeriodsCount > 0) Strings.monthsDueSuffix(currentLanguage, BengaliFormatter.formatDigits(summary.duePeriodsCount, currentLanguage)) else Strings.noDues(currentLanguage),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }

            // Current Billing Period Card
            summary.currentPeriod?.let { currentP ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Current ongoing header
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.currentOngoingPeriod(currentLanguage, BengaliFormatter.formatDigits(currentP.periodNumber, currentLanguage)),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // ongoing / upcoming badge placed directly under Current ongoing
                            StatusBadge(status = currentP.status, lang = currentLanguage)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = BengaliFormatter.formatPeriodRange(currentP.startDate, currentP.endDate, currentLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${Strings.feeColon(currentLanguage)} ${BengaliFormatter.formatCurrency(currentP.fee, currentLanguage)} | ${Strings.paidColon(currentLanguage)} ${BengaliFormatter.formatCurrency(currentP.paidAmount, currentLanguage)} | ${Strings.remainingColon(currentLanguage)} ${BengaliFormatter.formatCurrency(currentP.remainingAmount, currentLanguage)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Strings.ongoingRuleNotice(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            if (currentP.payments.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                currentP.payments.forEach { p ->
                                    val pDate = try { LocalDate.parse(p.paymentDate) } catch (e: Exception) { null }
                                    val dateStr = if (pDate != null) BengaliFormatter.formatDateDdMmYyyy(pDate, currentLanguage) else p.paymentDate
                                    val dateWithMonth = if (pDate != null) " (${BengaliFormatter.formatDateWithMonthName(pDate, currentLanguage)})" else ""
                                    Text(
                                        text = "${Strings.paidDateNotice(currentLanguage)} $dateStr$dateWithMonth • ${BengaliFormatter.formatCurrency(p.amount, currentLanguage)} ${if (p.notes.isNotBlank()) "(${p.notes})" else ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusPaid
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tab Row: Billing Periods vs Transactions
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                Strings.monthlyBillingHistory(currentLanguage, BengaliFormatter.formatDigits(summary.periods.size, currentLanguage)),
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                Strings.paymentRecords(currentLanguage, BengaliFormatter.formatDigits(summary.payments.size, currentLanguage)),
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            // Content based on tab
            if (selectedTab == 0) {
                // Monthly Billing History
                items(summary.periods.reversed()) { period ->
                    BillingPeriodDetailCard(
                        period = period,
                        currentLanguage = currentLanguage,
                        onPayClick = {
                            onRecordPaymentClick(student.id, period.index)
                        }
                    )
                }
            } else {
                // Payment Records List
                if (summary.payments.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Default.Payments,
                            title = Strings.noPaymentHistory(currentLanguage),
                            subtitle = Strings.noPaymentHistorySubtitle(currentLanguage)
                        )
                    }
                } else {
                    items(summary.payments.sortedByDescending { it.paymentDate }) { payment ->
                        PaymentRecordItem(
                            payment = payment,
                            currentLanguage = currentLanguage,
                            onDeleteClick = { onDeletePaymentClick(payment) }
                        )
                    }
                }
            }
        }
    }

    // Pause Confirmation Dialog
    if (showPauseDialog) {
        AlertDialog(
            onDismissRequest = { showPauseDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Pause, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.confirmPauseTitle(currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = Strings.confirmPauseMsg(currentLanguage, student.name),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = pauseDateInput,
                        onValueChange = { pauseDateInput = it },
                        label = { Text(Strings.pauseDateLabel(currentLanguage)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("pause_date_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalDate = pauseDateInput.trim().ifEmpty { LocalDate.now().toString() }
                        onPauseBillClick(student.id, finalDate)
                        showPauseDialog = false
                    },
                    modifier = Modifier.testTag("confirm_pause_action_button")
                ) {
                    Text(Strings.pauseBillButton(currentLanguage))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPauseDialog = false }) {
                    Text(Strings.cancel(currentLanguage))
                }
            }
        )
    }

    // Resume Confirmation Dialog
    if (showResumeDialog) {
        val pauseDate = com.example.domain.BillingEngine.parseDate(student.pausedDate ?: LocalDate.now().toString())
        val resumeDate = com.example.domain.BillingEngine.parseDate(resumeDateInput)
        val days = java.time.temporal.ChronoUnit.DAYS.between(pauseDate, resumeDate).coerceAtLeast(0)
        val isOver3Days = days > 3

        AlertDialog(
            onDismissRequest = { showResumeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.confirmResumeTitle(currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "${Strings.pauseDateLabel(currentLanguage)} ${student.pausedDate ?: ""}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${Strings.durationPausedLabel(currentLanguage)} ${Strings.daysUnit(currentLanguage, BengaliFormatter.formatDigits(days.toInt(), currentLanguage))}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isOver3Days) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    OutlinedTextField(
                        value = resumeDateInput,
                        onValueChange = { resumeDateInput = it },
                        label = { Text(Strings.resumeDateLabel(currentLanguage)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("resume_date_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = if (isOver3Days) Strings.pauseRule3DaysNotice(currentLanguage) else Strings.pauseRuleShortNotice(currentLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOver3Days) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalDate = resumeDateInput.trim().ifEmpty { LocalDate.now().toString() }
                        onResumeBillClick(student.id, finalDate)
                        showResumeDialog = false
                    },
                    modifier = Modifier.testTag("confirm_resume_action_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text(Strings.resumeBillButton(currentLanguage))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResumeDialog = false }) {
                    Text(Strings.cancel(currentLanguage))
                }
            }
        )
    }

    // Pause History Dialog
    if (showPauseHistoryDialog) {
        val history = summary.pauseRecords
        AlertDialog(
            onDismissRequest = { showPauseHistoryDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.pauseDetailsTitle(currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (student.isPaused) {
                        Surface(
                            color = Color(0xFFFFF8E1),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = Strings.activePauseBanner(currentLanguage, student.pausedDate ?: ""),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }

                    if (history.isEmpty()) {
                        Text(
                            text = Strings.noPauseHistoryYet(currentLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        history.forEachIndexed { i, record ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "#${BengaliFormatter.formatDigits(i + 1, currentLanguage)}: ${record.pauseDate} ➔ ${record.resumeDate ?: ""}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = Strings.durationPausedLabel(currentLanguage) + " " + Strings.daysUnit(currentLanguage, BengaliFormatter.formatDigits(record.daysPaused.toInt(), currentLanguage)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showPauseHistoryDialog = false }) {
                    Text(Strings.ok(currentLanguage))
                }
            }
        )
    }
}

@Composable
fun BillingPeriodDetailCard(
    period: BillingPeriod,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onPayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Period Title & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.periodTitle(currentLanguage, BengaliFormatter.formatDigits(period.periodNumber, currentLanguage)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = period.status, lang = currentLanguage)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Date Range
            Text(
                text = BengaliFormatter.formatPeriodRange(period.startDate, period.endDate, currentLanguage),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Financial Summary Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = Strings.monthlyFee(currentLanguage), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = BengaliFormatter.formatCurrency(period.fee, currentLanguage), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = Strings.paidLabel(currentLanguage), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = BengaliFormatter.formatCurrency(period.paidAmount, currentLanguage), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = StatusPaid)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (period.hasEnded) Strings.dueLabel(currentLanguage) else Strings.remainingColon(currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (period.hasEnded) StatusDue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = BengaliFormatter.formatCurrency(if (period.hasEnded) period.overdueAmount else period.remainingAmount, currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (period.hasEnded && period.overdueAmount > 0) StatusDue else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Display exact payment date(s) whenever payments exist for this period
            if (period.payments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = StatusPaid.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusPaid,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.paymentDatesSectionTitle(currentLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusPaid
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        period.payments.forEach { p ->
                            val pDate = try { LocalDate.parse(p.paymentDate) } catch (e: Exception) { null }
                            val dateFormatted = if (pDate != null) BengaliFormatter.formatDateDdMmYyyy(pDate, currentLanguage) else p.paymentDate
                            val dateWithMonth = if (pDate != null) " (${BengaliFormatter.formatDateWithMonthName(pDate, currentLanguage)})" else ""
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${Strings.dateColon(currentLanguage)} $dateFormatted$dateWithMonth",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (p.notes.isNotBlank()) {
                                        Text(
                                            text = "${Strings.methodColon(currentLanguage)} ${p.notes}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = "+${BengaliFormatter.formatCurrency(p.amount, currentLanguage)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusPaid
                                )
                            }
                        }
                    }
                }
            }

            // Quick "Pay" action if not fully paid
            if (period.remainingAmount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onPayClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(Strings.takePayment(currentLanguage), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentRecordItem(
    payment: PaymentEntity,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StatusPaid.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Payments,
                        contentDescription = null,
                        tint = StatusPaid,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    val pDate = try {
                        LocalDate.parse(payment.paymentDate)
                    } catch (e: Exception) {
                        null
                    }
                    val dateStr = if (pDate != null) BengaliFormatter.formatDateDdMmYyyy(pDate, currentLanguage) else payment.paymentDate
                    Text(
                        text = BengaliFormatter.formatCurrency(payment.amount, currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusPaid
                    )
                    Text(
                        text = "${Strings.dateColon(currentLanguage)} $dateStr ${if (payment.notes.isNotBlank()) "• ${payment.notes}" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = Strings.delete(currentLanguage),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
