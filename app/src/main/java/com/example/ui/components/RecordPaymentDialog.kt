package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.BillingPeriod
import com.example.domain.PaymentStatus
import com.example.domain.StudentBillingSummary
import com.example.util.AppLanguage
import com.example.util.BengaliFormatter
import com.example.util.Strings
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecordPaymentDialog(
    studentSummaries: List<StudentBillingSummary>,
    initialStudentId: Long? = null,
    initialPeriodIndex: Int? = null,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onDismiss: () -> Unit,
    onConfirm: (
        studentId: Long,
        periodIndex: Int,
        periodStartDate: String,
        periodEndDate: String,
        amount: Double,
        paymentDate: String,
        notes: String
    ) -> Unit
) {
    val context = LocalContext.current

    // Selected Student
    var selectedStudentId by remember {
        mutableStateOf(initialStudentId ?: studentSummaries.firstOrNull()?.student?.id ?: 0L)
    }

    val selectedSummary = studentSummaries.find { it.student.id == selectedStudentId }

    // Selected Period
    var selectedPeriodIndex by remember {
        val defaultIdx = if (initialPeriodIndex != null) {
            initialPeriodIndex
        } else {
            // Find oldest due or partially paid period
            val firstDue = selectedSummary?.periods?.find { it.status == PaymentStatus.DUE || it.status == PaymentStatus.PARTIALLY_PAID }
            firstDue?.index ?: selectedSummary?.currentPeriod?.index ?: 0
        }
        mutableIntStateOf(defaultIdx)
    }

    // Update selected period if student changes
    LaunchedEffect(selectedStudentId) {
        if (initialStudentId == null || selectedStudentId != initialStudentId) {
            val firstDue = selectedSummary?.periods?.find { it.status == PaymentStatus.DUE || it.status == PaymentStatus.PARTIALLY_PAID }
            selectedPeriodIndex = firstDue?.index ?: selectedSummary?.currentPeriod?.index ?: 0
        }
    }

    val selectedPeriod = selectedSummary?.periods?.find { it.index == selectedPeriodIndex }
        ?: selectedSummary?.periods?.firstOrNull()

    // Amount String
    var amountStr by remember {
        val rem = selectedPeriod?.remainingAmount ?: 0.0
        mutableStateOf(if (rem > 0) rem.toInt().toString() else "")
    }

    // Auto-update amount when selected period changes
    LaunchedEffect(selectedPeriodIndex, selectedStudentId) {
        val rem = selectedPeriod?.remainingAmount ?: 0.0
        if (rem > 0) {
            amountStr = rem.toInt().toString()
        }
    }

    // Payment Date
    var paymentDate by remember { mutableStateOf(LocalDate.now()) }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                paymentDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            paymentDate.year,
            paymentDate.monthValue - 1,
            paymentDate.dayOfMonth
        )
    }

    // Default note
    var notes by remember { mutableStateOf(if (currentLanguage == AppLanguage.BN) "ক্যাশ" else "Cash") }
    var studentDropdownExpanded by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Strings.recordPaymentTitle(currentLanguage),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Select Student (if multiple)
                if (studentSummaries.size > 1 && initialStudentId == null) {
                    Text(
                        text = Strings.selectStudentRequired(currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = studentDropdownExpanded,
                        onExpandedChange = { studentDropdownExpanded = !studentDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSummary?.student?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentDropdownExpanded) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = studentDropdownExpanded,
                            onDismissRequest = { studentDropdownExpanded = false }
                        ) {
                            studentSummaries.forEach { summary ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(summary.student.name, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                "${Strings.dueLabel(currentLanguage)}: ${BengaliFormatter.formatCurrency(summary.totalDue, currentLanguage)} | ${Strings.feeColon(currentLanguage)} ${BengaliFormatter.formatCurrency(summary.student.monthlyFee, currentLanguage)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedStudentId = summary.student.id
                                        studentDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else if (selectedSummary != null) {
                    // Show current student badge
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StudentAvatar(name = selectedSummary.student.name, size = 36)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    selectedSummary.student.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${Strings.totalOutstandingDue(currentLanguage)}: ${BengaliFormatter.formatCurrency(selectedSummary.totalDue, currentLanguage)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (selectedSummary.totalDue > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 2. Select Billing Period
                Text(
                    text = Strings.selectBillingPeriodRequired(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                val periods = selectedSummary?.periods ?: emptyList()
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    periods.takeLast(6).forEach { period ->
                        val isSelected = period.index == selectedPeriodIndex
                        PeriodSelectionCard(
                            period = period,
                            isSelected = isSelected,
                            currentLanguage = currentLanguage,
                            onSelect = { selectedPeriodIndex = period.index }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Enter Payment Amount
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = {
                        amountStr = it
                        val parsed = BengaliFormatter.parseAmount(it)
                        if (parsed != null && parsed > 0) amountError = false
                    },
                    label = { Text(Strings.paymentAmountRequired(currentLanguage)) },
                    leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                    prefix = { Text("৳ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError,
                    supportingText = {
                        if (amountError) {
                            Text(Strings.amountRequiredError(currentLanguage))
                        } else if (selectedPeriod != null) {
                            Text(Strings.periodRemainingHelper(currentLanguage, BengaliFormatter.formatCurrency(selectedPeriod.remainingAmount, currentLanguage)))
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payment_amount_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick amount chips
                if (selectedPeriod != null && selectedPeriod.remainingAmount > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { amountStr = selectedPeriod.remainingAmount.toInt().toString() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(Strings.fullRemainingButton(currentLanguage), style = MaterialTheme.typography.labelSmall)
                        }
                        if (selectedPeriod.remainingAmount >= 200) {
                            OutlinedButton(
                                onClick = { amountStr = (selectedPeriod.remainingAmount / 2).toInt().toString() },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(Strings.halfButton(currentLanguage), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Payment Date
                Text(
                    text = Strings.paymentDateRequired(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pick_payment_date_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${BengaliFormatter.formatDateDdMmYyyy(paymentDate, currentLanguage)} (${BengaliFormatter.formatDateWithMonthName(paymentDate, currentLanguage)})",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 5. Payment Method / Notes
                Text(
                    text = Strings.paymentMethodNotes(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                val quickNotes = Strings.quickPaymentMethods(currentLanguage)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    quickNotes.forEach { method ->
                        FilterChip(
                            selected = notes.startsWith(method),
                            onClick = { notes = method },
                            label = { Text(method) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text(Strings.notesPlaceholder(currentLanguage)) },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payment_notes_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = BengaliFormatter.parseAmount(amountStr)
                    if (amount == null || amount <= 0) {
                        amountError = true
                        return@Button
                    }
                    if (selectedPeriod != null && selectedSummary != null) {
                        onConfirm(
                            selectedSummary.student.id,
                            selectedPeriod.index,
                            selectedPeriod.startDate.toString(),
                            selectedPeriod.endDate.toString(),
                            amount,
                            paymentDate.toString(),
                            notes
                        )
                    }
                },
                modifier = Modifier.testTag("confirm_payment_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(Strings.addPaymentButton(currentLanguage))
            }
        },
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

@Composable
fun PeriodSelectionCard(
    period: BillingPeriod,
    isSelected: Boolean,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onSelect: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(10.dp)
            ),
        color = bgColor,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${Strings.periodTitle(currentLanguage, BengaliFormatter.formatDigits(period.periodNumber, currentLanguage))}: ${BengaliFormatter.formatPeriodRange(period.startDate, period.endDate, currentLanguage)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                    Text(
                        text = "${Strings.feeColon(currentLanguage)} ${BengaliFormatter.formatCurrency(period.fee, currentLanguage)} | ${Strings.paidColon(currentLanguage)} ${BengaliFormatter.formatCurrency(period.paidAmount, currentLanguage)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            StatusBadge(status = period.status, lang = currentLanguage)
        }
    }
}
