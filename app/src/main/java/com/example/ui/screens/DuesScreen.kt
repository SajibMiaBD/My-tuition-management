package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.StudentBillingSummary
import com.example.ui.components.EmptyState
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.StatusDue
import com.example.util.AppLanguage
import com.example.util.BengaliFormatter
import com.example.util.Strings

@Composable
fun DuesScreen(
    students: List<StudentBillingSummary>,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onStudentClick: (Long) -> Unit,
    onRecordPaymentClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dueStudents = students.filter { it.totalDue > 0.0 }.sortedByDescending { it.totalDue }
    val totalDueAmount = dueStudents.sumOf { it.totalDue }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("dues_screen")
    ) {
        // High Impact Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = Strings.totalOutstandingDueBanner(currentLanguage),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = BengaliFormatter.formatCurrency(totalDueAmount, currentLanguage),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Strings.studentsWithDueCountSubtitle(currentLanguage, BengaliFormatter.formatDigits(dueStudents.size, currentLanguage)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(StatusDue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = StatusDue,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // List Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Strings.duesStudentListTitle(currentLanguage),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (dueStudents.isEmpty()) {
            EmptyState(
                icon = Icons.Default.CheckCircle,
                title = Strings.congratulationsNoDues(currentLanguage),
                subtitle = Strings.congratulationsSubtitle(currentLanguage)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dueStudents, key = { it.student.id }) { summary ->
                    DueStudentCard(
                        summary = summary,
                        currentLanguage = currentLanguage,
                        onClick = { onStudentClick(summary.student.id) },
                        onRecordPaymentClick = { onRecordPaymentClick(summary.student.id) },
                        onSendSmsClick = { phone, name, due ->
                            val msg = Strings.smsBodyTemplate(currentLanguage, name, due)
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone")).apply {
                                putExtra("sms_body", msg)
                            }
                            context.startActivity(intent)
                        },
                        onCallClick = { phone ->
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DueStudentCard(
    summary: StudentBillingSummary,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onClick: () -> Unit,
    onRecordPaymentClick: () -> Unit,
    onSendSmsClick: (phone: String, name: String, due: String) -> Unit,
    onCallClick: (phone: String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Student Info Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    StudentAvatar(name = summary.student.name, size = 44)
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
                            color = StatusDue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = BengaliFormatter.formatCurrency(summary.totalDue, currentLanguage),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = StatusDue
                    )
                    Text(
                        text = Strings.dueLabel(currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Overdue periods overview
            val unpaidCompletedPeriods = summary.periods.filter { it.hasEnded && it.overdueAmount > 0 }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                unpaidCompletedPeriods.forEach { p ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${Strings.periodTitle(currentLanguage, BengaliFormatter.formatDigits(p.periodNumber, currentLanguage))} (${BengaliFormatter.formatPeriodRange(p.startDate, p.endDate, currentLanguage)})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = BengaliFormatter.formatCurrency(p.overdueAmount, currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = StatusDue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRecordPaymentClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(Strings.recordPayment(currentLanguage), style = MaterialTheme.typography.labelMedium)
                }

                if (summary.student.phone.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            onSendSmsClick(
                                summary.student.phone,
                                summary.student.name,
                                summary.totalDue.toInt().toString()
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(Strings.sendReminderSms(currentLanguage), style = MaterialTheme.typography.labelMedium)
                    }

                    OutlinedButton(
                        onClick = { onCallClick(summary.student.phone) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = Strings.call(currentLanguage), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
