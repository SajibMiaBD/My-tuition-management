package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AttendanceEntity
import com.example.data.StudentEntity
import com.example.ui.components.AppOutlinedTextField
import com.example.ui.components.EmptyState
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusPaid
import com.example.util.AppLanguage
import com.example.util.BengaliFormatter
import com.example.util.Strings
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    students: List<StudentEntity>,
    allAttendance: List<AttendanceEntity>,
    selectedYear: Int = LocalDate.now().year,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onRecordAttendance: (studentId: Long, date: String, isPresent: Boolean) -> Unit,
    onMarkAllAttendance: (date: String, isPresent: Boolean) -> Unit,
    onStudentClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBackClick()
    }

    val context = LocalContext.current
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    var expandedDateLog by remember { mutableStateOf<String?>(null) }
    var studentSearchQuery by remember { mutableStateOf("") }

    val filteredStudents = remember(students, studentSearchQuery) {
        if (studentSearchQuery.isBlank()) {
            students
        } else {
            val q = studentSearchQuery.trim()
            students.filter { it.name.contains(q, ignoreCase = true) || it.phone.contains(q) }
        }
    }

    val selectedDateStr = selectedDate.toString()
    val isTodaySelected = selectedDate == today

    // Attendance map for selected date: studentId -> isPresent
    val attendanceForSelectedDate = remember(allAttendance, selectedDateStr) {
        allAttendance.filter { it.date == selectedDateStr }.associateBy({ it.studentId }, { it.isPresent })
    }

    val presentCountForSelectedDate = remember(students, attendanceForSelectedDate) {
        students.count { attendanceForSelectedDate[it.id] == true }
    }
    val absentCountForSelectedDate = remember(students, attendanceForSelectedDate) {
        students.count { attendanceForSelectedDate[it.id] == false }
    }
    val unmarkedCountForSelectedDate = students.size - presentCountForSelectedDate - absentCountForSelectedDate

    val attendanceRate = if (students.isNotEmpty()) {
        (presentCountForSelectedDate.toFloat() / students.size.toFloat() * 100f).toInt()
    } else 0

    // Date picker dialog
    val openDatePicker = {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        ).show()
    }

    // All distinct dates that have recorded attendance, sorted latest first
    val recordedDates = remember(allAttendance, students) {
        val studentIds = students.map { it.id }.toSet()
        allAttendance
            .filter { it.studentId in studentIds }
            .map { it.date }
            .distinct()
            .sortedDescending()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = Strings.studentAttendanceTitle(currentLanguage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = Strings.attendanceSubtitle(currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Strings.back(currentLanguage)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("attendance_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // 1. TOP SECTION: Premium Daily Attendance Marking System
            // ==========================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("daily_attendance_marking_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FactCheck,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.BN) "দৈনিক হাজিরা ইনপুট" else "Daily Attendance Input",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.BN) "উপস্থিত অথবা অনুপস্থিত নির্বাচন করুন" else "Toggle Present or Absent",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (!isTodaySelected) {
                                Surface(
                                    onClick = { selectedDate = today },
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Today,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (currentLanguage == AppLanguage.BN) "আজকে যান" else "Go Today",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Date Selector Bar with Prev, Next, and Calendar
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { selectedDate = selectedDate.minusDays(1) },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day")
                                }

                                Surface(
                                    onClick = openDatePicker,
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    shadowElevation = 1.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val dayName = if (currentLanguage == AppLanguage.BN) {
                                            when (selectedDate.dayOfWeek) {
                                                java.time.DayOfWeek.SATURDAY -> "শনিবার"
                                                java.time.DayOfWeek.SUNDAY -> "রবিবার"
                                                java.time.DayOfWeek.MONDAY -> "সোমবার"
                                                java.time.DayOfWeek.TUESDAY -> "মঙ্গলবার"
                                                java.time.DayOfWeek.WEDNESDAY -> "বুধবার"
                                                java.time.DayOfWeek.THURSDAY -> "বৃহস্পতিবার"
                                                java.time.DayOfWeek.FRIDAY -> "শুক্রবার"
                                            }
                                        } else {
                                            selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                        }
                                        val dateFormatted = BengaliFormatter.formatDateDdMmYyyy(selectedDate, currentLanguage)
                                        Text(
                                            text = "$dateFormatted ($dayName)",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { selectedDate = selectedDate.plusDays(1) },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Day")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Summary Bar for Selected Date
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(StatusPaid)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${Strings.presentLabel(currentLanguage)}: ${BengaliFormatter.formatDigits(presentCountForSelectedDate, currentLanguage)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusPaid
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(StatusDue)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${Strings.absentLabel(currentLanguage)}: ${BengaliFormatter.formatDigits(absentCountForSelectedDate, currentLanguage)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusDue
                                    )
                                }

                                Text(
                                    text = "${BengaliFormatter.formatDigits(attendanceRate, currentLanguage)}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bulk Actions: Mark All Present / Mark All Absent
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onMarkAllAttendance(selectedDateStr, true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mark_all_present_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusPaid),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.markAllPresent(currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = { onMarkAllAttendance(selectedDateStr, false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mark_all_absent_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = StatusDue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Strings.markAllAbsent(currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    color = StatusDue,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Search Bar for Students
                        AppOutlinedTextField(
                            value = studentSearchQuery,
                            onValueChange = { studentSearchQuery = it },
                            placeholder = { Text(Strings.searchStudentPlaceholder(currentLanguage)) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            trailingIcon = {
                                if (studentSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { studentSearchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("attendance_student_search_bar")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Student attendance list
                        if (filteredStudents.isEmpty()) {
                            Text(
                                text = if (studentSearchQuery.isNotBlank()) {
                                    if (currentLanguage == AppLanguage.BN) "কোনো শিক্ষার্থী খুঁজে পাওয়া যায়নি" else "No matching students found"
                                } else {
                                    Strings.noStudentsFound(currentLanguage)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                filteredStudents.forEach { student ->
                                    val status = attendanceForSelectedDate[student.id]

                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = 8.dp)
                                                    .clickable { onStudentClick(student.id) }
                                            ) {
                                                StudentAvatar(name = student.name, size = 38)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = student.name,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 1,
                                                        softWrap = false,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    val statusText = when (status) {
                                                        true -> Strings.presentLabel(currentLanguage)
                                                        false -> Strings.absentLabel(currentLanguage)
                                                        null -> Strings.unmarkedLabel(currentLanguage)
                                                    }
                                                    val statusColor = when (status) {
                                                        true -> StatusPaid
                                                        false -> StatusDue
                                                        null -> MaterialTheme.colorScheme.onSurfaceVariant
                                                    }
                                                    Text(
                                                        text = statusText,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = statusColor,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }

                                            // Balanced, uniform Present & Absent Buttons
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Present Button
                                                Surface(
                                                    onClick = { onRecordAttendance(student.id, selectedDateStr, true) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (status == true) StatusPaid else StatusPaid.copy(alpha = 0.12f),
                                                    modifier = Modifier
                                                        .width(96.dp)
                                                        .testTag("present_btn_${student.id}")
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = if (status == true) Color.White else StatusPaid,
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = Strings.presentLabel(currentLanguage),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1,
                                                            softWrap = false,
                                                            color = if (status == true) Color.White else StatusPaid
                                                        )
                                                    }
                                                }

                                                // Absent Button
                                                Surface(
                                                    onClick = { onRecordAttendance(student.id, selectedDateStr, false) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (status == false) StatusDue else StatusDue.copy(alpha = 0.12f),
                                                    modifier = Modifier
                                                        .width(96.dp)
                                                        .testTag("absent_btn_${student.id}")
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = null,
                                                            tint = if (status == false) Color.White else StatusDue,
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = Strings.absentLabel(currentLanguage),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1,
                                                            softWrap = false,
                                                            color = if (status == false) Color.White else StatusDue
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 2. MIDDLE SECTION: Interactive Visual Attendance Analytics & Multi-View Chart
            // ==========================================
            item {
                com.example.ui.components.ModernAttendanceVisualChart(
                    students = students,
                    allAttendance = allAttendance,
                    selectedDate = selectedDate,
                    currentLanguage = currentLanguage,
                    onDateSelected = { selectedDate = it },
                    onLoadAndEditDate = { selectedDate = it },
                    onStudentClick = onStudentClick
                )
            }

            // ==========================================
            // 3. BOTTOM SECTION: Date-wise History & Student Attendance Breakdown
            // ==========================================
            item {
                Text(
                    text = Strings.attendanceHistoryLog(currentLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            if (recordedDates.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.FactCheck,
                        title = Strings.noAttendanceRecords(currentLanguage),
                        subtitle = if (currentLanguage == AppLanguage.BN) "উপরের অংশে আজকের উপস্থিতি বা অনুপস্থিতি রেকর্ড করুন।" else "Mark today's attendance in the section above."
                    )
                }
            } else {
                items(recordedDates) { dateStr ->
                    val dateObj = try { LocalDate.parse(dateStr) } catch (_: Exception) { null }
                    val dayAttendanceMap = remember(allAttendance, dateStr) {
                        allAttendance.filter { it.date == dateStr }.associateBy({ it.studentId }, { it.isPresent })
                    }
                    val presentStudents = remember(students, dayAttendanceMap) {
                        students.filter { dayAttendanceMap[it.id] == true }
                    }
                    val absentStudents = remember(students, dayAttendanceMap) {
                        students.filter { dayAttendanceMap[it.id] == false }
                    }

                    val isExpanded = expandedDateLog == dateStr

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedDateLog = if (isExpanded) null else dateStr
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (dateStr == selectedDateStr) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = if (dateStr == selectedDateStr) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        val displayDate = if (dateObj != null) BengaliFormatter.formatDateDdMmYyyy(dateObj, currentLanguage) else dateStr
                                        Text(
                                            text = displayDate,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        val totalStudentsCount = students.size
                                        val sUnit = if (currentLanguage == AppLanguage.BN) " জন" else ""
                                        Text(
                                            text = "${Strings.presentLabel(currentLanguage)}: ${BengaliFormatter.formatDigits(presentStudents.size, currentLanguage)}/${BengaliFormatter.formatDigits(totalStudentsCount, currentLanguage)}$sUnit",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = StatusPaid.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "+${BengaliFormatter.formatDigits(presentStudents.size, currentLanguage)}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusPaid,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    if (absentStudents.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = StatusDue.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "-${BengaliFormatter.formatDigits(absentStudents.size, currentLanguage)}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = StatusDue,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Expand",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Expanded details: List of who was present and who was absent
                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                Spacer(modifier = Modifier.height(10.dp))

                                // Present students list
                                if (presentStudents.isNotEmpty()) {
                                    val countSuffix = if (currentLanguage == AppLanguage.BN) " (${BengaliFormatter.formatDigits(presentStudents.size, currentLanguage)} জন):" else " (${presentStudents.size}):"
                                    Text(
                                        text = "✓ ${Strings.presentLabel(currentLanguage)}$countSuffix",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusPaid
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = presentStudents.joinToString(", ") { it.name },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // Absent students list
                                if (absentStudents.isNotEmpty()) {
                                    val countSuffix = if (currentLanguage == AppLanguage.BN) " (${BengaliFormatter.formatDigits(absentStudents.size, currentLanguage)} জন):" else " (${absentStudents.size}):"
                                    Text(
                                        text = "✗ ${Strings.absentLabel(currentLanguage)}$countSuffix",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusDue
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = absentStudents.joinToString(", ") { it.name },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Quick button to load this date into editor
                                if (dateObj != null && selectedDate != dateObj) {
                                    OutlinedButton(
                                        onClick = { selectedDate = dateObj },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Today, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (currentLanguage == AppLanguage.BN) "এই তারিখের হাজিরা উপরে এডিট করুন" else "Edit this date's attendance above",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
