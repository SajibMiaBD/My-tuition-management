package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PaymentEntity
import com.example.domain.BillingPeriod
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusPaid
import com.example.util.AppLanguage
import com.example.util.BengaliFormatter
import com.example.util.Strings
import java.time.LocalDate

class EditablePaymentItem(
    val original: PaymentEntity,
    initialAmount: String,
    initialDate: String,
    initialNotes: String
) {
    var amount by mutableStateOf(initialAmount)
    var date by mutableStateOf(initialDate)
    var notes by mutableStateOf(initialNotes)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPeriodDialog(
    period: BillingPeriod,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onDismissRequest: () -> Unit,
    onSave: (
        newStartDate: String,
        modifiedPayments: List<PaymentEntity>,
        newPayment: PaymentEntity?,
        deletedPaymentIds: List<Long>
    ) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var startDateInput by remember { mutableStateOf(period.startDate.toString()) }

    fun showDatePicker(initialDateStr: String, onDateSelected: (String) -> Unit) {
        val initial = try { LocalDate.parse(initialDateStr.trim()) } catch (_: Exception) { LocalDate.now() }
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val picked = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(picked.toString())
            },
            initial.year,
            initial.monthValue - 1,
            initial.dayOfMonth
        ).show()
    }

    val calculatedEndDate = remember(startDateInput) {
        try {
            LocalDate.parse(startDateInput.trim()).plusMonths(1).minusDays(1)
        } catch (_: Exception) {
            period.endDate
        }
    }

    val editablePayments = remember(period.payments) {
        period.payments.map { p ->
            EditablePaymentItem(
                original = p,
                initialAmount = if (p.amount % 1.0 == 0.0) p.amount.toInt().toString() else p.amount.toString(),
                initialDate = p.paymentDate,
                initialNotes = p.notes
            )
        }
    }

    val deletedPaymentIds = remember { mutableStateListOf<Long>() }

    var showAddNewPayment by remember {
        mutableStateOf(period.payments.isEmpty() && period.remainingAmount > 0)
    }

    val defaultNewAmount = if (period.remainingAmount > 0) {
        if (period.remainingAmount % 1.0 == 0.0) period.remainingAmount.toInt().toString() else period.remainingAmount.toString()
    } else {
        if (period.fee % 1.0 == 0.0) period.fee.toInt().toString() else period.fee.toString()
    }
    var newAmountInput by remember { mutableStateOf(defaultNewAmount) }
    var newDateInput by remember { mutableStateOf(LocalDate.now().toString()) }
    var newNotesInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("edit_period_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Strings.editPeriodDialogTitle(
                            currentLanguage,
                            BengaliFormatter.formatDigits(period.periodNumber, currentLanguage)
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                StatusBadge(status = period.status, lang = currentLanguage)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Study Dates
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.studyDatesSection(currentLanguage),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        AppOutlinedTextField(
                            value = startDateInput,
                            onValueChange = { startDateInput = it },
                            label = { Text(Strings.studyStartDateLabel(currentLanguage)) },
                            singleLine = true,
                            placeholder = { Text("YYYY-MM-DD (e.g. 2026-01-15)") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    showDatePicker(startDateInput) { startDateInput = it }
                                }) {
                                    Icon(
                                        Icons.Default.CalendarMonth,
                                        contentDescription = Strings.studyStartDateLabel(currentLanguage),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("period_edit_start_date_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Strings.studyEndDateLabel(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val calcStart = try { LocalDate.parse(startDateInput.trim()) } catch (_: Exception) { period.startDate }
                            Text(
                                text = BengaliFormatter.formatPeriodRange(calcStart, calculatedEndDate, currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.subsequentCycleNotice(currentLanguage),
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // Section 2: Payments
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = StatusPaid,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.paymentDetailsSection(currentLanguage),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = "${Strings.feeColon(currentLanguage)} ${BengaliFormatter.formatCurrency(period.fee, currentLanguage)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Existing Payments
                        val visiblePayments = editablePayments.filter { it.original.id !in deletedPaymentIds }
                        if (visiblePayments.isEmpty() && !showAddNewPayment) {
                            Text(
                                text = Strings.noPaymentsForThisPeriod(currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            visiblePayments.forEachIndexed { idx, item ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = Strings.paymentEntryTitle(
                                                    currentLanguage,
                                                    BengaliFormatter.formatDigits(idx + 1, currentLanguage)
                                                ),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = StatusPaid
                                            )
                                            IconButton(
                                                onClick = { deletedPaymentIds.add(item.original.id) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = Strings.deletePaymentAction(currentLanguage),
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            AppOutlinedTextField(
                                                value = item.amount,
                                                onValueChange = { item.amount = it },
                                                label = { Text(Strings.paymentAmountLabel(currentLanguage)) },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            AppOutlinedTextField(
                                                value = item.date,
                                                onValueChange = { item.date = it },
                                                label = { Text(Strings.paymentDateLabel(currentLanguage)) },
                                                singleLine = true,
                                                trailingIcon = {
                                                    IconButton(onClick = {
                                                        showDatePicker(item.date) { item.date = it }
                                                    }) {
                                                        Icon(
                                                            Icons.Default.CalendarMonth,
                                                            contentDescription = Strings.paymentDateLabel(currentLanguage),
                                                            tint = StatusPaid,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.weight(1.2f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        AppOutlinedTextField(
                                            value = item.notes,
                                            onValueChange = { item.notes = it },
                                            label = { Text(Strings.paymentNotesLabel(currentLanguage)) },
                                            singleLine = true,
                                            placeholder = { Text("নগদ / বিকাশ / অন্যান্য...") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Add New Payment Expansion
                        if (showAddNewPayment) {
                            Surface(
                                color = StatusPaid.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = Strings.addNewPaymentBtn(currentLanguage),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusPaid
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        AppOutlinedTextField(
                                            value = newAmountInput,
                                            onValueChange = { newAmountInput = it },
                                            label = { Text(Strings.paymentAmountLabel(currentLanguage)) },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        AppOutlinedTextField(
                                            value = newDateInput,
                                            onValueChange = { newDateInput = it },
                                            label = { Text(Strings.paymentDateLabel(currentLanguage)) },
                                            singleLine = true,
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    showDatePicker(newDateInput) { newDateInput = it }
                                                }) {
                                                    Icon(
                                                        Icons.Default.CalendarMonth,
                                                        contentDescription = Strings.paymentDateLabel(currentLanguage),
                                                        tint = StatusPaid,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            },
                                            modifier = Modifier.weight(1.2f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    AppOutlinedTextField(
                                        value = newNotesInput,
                                        onValueChange = { newNotesInput = it },
                                        label = { Text(Strings.paymentNotesLabel(currentLanguage)) },
                                        singleLine = true,
                                        placeholder = { Text("নগদ / বিকাশ / অন্যান্য...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = { showAddNewPayment = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(Strings.addNewPaymentBtn(currentLanguage), style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalStartDate = startDateInput.trim().ifEmpty { period.startDate.toString() }
                    val pStart = try { LocalDate.parse(finalStartDate) } catch (_: Exception) { period.startDate }
                    val pEnd = pStart.plusMonths(1).minusDays(1)

                    val modifiedPayments = editablePayments
                        .filter { it.original.id !in deletedPaymentIds }
                        .map { item ->
                            val amt = item.amount.toDoubleOrNull() ?: item.original.amount
                            val dt = item.date.trim().ifEmpty { item.original.paymentDate }
                            item.original.copy(
                                amount = amt,
                                paymentDate = dt,
                                notes = item.notes.trim(),
                                periodStartDate = pStart.toString(),
                                periodEndDate = pEnd.toString()
                            )
                        }

                    val newPayment = if (showAddNewPayment && newAmountInput.isNotBlank()) {
                        val amt = newAmountInput.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            PaymentEntity(
                                studentId = period.studentId,
                                periodIndex = period.index,
                                periodStartDate = pStart.toString(),
                                periodEndDate = pEnd.toString(),
                                amount = amt,
                                paymentDate = newDateInput.trim().ifEmpty { LocalDate.now().toString() },
                                notes = newNotesInput.trim()
                            )
                        } else null
                    } else null

                    onSave(finalStartDate, modifiedPayments, newPayment, deletedPaymentIds.toList())
                },
                modifier = Modifier.testTag("period_edit_save_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(Strings.savePeriodChanges(currentLanguage), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag("period_edit_cancel_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(Strings.cancel(currentLanguage))
            }
        }
    )
}
