package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.PaymentEntity
import com.example.data.StudentEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BackupData(
    val exportDate: String,
    val students: List<StudentEntity>,
    val payments: List<PaymentEntity>
)

object BackupHelper {

    fun createBackupJson(students: List<StudentEntity>, payments: List<PaymentEntity>): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("appName", "My Tuition Manager")
        val now = LocalDateTime.now()
        val formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        root.put("exportDate", formattedDate)

        val studentsArray = JSONArray()
        for (s in students) {
            val sObj = JSONObject()
            sObj.put("id", s.id)
            sObj.put("name", s.name)
            sObj.put("phone", s.phone)
            sObj.put("startDate", s.startDate)
            sObj.put("monthlyFee", s.monthlyFee)
            sObj.put("notes", s.notes)
            sObj.put("createdAt", s.createdAt)
            sObj.put("isPaused", s.isPaused)
            sObj.put("pausedDate", s.pausedDate ?: JSONObject.NULL)
            sObj.put("pauseHistoryJson", s.pauseHistoryJson)
            sObj.put("activeCycleStartDate", s.activeCycleStartDate ?: JSONObject.NULL)
            studentsArray.put(sObj)
        }
        root.put("students", studentsArray)

        val paymentsArray = JSONArray()
        for (p in payments) {
            val pObj = JSONObject()
            pObj.put("id", p.id)
            pObj.put("studentId", p.studentId)
            pObj.put("periodIndex", p.periodIndex)
            pObj.put("periodStartDate", p.periodStartDate)
            pObj.put("periodEndDate", p.periodEndDate)
            pObj.put("amount", p.amount)
            pObj.put("paymentDate", p.paymentDate)
            pObj.put("notes", p.notes)
            pObj.put("createdAt", p.createdAt)
            paymentsArray.put(pObj)
        }
        root.put("payments", paymentsArray)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): BackupData {
        val root = JSONObject(jsonString)
        val exportDate = root.optString("exportDate", "")

        val studentsList = mutableListOf<StudentEntity>()
        val studentsArray = root.optJSONArray("students")
        if (studentsArray != null) {
            for (i in 0 until studentsArray.length()) {
                val sObj = studentsArray.getJSONObject(i)
                val pDate = if (sObj.isNull("pausedDate")) null else sObj.optString("pausedDate", null)
                val cDate = if (sObj.isNull("activeCycleStartDate")) null else sObj.optString("activeCycleStartDate", null)
                studentsList.add(
                    StudentEntity(
                        id = sObj.optLong("id", 0L),
                        name = sObj.getString("name"),
                        phone = sObj.optString("phone", ""),
                        startDate = sObj.getString("startDate"),
                        monthlyFee = sObj.getDouble("monthlyFee"),
                        notes = sObj.optString("notes", ""),
                        createdAt = sObj.optLong("createdAt", System.currentTimeMillis()),
                        isPaused = sObj.optBoolean("isPaused", false),
                        pausedDate = pDate,
                        pauseHistoryJson = sObj.optString("pauseHistoryJson", "[]"),
                        activeCycleStartDate = cDate
                    )
                )
            }
        }

        val paymentsList = mutableListOf<PaymentEntity>()
        val paymentsArray = root.optJSONArray("payments")
        if (paymentsArray != null) {
            for (i in 0 until paymentsArray.length()) {
                val pObj = paymentsArray.getJSONObject(i)
                paymentsList.add(
                    PaymentEntity(
                        id = pObj.optLong("id", 0L),
                        studentId = pObj.getLong("studentId"),
                        periodIndex = pObj.getInt("periodIndex"),
                        periodStartDate = pObj.getString("periodStartDate"),
                        periodEndDate = pObj.getString("periodEndDate"),
                        amount = pObj.getDouble("amount"),
                        paymentDate = pObj.getString("paymentDate"),
                        notes = pObj.optString("notes", ""),
                        createdAt = pObj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        }

        return BackupData(
            exportDate = exportDate,
            students = studentsList,
            payments = paymentsList
        )
    }

    fun writeStringToUri(context: Context, uri: Uri, content: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(content)
                    writer.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun readStringFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareBackupText(context: Context, jsonContent: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "My Tuition Manager Backup")
            putExtra(Intent.EXTRA_TEXT, jsonContent)
        }
        val chooser = Intent.createChooser(intent, "Share Tuition Backup")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun shareToGoogleDrive(context: Context, jsonContent: String, accountEmail: String?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Tuition Manager Backup (${accountEmail ?: "Google Account"})")
            putExtra(Intent.EXTRA_TEXT, jsonContent)
            setPackage("com.google.android.apps.docs") // Google Drive package
        }
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // If Google Drive app is not installed or package cannot be found, use generic chooser
            shareBackupText(context, jsonContent)
        }
    }
}

