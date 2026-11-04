package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PaymentEntity
import com.example.ui.PaymentWithStudent
import com.example.ui.components.AppOutlinedTextField
import com.example.ui.components.EmptyState
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.StatusPaid
import com.example.util.AppLanguage
import com.example.util.BengaliFormatter
import com.example.util.Strings
import java.time.LocalDate

enum class PaymentViewMode {
    THIS_YEAR,
    THIS_MONTH
}

@Composable
fun PaymentsScreen(
    payments: List<PaymentWithStudent>,
    selectedYear: Int = LocalDate.now().year,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onAddPaymentClick: () -> Unit,
    onDeletePaymentClick: (PaymentEntity) -> Unit,
    onStudentClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var activeViewMode by remember { mutableStateOf(PaymentViewMode.THIS_YEAR) }
    var paymentToDelete by remember { mutableStateOf<PaymentEntity?>(null) }

    val today = remember { LocalDate.now() }
    val thisMonthPrefix = remember(today) {
        "${today.year}-${String.format(java.util.Locale.US, "%02d", today.monthValue)}"
    }
    val thisYearPrefix = remember(selectedYear) {
        "$selectedYear-"
    }

    // Partition and sort payments chronologically (latest first with sequential order #1, #2...)
    val thisMonthPayments = remember(payments, thisMonthPrefix) {
        payments
            .filter { it.payment.paymentDate.startsWith(thisMonthPrefix) }
            .sortedWith(compareByDescending<PaymentWithStudent> { it.payment.paymentDate }.thenByDescending { it.payment.id })
    }

    val thisYearPayments = remember(payments, thisYearPrefix) {
        payments
            .filter { it.payment.paymentDate.startsWith(thisYearPrefix) }
            .sortedWith(compareByDescending<PaymentWithStudent> { it.payment.paymentDate }.thenByDescending { it.payment.id })
    }

    val thisMonthCollected = remember(thisMonthPayments) { thisMonthPayments.sumOf { it.payment.amount } }
    val thisYearCollected = remember(thisYearPayments) { thisYearPayments.sumOf { it.payment.amount } }

    val currentList = when (activeViewMode) {
        PaymentViewMode.THIS_MONTH -> thisMonthPayments
        PaymentViewMode.THIS_YEAR -> thisYearPayments
    }

    val filteredList = remember(currentList, searchQuery) {
        if (searchQuery.isBlank()) {
            currentList
        } else {
            currentList.filter { item ->
                item.studentName.contains(searchQuery, ignoreCase = true) ||
                        item.payment.notes.contains(searchQuery, ignoreCase = true) ||
                        item.payment.paymentDate.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPaymentClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("payments_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = Strings.recordPayment(currentLanguage))
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("payments_screen")
        ) {
            // Interactive Summary Cards (2026 Collection & This Month's Collection)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val yrTitle = if (currentLanguage == AppLanguage.BN) "$selectedYear সালের আদায়" else "$selectedYear Collection"
                InteractiveStatCard(
                    title = yrTitle,
                    value = BengaliFormatter.formatCurrency(thisYearCollected, currentLanguage),
                    countText = "${BengaliFormatter.formatDigits(thisYearPayments.size, currentLanguage)} ${if (currentLanguage == AppLanguage.BN) "টি পেমেন্ট" else "payments"}",
                    icon = Icons.Default.CalendarToday,
                    iconTint = StatusPaid,
                    isSelected = activeViewMode == PaymentViewMode.THIS_YEAR,
                    onClick = { activeViewMode = PaymentViewMode.THIS_YEAR },
                    modifier = Modifier.weight(1f)
                )

                InteractiveStatCard(
                    title = if (currentLanguage == AppLanguage.BN) "চলতি মাসের আদায়" else "This Month's Collection",
                    value = BengaliFormatter.formatCurrency(thisMonthCollected, currentLanguage),
                    countText = "${BengaliFormatter.formatDigits(thisMonthPayments.size, currentLanguage)} ${if (currentLanguage == AppLanguage.BN) "টি পেমেন্ট" else "payments"}",
                    icon = Icons.Default.CalendarMonth,
                    iconTint = MaterialTheme.colorScheme.primary,
                    isSelected = activeViewMode == PaymentViewMode.THIS_MONTH,
                    onClick = { activeViewMode = PaymentViewMode.THIS_MONTH },
                    modifier = Modifier.weight(1f)
                )
            }

            // Mode Selector Chips (Strictly 2026 Collection and This months collection)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeViewMode == PaymentViewMode.THIS_YEAR,
                    onClick = { activeViewMode = PaymentViewMode.THIS_YEAR },
                    leadingIcon = {
                        Icon(
                            imageVector = if (activeViewMode == PaymentViewMode.THIS_YEAR) Icons.Default.CheckCircle else Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (currentLanguage == AppLanguage.BN) "$selectedYear সালের আদায় (${BengaliFormatter.formatDigits(thisYearPayments.size, currentLanguage)})" else "$selectedYear Collection (${thisYearPayments.size})",
                            fontWeight = if (activeViewMode == PaymentViewMode.THIS_YEAR) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                FilterChip(
                    selected = activeViewMode == PaymentViewMode.THIS_MONTH,
                    onClick = { activeViewMode = PaymentViewMode.THIS_MONTH },
                    leadingIcon = {
                        Icon(
                            imageVector = if (activeViewMode == PaymentViewMode.THIS_MONTH) Icons.Default.CheckCircle else Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (currentLanguage == AppLanguage.BN) "চলতি মাসের আদায় (${BengaliFormatter.formatDigits(thisMonthPayments.size, currentLanguage)})" else "This Month's Collection (${thisMonthPayments.size})",
                            fontWeight = if (activeViewMode == PaymentViewMode.THIS_MONTH) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                AppOutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(Strings.searchPaymentPlaceholder(currentLanguage)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section Subtitle Notice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FormatListNumbered,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                val subtitle = if (activeViewMode == PaymentViewMode.THIS_MONTH) {
                    Strings.thisMonthListSubtitle(currentLanguage)
                } else {
                    Strings.allYearsListSubtitle(currentLanguage)
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Payments List (Chronological with sequence #1, #2... and dates)
            if (filteredList.isEmpty()) {
                val emptyTitle = if (searchQuery.isNotEmpty()) {
                    Strings.noPaymentMatched(currentLanguage)
                } else if (activeViewMode == PaymentViewMode.THIS_MONTH) {
                    Strings.noPaymentThisMonth(currentLanguage)
                } else {
                    Strings.noPaymentAllYears(currentLanguage)
                }

                EmptyState(
                    icon = Icons.Default.Payments,
                    title = emptyTitle,
                    subtitle = if (searchQuery.isNotEmpty()) Strings.tryOtherName(currentLanguage) else Strings.noPaymentHistorySubtitle(currentLanguage)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(filteredList, key = { _, item -> item.payment.id }) { index, item ->
                        SequentialPaymentCard(
                            sequenceNumber = index + 1,
                            item = item,
                            currentLanguage = currentLanguage,
                            onStudentClick = { onStudentClick(item.payment.studentId) },
                            onDeleteClick = { paymentToDelete = item.payment }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (paymentToDelete != null) {
        val p = paymentToDelete!!
        AlertDialog(
            onDismissRequest = { paymentToDelete = null },
            title = { Text(Strings.deletePaymentConfirmTitle(currentLanguage), fontWeight = FontWeight.Bold) },
            text = { Text(Strings.deletePaymentConfirmText(currentLanguage, BengaliFormatter.formatCurrency(p.amount, currentLanguage))) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeletePaymentClick(p)
                        paymentToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(Strings.delete(currentLanguage), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { paymentToDelete = null }) {
                    Text(Strings.cancel(currentLanguage))
                }
            }
        )
    }
}

/**
 * Interactive Stat Card that visually highlights when active
 */
@Composable
fun InteractiveStatCard(
    title: String,
    value: String,
    countText: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isSelected) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "✓",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = countText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Sequential payment card showing serial order #1, #2..., student info, and payment date
 */
@Composable
fun SequentialPaymentCard(
    sequenceNumber: Int,
    item: PaymentWithStudent,
    currentLanguage: AppLanguage = AppLanguage.BN,
    onStudentClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val pDate = try {
        LocalDate.parse(item.payment.paymentDate)
    } catch (_: Exception) {
        null
    }
    val dateStr = if (pDate != null) BengaliFormatter.formatDateDdMmYyyy(pDate, currentLanguage) else item.payment.paymentDate

    val pStart = try { LocalDate.parse(item.payment.periodStartDate) } catch (_: Exception) { null }
    val pEnd = try { LocalDate.parse(item.payment.periodEndDate) } catch (_: Exception) { null }
    val periodStr = if (pStart != null && pEnd != null) {
        "${Strings.periodTitle(currentLanguage, BengaliFormatter.formatDigits(item.payment.periodIndex + 1, currentLanguage))}: ${BengaliFormatter.formatPeriodRange(pStart, pEnd, currentLanguage)}"
    } else {
        Strings.periodTitle(currentLanguage, BengaliFormatter.formatDigits(item.payment.periodIndex + 1, currentLanguage))
    }

    val seqStr = BengaliFormatter.formatDigits(sequenceNumber, currentLanguage)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Sequence Badge (কার পর কে পেমেন্ট করেছে তা নির্দেশক)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Text(
                            text = Strings.paymentSeqLabel(currentLanguage, seqStr),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    StudentAvatar(name = item.studentName, size = 38)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.studentName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onStudentClick() }
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+${BengaliFormatter.formatCurrency(item.payment.amount, currentLanguage)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusPaid
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = Strings.delete(currentLanguage),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Period and Notes tag
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = periodStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (item.payment.notes.isNotBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = item.payment.notes,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
