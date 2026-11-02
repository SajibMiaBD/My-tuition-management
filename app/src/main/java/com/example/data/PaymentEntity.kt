package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("studentId")]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val periodIndex: Int,
    val periodStartDate: String, // ISO YYYY-MM-DD
    val periodEndDate: String,   // ISO YYYY-MM-DD
    val amount: Double,
    val paymentDate: String,     // ISO YYYY-MM-DD
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
