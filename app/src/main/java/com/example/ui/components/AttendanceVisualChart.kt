package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AttendanceEntity
import com.example.data.StudentEntity
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusPaid
import com.example.util.AppLanguage
import com.example.util.BengaliFormatter
import com.example.util.Strings
import java.time.LocalDate

data class DayAttendanceStat(
    val date: LocalDate,
    val presentCount: Int,
    val absentCount: Int,
    val totalStudents: Int,
    val rate: Int,
    val isMarked: Boolean
)

data class StudentAttendanceRank(
    val student: StudentEntity,
    val presentDays: Int,
    val absentDays: Int,
    val totalDays: Int,
    val rate: Int
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModernAttendanceVisualChart(
    students: List<StudentEntity>,
    allAttendance: List<AttendanceEntity>,
    selectedDate: LocalDate,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onDateSelected: (LocalDate) -> Unit,
    onLoadAndEditDate: (LocalDate) -> Unit,
    onStudentClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var rangeDays by remember { mutableIntStateOf(14) } // 7, 14, 30
    var activeViewTab by remember { mutableIntStateOf(0) } // 0: Trend Graph, 1: Calendar Heatmap, 2: Student Rankings
    val today = remember { LocalDate.now() }

    // Precompute stats for each day in selected range
    val statsList = remember(allAttendance, students, rangeDays, today) {
        val studentIds = students.map { it.id }.toSet()
        (rangeDays - 1 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val dateStr = date.toString()
            val dayRecords = allAttendance.filter { it.date == dateStr && it.studentId in studentIds }
            val map = dayRecords.associateBy({ it.studentId }, { it.isPresent })

            val pCount = students.count { map[it.id] == true }
            val aCount = students.count { map[it.id] == false }
            val isMarked = dayRecords.isNotEmpty()
            val rate = if (students.isNotEmpty() && isMarked) {
                ((pCount.toFloat() / students.size.toFloat()) * 100f).toInt()
            } else 0

            DayAttendanceStat(
                date = date,
                presentCount = pCount,
                absentCount = aCount,
                totalStudents = students.size,
                rate = rate,
                isMarked = isMarked
            )
        }
    }

    // High level metrics across the range
    val markedDays = remember(statsList) { statsList.filter { it.isMarked } }
    val totalSessionsHeld = markedDays.size
    val totalPresentSum = remember(markedDays) { markedDays.sumOf { it.presentCount } }
    val totalAbsentSum = remember(markedDays) { markedDays.sumOf { it.absentCount } }
    val totalPossibleAttendance = totalPresentSum + totalAbsentSum
    val avgAttendanceRate = remember(totalPresentSum, totalPossibleAttendance) {
        if (totalPossibleAttendance > 0) {
            ((totalPresentSum.toFloat() / totalPossibleAttendance.toFloat()) * 100f).toInt()
        } else if (markedDays.isNotEmpty()) {
            (markedDays.map { it.rate }.average()).toInt()
        } else 0
    }

    // Student ranks in this window
    val studentRanks = remember(allAttendance, students, statsList) {
        val rangeDates = statsList.filter { it.isMarked }.map { it.date.toString() }.toSet()
        students.map { student ->
            val studentAtt = allAttendance.filter { it.studentId == student.id && it.date in rangeDates }
            val pDays = studentAtt.count { it.isPresent }
            val aDays = studentAtt.count { !it.isPresent }
            val tDays = pDays + aDays
            val rate = if (tDays > 0) ((pDays.toFloat() / tDays.toFloat()) * 100f).toInt() else 0
            StudentAttendanceRank(
                student = student,
                presentDays = pDays,
                absentDays = aDays,
                totalDays = tDays,
                rate = rate
            )
        }.sortedWith(compareByDescending<StudentAttendanceRank> { it.rate }.thenByDescending { it.presentDays })
    }

    // Selected day breakdown
    val selectedStat = remember(statsList, selectedDate) {
        statsList.find { it.date == selectedDate } ?: DayAttendanceStat(
            date = selectedDate,
            presentCount = 0,
            absentCount = 0,
            totalStudents = students.size,
            rate = 0,
            isMarked = false
        )
    }

    // Detailed student attendance for selected day
    val selectedDayAttendanceMap = remember(allAttendance, selectedDate) {
        val sDateStr = selectedDate.toString()
        allAttendance.filter { it.date == sDateStr }.associateBy({ it.studentId }, { it.isPresent })
    }
    val selectedDayPresentStudents = remember(students, selectedDayAttendanceMap) {
        students.filter { selectedDayAttendanceMap[it.id] == true }
    }
    val selectedDayAbsentStudents = remember(students, selectedDayAttendanceMap) {
        students.filter { selectedDayAttendanceMap[it.id] == false }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("attendance_visual_chart_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ==========================================
            // 1. HEADER: Title, Badge & Timeframe Selector
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.attendanceVisualChart(currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = Strings.attendanceChartSubtitle(currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time range selector chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(7, 14, 30).forEach { days ->
                    val isSelected = rangeDays == days
                    val label = when (days) {
                        7 -> Strings.last7Days(currentLanguage)
                        14 -> Strings.last14Days(currentLanguage)
                        else -> Strings.thisMonthTab(currentLanguage)
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { rangeDays = days },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier.testTag("attendance_range_${days}_chip")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 2. EXECUTIVE SUMMARY TILES (3 High-Impact Cards)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tile 1: Average Attendance Rate
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = Strings.avgAttendanceRateLabel(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${BengaliFormatter.formatDigits(avgAttendanceRate, currentLanguage)}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val statusText = when {
                            avgAttendanceRate >= 85 -> if (currentLanguage == AppLanguage.BN) "চমৎকার" else "Excellent"
                            avgAttendanceRate >= 70 -> if (currentLanguage == AppLanguage.BN) "ভালো" else "Good"
                            avgAttendanceRate >= 50 -> if (currentLanguage == AppLanguage.BN) "চলনসই" else "Average"
                            else -> if (currentLanguage == AppLanguage.BN) "মনোযোগ দিন" else "Low"
                        }
                        val statusColor = when {
                            avgAttendanceRate >= 85 -> StatusPaid
                            avgAttendanceRate >= 70 -> Color(0xFF0284C7)
                            avgAttendanceRate >= 50 -> Color(0xFFE65100)
                            else -> StatusDue
                        }
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                // Tile 2: Sessions / Days Held
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = Strings.totalClassesHeldLabel(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = BengaliFormatter.formatDigits(totalSessionsHeld, currentLanguage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.BN) "দিন হাজিরা নেওয়া হয়েছে" else "days recorded",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Tile 3: Present vs Absent Totals
                Surface(
                    color = StatusPaid.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = Strings.presentRatioLabel(currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = BengaliFormatter.formatDigits(totalPresentSum, currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusPaid
                            )
                            Text(
                                text = " : ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = BengaliFormatter.formatDigits(totalAbsentSum, currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusDue
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        // Mini dual ratio bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        ) {
                            val pWeight = if (totalPossibleAttendance > 0) totalPresentSum.toFloat() / totalPossibleAttendance.toFloat() else 0.5f
                            if (pWeight > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(pWeight.coerceAtLeast(0.01f))
                                        .fillMaxHeight()
                                        .background(StatusPaid)
                                )
                            }
                            val aWeight = if (totalPossibleAttendance > 0) totalAbsentSum.toFloat() / totalPossibleAttendance.toFloat() else 0.5f
                            if (aWeight > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(aWeight.coerceAtLeast(0.01f))
                                        .fillMaxHeight()
                                        .background(StatusDue)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 3. VIEW MODE TAB SWITCHER: Graph, Heatmap, Ranking
            // ==========================================
            TabRow(
                selectedTabIndex = activeViewTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activeViewTab == 0,
                    onClick = { activeViewTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Strings.trendViewTab(currentLanguage), fontSize = 12.sp, fontWeight = if (activeViewTab == 0) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = activeViewTab == 1,
                    onClick = { activeViewTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Strings.heatmapViewTab(currentLanguage), fontSize = 12.sp, fontWeight = if (activeViewTab == 1) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = activeViewTab == 2,
                    onClick = { activeViewTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Strings.studentRankingTab(currentLanguage), fontSize = 12.sp, fontWeight = if (activeViewTab == 2) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 4. TAB CONTENTS
            // ==========================================
            when (activeViewTab) {
                0 -> {
                    // TAB 0: Premium Interactive Trend Bar Chart
                    AttendanceTrendBarChart(
                        statsList = statsList,
                        selectedDate = selectedDate,
                        currentLanguage = currentLanguage,
                        onDateSelected = onDateSelected
                    )
                }
                1 -> {
                    // TAB 1: Modern Calendar Heatmap Matrix
                    AttendanceCalendarHeatmap(
                        statsList = statsList,
                        selectedDate = selectedDate,
                        currentLanguage = currentLanguage,
                        onDateSelected = onDateSelected
                    )
                }
                2 -> {
                    // TAB 2: Student Attendance Leaderboard & Consistency
                    AttendanceStudentRankingList(
                        studentRanks = studentRanks,
                        currentLanguage = currentLanguage,
                        onStudentClick = onStudentClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 5. SELECTED DAY DEEP-DIVE INSPECTION CARD
            // ==========================================
            SelectedDayInspectorCard(
                stat = selectedStat,
                selectedDate = selectedDate,
                today = today,
                presentStudents = selectedDayPresentStudents,
                absentStudents = selectedDayAbsentStudents,
                currentLanguage = currentLanguage,
                onLoadAndEditDate = { onLoadAndEditDate(selectedDate) }
            )
        }
    }
}

/**
 * Tab 0: Modern Trend Bar Chart with percentage grid guides, dual-fill columns,
 * floating badges, and clean day-of-week labels.
 */
@Composable
private fun AttendanceTrendBarChart(
    statsList: List<DayAttendanceStat>,
    selectedDate: LocalDate,
    currentLanguage: AppLanguage,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Grid Reference Indicators (100%, 50%, 0%)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            // Background grid guide lines
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(100, 50, 0).forEach { pct ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${BengaliFormatter.formatDigits(pct, currentLanguage)}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.width(26.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (pct == 0) 0.5f else 0.25f),
                            thickness = 1.dp
                        )
                    }
                }
            }

            // Stacked Columns
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 28.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                statsList.forEach { stat ->
                    val isSelected = stat.date == selectedDate
                    val isToday = stat.date == LocalDate.now()
                    val ratio = (stat.rate.toFloat() / 100f).coerceIn(0f, 1f)

                    val animatedFill by animateFloatAsState(
                        targetValue = if (stat.isMarked) ratio else 0.05f,
                        animationSpec = tween(durationMillis = 350),
                        label = "att_bar_anim"
                    )

                    val barColor by animateColorAsState(
                        targetValue = when {
                            !stat.isMarked -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                            stat.rate >= 75 -> StatusPaid
                            stat.rate >= 50 -> Color(0xFFF59E0B)
                            else -> StatusDue
                        },
                        label = "att_bar_col"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onDateSelected(stat.date) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Floating percentage badge above selected bar
                        if (isSelected && stat.isMarked) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 2.dp)
                            ) {
                                Text(
                                    text = "${stat.rate}%",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                )
                            }
                        }

                        // Bar Capsule
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 18.dp else 13.dp)
                                .fillMaxHeight(fraction = 0.82f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .then(
                                    if (isSelected) Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                                    else if (isToday) Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                                    else Modifier
                                ),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            if (stat.isMarked) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(fraction = animatedFill.coerceAtLeast(0.06f))
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    barColor,
                                                    barColor.copy(alpha = 0.85f)
                                                )
                                            )
                                        )
                                )
                            } else {
                                // Faint dash indicating no class
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.dp))
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }

            // X-Axis Labels (Day of week + Date Number)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                statsList.forEach { stat ->
                    val isSelected = stat.date == selectedDate
                    val isToday = stat.date == LocalDate.now()
                    val dayName = BengaliFormatter.formatShortDayOfWeek(stat.date, currentLanguage)

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onDateSelected(stat.date) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dayName,
                            fontSize = 8.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        Text(
                            text = BengaliFormatter.formatDigits(stat.date.dayOfMonth, currentLanguage),
                            fontSize = 9.5.sp,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else if (isToday) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chart Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendIndicator(color = StatusPaid, label = Strings.presentLabel(currentLanguage))
            Spacer(modifier = Modifier.width(16.dp))
            LegendIndicator(color = StatusDue, label = Strings.absentLabel(currentLanguage))
            Spacer(modifier = Modifier.width(16.dp))
            LegendIndicator(color = MaterialTheme.colorScheme.outlineVariant, label = if (currentLanguage == AppLanguage.BN) "ক্লাস নেই" else "No Class")
        }
    }
}

@Composable
private fun LegendIndicator(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Tab 1: Calendar Heatmap Grid View
 * Displays day cells colored by attendance percentage, showing attendance consistency.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AttendanceCalendarHeatmap(
    statsList: List<DayAttendanceStat>,
    selectedDate: LocalDate,
    currentLanguage: AppLanguage,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Text(
            text = if (currentLanguage == AppLanguage.BN) "দৈনিক উপস্থিতির হিটম্যাপ ম্যাট্রিক্স" else "Daily Attendance Heatmap Matrix",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (currentLanguage == AppLanguage.BN) "গাঢ় সবুজ নির্দেশ করে সর্বোচ্চ উপস্থিতি" else "Darker green indicates higher attendance",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Grid of cells for each day in statsList
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            statsList.forEach { stat ->
                val isSelected = stat.date == selectedDate
                val isToday = stat.date == LocalDate.now()

                val cellBgColor = when {
                    !stat.isMarked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    stat.rate >= 90 -> Color(0xFF059669) // Deep Emerald
                    stat.rate >= 75 -> Color(0xFF10B981) // Bright Green
                    stat.rate >= 50 -> Color(0xFFF59E0B) // Amber
                    stat.rate > 0 -> Color(0xFFEF4444) // Coral Red
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                }

                val textColor = if (stat.isMarked && stat.rate >= 50) Color.White
                else if (stat.isMarked) Color.White
                else MaterialTheme.colorScheme.onSurfaceVariant

                Surface(
                    onClick = { onDateSelected(stat.date) },
                    shape = RoundedCornerShape(8.dp),
                    color = cellBgColor,
                    modifier = Modifier
                        .size(42.dp)
                        .then(
                            if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                            else if (isToday) Modifier.border(1.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            else Modifier
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = BengaliFormatter.formatShortDayOfWeek(stat.date, currentLanguage),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Normal,
                            color = textColor.copy(alpha = 0.85f)
                        )
                        Text(
                            text = BengaliFormatter.formatDigits(stat.date.dayOfMonth, currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Heatmap scale legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (currentLanguage == AppLanguage.BN) "কম" else "Low",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                Color(0xFFEF4444),
                Color(0xFFF59E0B),
                Color(0xFF10B981),
                Color(0xFF059669)
            ).forEach { col ->
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(col)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = if (currentLanguage == AppLanguage.BN) "বেশি (১০০%)" else "High (100%)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Tab 2: Student Attendance Ranking & Consistency Leaderboard
 */
@Composable
private fun AttendanceStudentRankingList(
    studentRanks: List<StudentAttendanceRank>,
    currentLanguage: AppLanguage,
    onStudentClick: (Long) -> Unit
) {
    if (studentRanks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = Strings.noStudentsFound(currentLanguage),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            studentRanks.forEachIndexed { index, rank ->
                Surface(
                    onClick = { onStudentClick(rank.student.id) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Rank Badge
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (index) {
                                            0 -> Color(0xFFFFD700).copy(alpha = 0.25f) // Gold
                                            1 -> Color(0xFFC0C0C0).copy(alpha = 0.35f) // Silver
                                            2 -> Color(0xFFCD7F32).copy(alpha = 0.25f) // Bronze
                                            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (index < 3) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = when (index) {
                                            0 -> Color(0xFFB78103)
                                            1 -> Color(0xFF6B7280)
                                            else -> Color(0xFF9A3412)
                                        },
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Text(
                                        text = BengaliFormatter.formatDigits(index + 1, currentLanguage),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            StudentAvatar(name = rank.student.name, size = 34)

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = rank.student.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${Strings.totalPresentDays(currentLanguage, BengaliFormatter.formatDigits(rank.presentDays, currentLanguage))} / ${BengaliFormatter.formatDigits(rank.totalDays, currentLanguage)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Percentage & Status
                        Column(horizontalAlignment = Alignment.End) {
                            val rateColor = when {
                                rank.rate >= 80 -> StatusPaid
                                rank.rate >= 60 -> Color(0xFF0284C7)
                                rank.rate >= 40 -> Color(0xFFE65100)
                                else -> StatusDue
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = rateColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${BengaliFormatter.formatDigits(rank.rate, currentLanguage)}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = rateColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section 5: Selected Day Deep-Dive Inspector Card
 * Gives rich breakdown of present vs absent students and quick action to edit that day's attendance.
 */
@Composable
private fun SelectedDayInspectorCard(
    stat: DayAttendanceStat,
    selectedDate: LocalDate,
    today: LocalDate,
    presentStudents: List<StudentEntity>,
    absentStudents: List<StudentEntity>,
    currentLanguage: AppLanguage,
    onLoadAndEditDate: () -> Unit
) {
    val fullDayName = BengaliFormatter.formatFullDayOfWeek(selectedDate, currentLanguage)
    val fullDateFormatted = BengaliFormatter.formatDateWithMonthName(selectedDate, currentLanguage)
    val isToday = selectedDate == today

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Full Date & Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$fullDayName, $fullDateFormatted",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isToday) (if (currentLanguage == AppLanguage.BN) "আজকের দিন" else "Today")
                        else if (stat.isMarked) (if (currentLanguage == AppLanguage.BN) "হাজিরা রেকর্ড সংরক্ষিত" else "Attendance recorded")
                        else (if (currentLanguage == AppLanguage.BN) "এখনও হাজিরা দেওয়া হয়নি" else "Not marked yet"),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (stat.isMarked) StatusPaid else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Edit / Load Action Button
                Button(
                    onClick = onLoadAndEditDate,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("load_and_edit_date_btn")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.BN) "হাজিরা এডিট" else "Edit Attendance",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Two-tone Attendance Ratio Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                if (stat.totalStudents > 0 && stat.isMarked) {
                    val pRatio = (stat.presentCount.toFloat() / stat.totalStudents.toFloat()).coerceIn(0f, 1f)
                    val aRatio = (stat.absentCount.toFloat() / stat.totalStudents.toFloat()).coerceIn(0f, 1f)
                    if (pRatio > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(pRatio)
                                .fillMaxHeight()
                                .background(StatusPaid)
                        )
                    }
                    if (aRatio > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(aRatio)
                                .fillMaxHeight()
                                .background(StatusDue)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats chips: Present & Absent
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Present Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusPaid.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = StatusPaid, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        val countSuffix = if (currentLanguage == AppLanguage.BN) " জন" else ""
                        Text(
                            text = "${Strings.presentLabel(currentLanguage)}: ${BengaliFormatter.formatDigits(stat.presentCount, currentLanguage)}$countSuffix",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = StatusPaid
                        )
                    }
                }

                // Absent Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusDue.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = StatusDue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        val countSuffix = if (currentLanguage == AppLanguage.BN) " জন" else ""
                        Text(
                            text = "${Strings.absentLabel(currentLanguage)}: ${BengaliFormatter.formatDigits(stat.absentCount, currentLanguage)}$countSuffix",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = StatusDue
                        )
                    }
                }
            }

            // Quick Student Names Pills
            if (presentStudents.isNotEmpty() || absentStudents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                if (presentStudents.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${Strings.presentLabel(currentLanguage)}: ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StatusPaid
                        )
                        Text(
                            text = presentStudents.joinToString(", ") { it.name },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (absentStudents.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${Strings.absentLabel(currentLanguage)}: ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StatusDue
                        )
                        Text(
                            text = absentStudents.joinToString(", ") { it.name },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
