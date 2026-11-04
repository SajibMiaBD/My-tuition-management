package com.example.ui.components

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.StudentEntity
import com.example.util.AppLanguage
import com.example.util.BengaliFormatter
import com.example.util.Strings
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddEditStudentDialog(
    studentToEdit: StudentEntity? = null,
    currentLanguage: AppLanguage = AppLanguage.BN,
    initialAcademicYear: Int = LocalDate.now().year,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, startDate: String, monthlyFee: Double, notes: String, academicYear: Int, id: Long) -> Unit
) {
    val isEditMode = studentToEdit != null

    var name by remember(studentToEdit) { mutableStateOf(studentToEdit?.name ?: "") }
    var phone by remember(studentToEdit) { mutableStateOf(studentToEdit?.phone ?: "") }
    var monthlyFeeStr by remember(studentToEdit) {
        mutableStateOf(
            if (studentToEdit != null) studentToEdit.monthlyFee.toInt().toString() else ""
        )
    }
    var notes by remember(studentToEdit) { mutableStateOf(studentToEdit?.notes ?: "") }

    var academicYear by remember(studentToEdit, initialAcademicYear) {
        val yr = if (studentToEdit != null) {
            if (studentToEdit.academicYear > 0) studentToEdit.academicYear else try {
                LocalDate.parse(studentToEdit.startDate).year
            } catch (_: Exception) {
                initialAcademicYear
            }
        } else {
            initialAcademicYear
        }
        mutableIntStateOf(yr)
    }

    var selectedDate by remember(studentToEdit, initialAcademicYear) {
        val initialDate = if (studentToEdit != null) {
            try {
                LocalDate.parse(studentToEdit.startDate)
            } catch (_: Exception) {
                LocalDate.of(initialAcademicYear, 1, 1)
            }
        } else {
            val now = LocalDate.now()
            if (initialAcademicYear == now.year) {
                now
            } else {
                try {
                    LocalDate.of(initialAcademicYear, now.monthValue, now.dayOfMonth.coerceAtMost(28))
                } catch (_: Exception) {
                    LocalDate.of(initialAcademicYear, 1, 1)
                }
            }
        }
        mutableStateOf(initialDate)
    }

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var feeError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditMode) Strings.editStudentTitle(currentLanguage) else Strings.addNewStudentTitle(currentLanguage),
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
                // Academic Year Badge Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.academicYearLabel(currentLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = BengaliFormatter.formatDigits(academicYear, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Name Field
                AppOutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text(Strings.studentNameRequired(currentLanguage)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    isError = nameError,
                    supportingText = {
                        if (nameError) Text(Strings.nameRequiredError(currentLanguage))
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone Field
                AppOutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(Strings.phoneOptional(currentLanguage)) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_phone_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Start Date Header
                Text(
                    text = Strings.tuitionStartDateRequired(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Date display button
                OutlinedButton(
                    onClick = { showDatePickerDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pick_start_date_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${BengaliFormatter.formatDateDdMmYyyy(selectedDate, currentLanguage)} (${BengaliFormatter.formatDateWithMonthName(selectedDate, currentLanguage)})",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Quick Date Preset Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val today = LocalDate.now()
                    val presets = listOf(
                        (if (currentLanguage == AppLanguage.BN) "আজকে" else "Today") to today,
                        (if (currentLanguage == AppLanguage.BN) "১ম তারিখ" else "1st") to today.withDayOfMonth(1),
                        (if (currentLanguage == AppLanguage.BN) "১০ তারিখ" else "10th") to today.withDayOfMonth(10.coerceAtMost(today.lengthOfMonth())),
                        (if (currentLanguage == AppLanguage.BN) "১৫ তারিখ" else "15th") to today.withDayOfMonth(15.coerceAtMost(today.lengthOfMonth())),
                        (if (currentLanguage == AppLanguage.BN) "তারিখ বদলান" else "Change") to null
                    )

                    presets.forEach { (label, presetDate) ->
                        if (presetDate != null) {
                            val isSelected = selectedDate == presetDate
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDate = presetDate },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                shape = RoundedCornerShape(8.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        } else {
                            FilterChip(
                                selected = false,
                                onClick = { showDatePickerDialog = true },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                leadingIcon = { Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Monthly Tuition Fee
                AppOutlinedTextField(
                    value = monthlyFeeStr,
                    onValueChange = {
                        monthlyFeeStr = it
                        val parsed = BengaliFormatter.parseAmount(it)
                        if (parsed != null && parsed > 0) feeError = false
                    },
                    label = { Text(Strings.monthlyTuitionFeeRequired(currentLanguage)) },
                    leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                    prefix = { Text("৳ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = feeError,
                    supportingText = {
                        if (feeError) Text(Strings.feeRequiredError(currentLanguage))
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_fee_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Quick Fee Preset Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val feePresets = listOf(500, 1000, 1500, 2000, 2500, 3000)
                    feePresets.forEach { presetFee ->
                        val feeBn = BengaliFormatter.formatCurrency(presetFee.toDouble(), currentLanguage)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    monthlyFeeStr = presetFee.toString()
                                    feeError = false
                                },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = feeBn,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes Field
                AppOutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(Strings.notesOptional(currentLanguage)) },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_notes_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fee = BengaliFormatter.parseAmount(monthlyFeeStr)
                    val cleanName = name.trim()
                    if (cleanName.isBlank()) {
                        nameError = true
                    }
                    if (fee == null || fee <= 0) {
                        feeError = true
                    }
                    if (cleanName.isNotBlank() && fee != null && fee > 0) {
                        val cleanPhone = BengaliFormatter.toEnglishDigits(phone.trim())
                        onSave(
                            cleanName,
                            cleanPhone,
                            selectedDate.toString(),
                            fee,
                            notes.trim(),
                            academicYear,
                            studentToEdit?.id ?: 0L
                        )
                    }
                },
                modifier = Modifier.testTag("save_student_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isEditMode) Strings.update(currentLanguage) else Strings.save(currentLanguage))
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

    // Pure Compose Date Picker Dialog (no dependency on legacy android.app.DatePickerDialog)
    if (showDatePickerDialog) {
        ComposeDatePickerDialog(
            initialDate = selectedDate,
            currentLanguage = currentLanguage,
            onDismiss = { showDatePickerDialog = false },
            onDateSelected = { newDate ->
                selectedDate = newDate
                showDatePickerDialog = false
            }
        )
    }
}

/**
 * 100% native Compose Date Selection Dialog.
 * Allows easy Day, Month, Year selection with steppers and quick buttons, completely immune to BadTokenException.
 */
@Composable
fun ComposeDatePickerDialog(
    initialDate: LocalDate,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var year by remember { mutableIntStateOf(initialDate.year) }
    var month by remember { mutableIntStateOf(initialDate.monthValue) }
    var day by remember { mutableIntStateOf(initialDate.dayOfMonth) }

    val monthsBn = arrayOf(
        "", "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )
    val monthsEn = arrayOf(
        "", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val maxDays = try {
        LocalDate.of(year, month, 1).lengthOfMonth()
    } catch (_: Exception) {
        31
    }
    if (day > maxDays) day = maxDays

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (currentLanguage == AppLanguage.BN) "তারিখ নির্বাচন করুন" else "Select Date",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Selected Date Preview Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val previewDate = LocalDate.of(year, month, day)
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = BengaliFormatter.formatDateDdMmYyyy(previewDate, currentLanguage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = BengaliFormatter.formatDateWithMonthName(previewDate, currentLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Day Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.BN) "দিন (Day):" else "Day:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (day > 1) day-- else day = maxDays },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease day")
                        }
                        Text(
                            text = BengaliFormatter.formatDigits(day, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { if (day < maxDays) day++ else day = 1 },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase day")
                        }
                    }
                }

                // Month Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.BN) "মাস (Month):" else "Month:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (month > 1) month-- else month = 12 },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease month")
                        }
                        val monthName = if (currentLanguage == AppLanguage.BN) monthsBn[month] else monthsEn[month]
                        Text(
                            text = monthName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { if (month < 12) month++ else month = 1 },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase month")
                        }
                    }
                }

                // Year Stepper (supports 2000 to 2099)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.BN) "সাল (Year):" else "Year:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (year > 2000) year-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease year")
                        }
                        Text(
                            text = BengaliFormatter.formatDigits(year, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { if (year < 2099) year++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase year")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalDate = LocalDate.of(year, month, day.coerceAtMost(maxDays))
                    onDateSelected(finalDate)
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(Strings.ok(currentLanguage))
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
        shape = RoundedCornerShape(18.dp)
    )
}
