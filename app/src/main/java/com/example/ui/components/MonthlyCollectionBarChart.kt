package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.MonthlyCollection
import com.example.util.AppLanguage
import com.example.util.BengaliFormatter
import com.example.util.Strings
import java.time.LocalDate

@Composable
fun MonthlyCollectionBarChart(
    monthlyCollections: List<MonthlyCollection>,
    currentLanguage: AppLanguage = AppLanguage.BN,
    selectedYear: Int? = null,
    modifier: Modifier = Modifier
) {
    val totalAmount = remember(monthlyCollections) {
        monthlyCollections.sumOf { it.amount }
    }

    val maxAmount = remember(monthlyCollections) {
        val maxVal = monthlyCollections.maxOfOrNull { it.amount } ?: 0.0
        if (maxVal > 0.0) maxVal else 1000.0
    }

    // Default selected month is current month if within this year, or the latest month with collections
    val currentMonthVal = LocalDate.now().monthValue
    var selectedMonthIndex by remember(monthlyCollections, selectedYear) {
        val nonZeroIdx = monthlyCollections.indexOfLast { it.amount > 0 }
        val defaultIdx = if (nonZeroIdx >= 0) {
            nonZeroIdx
        } else if (selectedYear == null || selectedYear == LocalDate.now().year) {
            (currentMonthVal - 1).coerceIn(0, 11)
        } else {
            0
        }
        mutableStateOf(defaultIdx)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_monthly_bar_chart"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title and Total Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        val yrStr = if (selectedYear != null) BengaliFormatter.formatDigits(selectedYear, currentLanguage) else ""
                        val chartTitleText = if (selectedYear != null) {
                            if (currentLanguage == AppLanguage.BN) "$yrStr সালের টিউশন ফি আদায়" else "Fee Collections for $yrStr"
                        } else {
                            Strings.chartTitle(currentLanguage)
                        }
                        val chartSubtitleText = if (selectedYear != null) {
                            if (currentLanguage == AppLanguage.BN) "১২ মাসের আদায়ের চিত্র (Jan - Dec)" else "12-month breakdown (Jan - Dec)"
                        } else {
                            Strings.chartSubtitle(currentLanguage)
                        }
                        Text(
                            text = chartTitleText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = chartSubtitleText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = BengaliFormatter.formatCurrency(totalAmount, currentLanguage),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Month Detail Banner
            val selectedItem = monthlyCollections.getOrNull(selectedMonthIndex)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val monthName = if (selectedItem != null) {
                            if (currentLanguage == AppLanguage.BN) selectedItem.labelBn else selectedItem.labelEn
                        } else ""
                        val yrLabel = if (selectedYear != null) " $selectedYear" else ""
                        Text(
                            text = "$monthName$yrLabel",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    val selectedAmount = selectedItem?.amount ?: 0.0
                    Text(
                        text = BengaliFormatter.formatCurrency(selectedAmount, currentLanguage),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedAmount > 0.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 12-Month Native Jetpack Compose Bar Chart showing Jan, Feb, Mar, Apr...
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Bars Area
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        monthlyCollections.forEachIndexed { index, item ->
                            val isSelected = index == selectedMonthIndex
                            val barRatio = if (maxAmount > 0.0) {
                                (item.amount / maxAmount).toFloat().coerceIn(0f, 1f)
                            } else {
                                0f
                            }
                            val animatedHeightRatio by animateFloatAsState(
                                targetValue = barRatio,
                                animationSpec = tween(durationMillis = 400),
                                label = "bar_height"
                            )

                            val barColor by animateColorAsState(
                                targetValue = when {
                                    isSelected && item.amount > 0.0 -> MaterialTheme.colorScheme.primary
                                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    item.amount > 0.0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
                                },
                                label = "bar_color"
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        selectedMonthIndex = index
                                    },
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                // Background pill track
                                Box(
                                    modifier = Modifier
                                        .width(if (isSelected) 18.dp else 14.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                        )
                                )

                                // Foreground filled bar (with minimum height so 0 collection month has a sleek indicator pill)
                                val fillFraction = (animatedHeightRatio * 0.92f).coerceAtLeast(0.04f)
                                Box(
                                    modifier = Modifier
                                        .width(if (isSelected) 18.dp else 14.dp)
                                        .fillMaxHeight(fraction = fillFraction)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    barColor,
                                                    barColor.copy(alpha = 0.9f)
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 12 Month Short Names: Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        monthlyCollections.forEachIndexed { index, item ->
                            val isSelected = index == selectedMonthIndex
                            val labelText = if (currentLanguage == AppLanguage.BN) item.labelBn else item.labelEn

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        selectedMonthIndex = index
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = labelText,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else if (item.amount > 0.0) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            if (totalAmount <= 0.0) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = Strings.chartEmptyNotice(currentLanguage),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}
