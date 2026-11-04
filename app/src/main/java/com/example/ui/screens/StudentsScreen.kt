package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.StudentEntity
import com.example.domain.StudentBillingSummary
import com.example.domain.StudentFilter
import com.example.ui.components.EmptyState
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.StatusPartial
import com.example.util.AppLanguage
import com.example.util.BengaliFormatter
import com.example.util.Strings
import java.time.LocalDate

import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(
    students: List<StudentBillingSummary>,
    allStudentSummaries: List<StudentBillingSummary> = emptyList(),
    searchQuery: String,
    selectedFilter: StudentFilter,
    selectedYear: Int = LocalDate.now().year,
    availableYears: List<Int> = listOf(2027, 2026, 2025, 2024),
    currentLanguage: AppLanguage = AppLanguage.BN,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (StudentFilter) -> Unit,
    onYearSelected: (Int) -> Unit = {},
    onCopyStudentsFromYear: (Int) -> Unit = {},
    onStudentClick: (Long) -> Unit,
    onAddStudentClick: () -> Unit,
    onEditStudentClick: (StudentEntity) -> Unit,
    onDeleteStudentClick: (StudentEntity) -> Unit,
    onRecordPaymentClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showYearPickerDialog by remember { mutableStateOf(false) }
    var showCopyConfirmDialog by remember { mutableStateOf(false) }

    val yrDigits = BengaliFormatter.formatDigits(selectedYear, currentLanguage)
    val prevYear = selectedYear - 1
    val prevYearDigits = BengaliFormatter.formatDigits(prevYear, currentLanguage)

    val countSource = if (allStudentSummaries.isNotEmpty()) allStudentSummaries else students
    val allCount = countSource.size
    val paidCount = countSource.count { it.totalDue == 0.0 && (it.currentPeriod == null || it.currentPeriod.remainingAmount <= 0.0) }
    val pendingCount = countSource.count { it.totalDue == 0.0 && (it.currentPeriod?.remainingAmount ?: 0.0) > 0.0 }
    val overdueCount = countSource.count { it.totalDue > 0.0 }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddStudentClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("students_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = Strings.newStudent(currentLanguage))
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("students_screen")
        ) {
            // Academic Year Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("students_academic_year_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
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
                                text = Strings.studentListForYear(currentLanguage, yrDigits),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            val unitStr = if (currentLanguage == AppLanguage.BN) "জন শিক্ষার্থী" else "students"
                            Text(
                                text = "${Strings.totalStudents(currentLanguage)}: ${BengaliFormatter.formatDigits(allCount, currentLanguage)} $unitStr",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Surface(
                        onClick = { showYearPickerDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.22f),
                        modifier = Modifier.testTag("students_select_year_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = yrDigits,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Switch Year",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                com.example.ui.components.AppOutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text(Strings.searchStudentPlaceholder(currentLanguage)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_search_bar")
                )
            }

            // Filter Chips Row (Paid, Pending, Overdue, All)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(StudentFilter.entries) { filter ->
                    val isSelected = filter == selectedFilter
                    val count = when (filter) {
                        StudentFilter.ALL -> allCount
                        StudentFilter.PAID -> paidCount
                        StudentFilter.PENDING -> pendingCount
                        StudentFilter.OVERDUE -> overdueCount
                    }
                    val countStr = BengaliFormatter.formatDigits(count, currentLanguage)

                    val (chipIcon, iconTint) = when (filter) {
                        StudentFilter.ALL -> Icons.Default.Groups to MaterialTheme.colorScheme.primary
                        StudentFilter.PAID -> Icons.Default.CheckCircle to StatusPaid
                        StudentFilter.PENDING -> Icons.Default.Schedule to StatusPartial
                        StudentFilter.OVERDUE -> Icons.Default.ErrorOutline to StatusDue
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChange(filter) },
                        leadingIcon = {
                            Icon(
                                imageVector = chipIcon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else iconTint
                            )
                        },
                        label = {
                            Text(
                                text = "${Strings.filterLabel(filter, currentLanguage)} ($countStr)",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("filter_chip_${filter.name.lowercase()}")
                    )
                }
            }

            // Student Count summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.totalShowing(currentLanguage, BengaliFormatter.formatDigits(students.size, currentLanguage)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Students List
            if (students.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EmptyState(
                        icon = Icons.Default.Person,
                        title = if (searchQuery.isNotEmpty()) Strings.noStudentsFound(currentLanguage) else Strings.noStudentsInYear(currentLanguage, yrDigits),
                        subtitle = if (searchQuery.isNotEmpty()) Strings.tryOtherName(currentLanguage) else Strings.tapFabToAdd(currentLanguage),
                        actionText = if (searchQuery.isEmpty()) Strings.addStudentForYear(currentLanguage, yrDigits) else null,
                        onActionClick = if (searchQuery.isEmpty()) onAddStudentClick else null
                    )

                    if (searchQuery.isEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = { showCopyConfirmDialog = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GroupAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(Strings.copyStudentsFromPrevYear(currentLanguage, prevYearDigits))
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(students, key = { it.student.id }) { summary ->
                        StudentListItemCard(
                            summary = summary,
                            currentLanguage = currentLanguage,
                            onClick = { onStudentClick(summary.student.id) },
                            onEditClick = { onEditStudentClick(summary.student) },
                            onDeleteClick = { onDeleteStudentClick(summary.student) },
                            onRecordPaymentClick = { onRecordPaymentClick(summary.student.id) },
                            onCallClick = { phone ->
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                context.startActivity(intent)
                            },
                            onMessageClick = { phone ->
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
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

    if (showCopyConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCopyConfirmDialog = false },
            title = {
                Text(
                    text = Strings.copyStudentsDialogTitle(currentLanguage),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = Strings.copyStudentsDialogMsg(currentLanguage, prevYearDigits, yrDigits),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCopyStudentsFromYear(prevYear)
                        showCopyConfirmDialog = false
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(Strings.confirmCopy(currentLanguage))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCopyConfirmDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(Strings.cancel(currentLanguage))
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
fun StudentListItemCard(
    summary: StudentBillingSummary,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRecordPaymentClick: () -> Unit,
    onCallClick: (String) -> Unit,
    onMessageClick: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val startDateObj = try {
        LocalDate.parse(summary.student.startDate)
    } catch (e: Exception) {
        null
    }
    val startDateStr = if (startDateObj != null) BengaliFormatter.formatDateDdMmYyyy(startDateObj, currentLanguage) else summary.student.startDate

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("student_item_${summary.student.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Avatar, Name, Options Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    StudentAvatar(name = summary.student.name, size = 42)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = summary.student.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (summary.student.notes.isNotBlank()) {
                            Text(
                                text = summary.student.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(Strings.recordPayment(currentLanguage)) },
                            leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onRecordPaymentClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(Strings.editInfo(currentLanguage)) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEditClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(Strings.deleteStudent(currentLanguage), color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menuExpanded = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Info Grid: Fee, Start Date, Due/Paid status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Strings.monthlyFee(currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = BengaliFormatter.formatCurrency(summary.student.monthlyFee, currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column {
                    Text(
                        text = Strings.startDate(currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = startDateStr,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Due, Pending, or Paid Badge
                Column(horizontalAlignment = Alignment.End) {
                    if (summary.totalDue > 0) {
                        Text(
                            text = Strings.overdueLabel(currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusDue,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = BengaliFormatter.formatCurrency(summary.totalDue, currentLanguage),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StatusDue
                        )
                    } else if ((summary.currentPeriod?.remainingAmount ?: 0.0) > 0.0) {
                        Text(
                            text = Strings.pendingLabel(currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusPartial,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = BengaliFormatter.formatCurrency(summary.currentPeriod?.remainingAmount ?: 0.0, currentLanguage),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StatusPartial
                        )
                    } else {
                        Surface(
                            color = StatusPaid.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = Strings.paidLabel(currentLanguage),
                                color = StatusPaid,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Last Payment Date (if any payment was made)
            summary.lastPayment?.let { lp ->
                Spacer(modifier = Modifier.height(8.dp))
                val lpDate = try { LocalDate.parse(lp.paymentDate) } catch (e: Exception) { null }
                val lpDateStr = if (lpDate != null) BengaliFormatter.formatDateDdMmYyyy(lpDate, currentLanguage) else lp.paymentDate
                val lpMonthStr = if (lpDate != null) " (${BengaliFormatter.formatDateWithMonthName(lpDate, currentLanguage)})" else ""
                Surface(
                    color = StatusPaid.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${Strings.lastPaymentDateLabel(currentLanguage)} $lpDateStr$lpMonthStr • ${BengaliFormatter.formatCurrency(lp.amount, currentLanguage)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusPaid,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Call / SMS if phone is present
            if (summary.student.phone.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${Strings.phoneLabel(currentLanguage)} ${summary.student.phone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            onClick = { onCallClick(summary.student.phone) },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = Strings.call(currentLanguage),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(16.dp)
                            )
                        }

                        Surface(
                            onClick = { onMessageClick(summary.student.phone) },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Message,
                                contentDescription = Strings.message(currentLanguage),
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
