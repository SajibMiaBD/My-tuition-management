package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
    indices = [Index(value = ["studentId", "date"], unique = true)]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val date: String, // ISO YYYY-MM-DD
    val isPresent: Boolean, // true = Present, false = Absent
    val academicYear: Int = 2026,
    val notes: String = ""
)
