package com.example.data

import kotlinx.coroutines.flow.Flow

class TuitionRepository(
    private val studentDao: StudentDao,
    private val paymentDao: PaymentDao
) {
    val allStudents: Flow<List<StudentEntity>> = studentDao.getAllStudents()
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()

    suspend fun getAllStudentsList(): List<StudentEntity> = studentDao.getAllStudentsList()
    suspend fun getAllPaymentsList(): List<PaymentEntity> = paymentDao.getAllPaymentsList()

    fun getStudentById(id: Long): Flow<StudentEntity?> = studentDao.getStudentById(id)
    fun getPaymentsForStudent(studentId: Long): Flow<List<PaymentEntity>> = paymentDao.getPaymentsForStudent(studentId)

    suspend fun insertStudent(student: StudentEntity): Long = studentDao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = studentDao.updateStudent(student)
    suspend fun deleteStudent(student: StudentEntity) = studentDao.deleteStudent(student)
    suspend fun deleteStudentById(id: Long) = studentDao.deleteStudentById(id)

    suspend fun insertPayment(payment: PaymentEntity): Long = paymentDao.insertPayment(payment)
    suspend fun updatePayment(payment: PaymentEntity) = paymentDao.updatePayment(payment)
    suspend fun deletePayment(payment: PaymentEntity) = paymentDao.deletePayment(payment)
    suspend fun deletePaymentById(id: Long) = paymentDao.deletePaymentById(id)

    suspend fun clearAllData() {
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
