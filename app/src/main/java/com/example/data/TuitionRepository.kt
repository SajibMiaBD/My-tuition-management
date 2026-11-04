package com.example.data

import kotlinx.coroutines.flow.Flow

class TuitionRepository(
    private val studentDao: StudentDao,
    private val paymentDao: PaymentDao,
    private val attendanceDao: AttendanceDao
) {
    val allStudents: Flow<List<StudentEntity>> = studentDao.getAllStudents()
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()
    val allAttendance: Flow<List<AttendanceEntity>> = attendanceDao.getAllAttendance()

    fun getAttendanceForYear(year: Int): Flow<List<AttendanceEntity>> = attendanceDao.getAttendanceForYear(year)
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceEntity>> = attendanceDao.getAttendanceForStudent(studentId)
    fun getAttendanceForDate(date: String): Flow<List<AttendanceEntity>> = attendanceDao.getAttendanceForDate(date)

    suspend fun recordAttendance(studentId: Long, date: String, isPresent: Boolean, academicYear: Int, notes: String = "") {
        attendanceDao.insertOrUpdate(
            AttendanceEntity(
                studentId = studentId,
                date = date,
                isPresent = isPresent,
                academicYear = academicYear,
                notes = notes
            )
        )
    }

    suspend fun recordBulkAttendance(records: List<AttendanceEntity>) {
        attendanceDao.insertOrUpdateAll(records)
    }

    suspend fun deleteAttendance(studentId: Long, date: String) {
        attendanceDao.deleteAttendance(studentId, date)
    }

    fun getStudentsByYear(year: Int): Flow<List<StudentEntity>> = studentDao.getStudentsByYear(year)
    suspend fun getStudentsByYearList(year: Int): List<StudentEntity> = studentDao.getStudentsByYearList(year)

    suspend fun getAllStudentsList(): List<StudentEntity> = studentDao.getAllStudentsList()
    suspend fun getAllPaymentsList(): List<PaymentEntity> = paymentDao.getAllPaymentsList()

    fun getStudentById(id: Long): Flow<StudentEntity?> = studentDao.getStudentById(id)
    fun getPaymentsForStudent(studentId: Long): Flow<List<PaymentEntity>> = paymentDao.getPaymentsForStudent(studentId)

    suspend fun insertStudent(student: StudentEntity): Long = studentDao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = studentDao.updateStudent(student)
    suspend fun deleteStudent(student: StudentEntity) = studentDao.deleteStudent(student)
    suspend fun deleteStudentById(id: Long) = studentDao.deleteStudentById(id)

    suspend fun copyStudentsToYear(sourceYear: Int, targetYear: Int): Int {
        val sourceStudents = studentDao.getStudentsByYearList(sourceYear)
        val existingInTarget = studentDao.getStudentsByYearList(targetYear)
        val existingNames = existingInTarget.map { it.name.trim().lowercase() }.toSet()

        var count = 0
        for (s in sourceStudents) {
            if (s.name.trim().lowercase() !in existingNames) {
                val originalStart = try {
                    java.time.LocalDate.parse(s.startDate)
                } catch (_: Exception) {
                    java.time.LocalDate.of(sourceYear, 1, 1)
                }
                val newStartDate = try {
                    java.time.LocalDate.of(targetYear, originalStart.monthValue, originalStart.dayOfMonth.coerceAtMost(28)).toString()
                } catch (_: Exception) {
                    "$targetYear-01-01"
                }
                studentDao.insertStudent(
                    StudentEntity(
                        name = s.name,
                        phone = s.phone,
                        startDate = newStartDate,
                        monthlyFee = s.monthlyFee,
                        notes = s.notes,
                        createdAt = System.currentTimeMillis(),
                        isPaused = false,
                        pausedDate = null,
                        pauseHistoryJson = "[]",
                        activeCycleStartDate = null,
                        academicYear = targetYear
                    )
                )
                count++
            }
        }
        return count
    }

    suspend fun insertPayment(payment: PaymentEntity): Long = paymentDao.insertPayment(payment)
    suspend fun updatePayment(payment: PaymentEntity) = paymentDao.updatePayment(payment)
    suspend fun deletePayment(payment: PaymentEntity) = paymentDao.deletePayment(payment)
    suspend fun deletePaymentById(id: Long) = paymentDao.deletePaymentById(id)

    suspend fun clearAllData() {
        attendanceDao.deleteAll()
        paymentDao.deleteAllPayments()
        studentDao.deleteAllStudents()
    }

    suspend fun restoreBackup(
        backupStudents: List<StudentEntity>,
        backupPayments: List<PaymentEntity>,
        replaceExisting: Boolean
    ): Pair<Int, Int> {
        if (replaceExisting) {
            paymentDao.deleteAllPayments()
            studentDao.deleteAllStudents()
            studentDao.insertStudents(backupStudents)
            paymentDao.insertPayments(backupPayments)
            return Pair(backupStudents.size, backupPayments.size)
        } else {
            // Non-destructive merge
            val existingStudents = studentDao.getAllStudentsList()
            val existingPayments = paymentDao.getAllPaymentsList()

            val studentIdMap = mutableMapOf<Long, Long>()
            var studentsAdded = 0
            var paymentsAdded = 0

            for (backupStudent in backupStudents) {
                val existing = existingStudents.find {
                    it.name.trim().equals(backupStudent.name.trim(), ignoreCase = true) &&
                    it.startDate == backupStudent.startDate
                }
                if (existing != null) {
                    studentIdMap[backupStudent.id] = existing.id
                } else {
                    val newId = studentDao.insertStudent(backupStudent.copy(id = 0))
                    studentIdMap[backupStudent.id] = newId
                    studentsAdded++
                }
            }

            for (backupPayment in backupPayments) {
                val targetStudentId = studentIdMap[backupPayment.studentId] ?: continue
                val duplicate = existingPayments.any {
                    it.studentId == targetStudentId &&
                    it.periodIndex == backupPayment.periodIndex &&
                    it.periodStartDate == backupPayment.periodStartDate &&
                    it.amount == backupPayment.amount &&
                    it.paymentDate == backupPayment.paymentDate
                }
                if (!duplicate) {
                    paymentDao.insertPayment(
                        backupPayment.copy(
                            id = 0,
                            studentId = targetStudentId
                        )
                    )
                    paymentsAdded++
                }
            }

            return Pair(studentsAdded, paymentsAdded)
        }
    }
}
