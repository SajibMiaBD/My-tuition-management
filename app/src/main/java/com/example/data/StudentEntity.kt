package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val startDate: String, // ISO YYYY-MM-DD (e.g. 2026-01-15)
    val monthlyFee: Double,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isPaused: Boolean = false,
    val pausedDate: String? = null,
    val pauseHistoryJson: String = "[]",
    val activeCycleStartDate: String? = null,
    val academicYear: Int = 2026,
    val customPeriodStartsJson: String = "{}"
)
