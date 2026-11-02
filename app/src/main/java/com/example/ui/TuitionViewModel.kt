package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PaymentEntity
import com.example.data.SampleDataProvider
import com.example.data.StudentEntity
import com.example.data.TuitionRepository
import com.example.domain.BillingEngine
import com.example.domain.DashboardStats
import com.example.domain.PaymentStatus
import com.example.domain.StudentBillingSummary
import com.example.domain.StudentFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed class Screen(val title: String) {
    data object Dashboard : Screen("ড্যাশবোর্ড")
    data object Students : Screen("শিক্ষার্থী")
    data object Payments : Screen("পেমেন্ট")
    data object Dues : Screen("বকেয়া")
    data object Settings : Screen("সেটিংস")
    data class StudentDetails(val studentId: Long) : Screen("শিক্ষার্থীর বিবরণ")
}

data class PaymentWithStudent(
    val payment: PaymentEntity,
    val studentName: String,
    val studentPhone: String
)

class TuitionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TuitionRepository
    val today: LocalDate = LocalDate.now()
    private val prefs = application.getSharedPreferences("tuition_prefs", android.content.Context.MODE_PRIVATE)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TuitionRepository(database.studentDao(), database.paymentDao())

        // Fresh installs must always start with empty database (clean slate)
        // Never auto-populate sample students on install
        val hasCleanedDefaultSamples = prefs.getBoolean("has_cleared_initial_samples_v5", false)
        if (!hasCleanedDefaultSamples) {
            viewModelScope.launch {
                val existing = repository.getAllStudentsList()
                val sampleNames = setOf(
                    "রহিম আহমেদ", "সুমাইয়া আক্তার", "তানভীর হাসান", "নাফিসা কামাল", "ফারহান চৌধুরী",
                    "Rahim Ahmed", "Sumaiya Akter", "Tanvir Hasan", "Nafisa Kamal", "Farhan Chowdhury"
                )
                if (existing.isNotEmpty() && existing.all { it.name in sampleNames }) {
                    repository.clearAllData()
                }
                prefs.edit()
                    .putBoolean("has_initialized_sample_data", true)
                    .putBoolean("has_cleared_initial_samples_v5", true)
                    .apply()
            }
        }
    }

    // Theme Preference (persisted via SharedPreferences)
    private val _currentTheme = MutableStateFlow(
        com.example.ui.theme.AppTheme.fromName(prefs.getString("app_theme", com.example.ui.theme.AppTheme.ROYAL_EMERALD.name))
    )
    val currentTheme: StateFlow<com.example.ui.theme.AppTheme> = _currentTheme.asStateFlow()

    fun setTheme(theme: com.example.ui.theme.AppTheme) {
        _currentTheme.value = theme
        prefs.edit().putString("app_theme", theme.name).apply()
    }

    // Language Preference (persisted via SharedPreferences)
    // Requirement: Fresh install defaults to English (EN)
    private val _currentLanguage = MutableStateFlow(
        if (prefs.getString("app_language", "EN") == "BN") com.example.util.AppLanguage.BN else com.example.util.AppLanguage.EN
    )
    val currentLanguage: StateFlow<com.example.util.AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(lang: com.example.util.AppLanguage) {
        _currentLanguage.value = lang
        prefs.edit().putString("app_language", lang.name).apply()
    }

    // Selected Financial Year for Dashboard isolation
    private val _selectedYear = MutableStateFlow(today.year)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    fun setSelectedYear(year: Int) {
        _selectedYear.value = year
    }

    // Available years dynamically derived from data & current year
    val availableYears: StateFlow<List<Int>> = combine(repository.allStudents, repository.allPayments) { students, payments ->
        val years = mutableSetOf(today.year, today.year - 1, today.year - 2, today.year + 1)
        students.forEach { s ->
            try {
                years.add(LocalDate.parse(s.startDate).year)
            } catch (_: Exception) {}
        }
        payments.forEach { p ->
            try {
                years.add(LocalDate.parse(p.paymentDate).year)
            } catch (_: Exception) {}
        }
        years.sortedDescending()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(today.year + 1, today.year, today.year - 1, today.year - 2))

    // Navigation Stack
    private val _navStack = MutableStateFlow<List<Screen>>(listOf(Screen.Dashboard))
    val currentScreen: StateFlow<Screen> = _navStack.combine(_navStack) { stack, _ ->
        stack.lastOrNull() ?: Screen.Dashboard
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Screen.Dashboard)

    fun navigateTo(screen: Screen) {
        val current = _navStack.value
        // If navigating to main tab, reset stack to that tab
        if (screen is Screen.Dashboard || screen is Screen.Students || screen is Screen.Payments || screen is Screen.Dues || screen is Screen.Settings) {
            _navStack.value = listOf(screen)
        } else {
            _navStack.value = current + screen
        }
    }

    fun navigateBack(): Boolean {
        val current = _navStack.value
        return if (current.size > 1) {
            _navStack.value = current.dropLast(1)
            true
        } else {
            false
        }
    }

    // Raw Flows from DB
    val allStudentsFlow = repository.allStudents
    val allPaymentsFlow = repository.allPayments

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _studentFilter = MutableStateFlow(StudentFilter.ALL)
    val studentFilter: StateFlow<StudentFilter> = _studentFilter.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStudentFilter(filter: StudentFilter) {
        _studentFilter.value = filter
    }

    // Computed Student Summaries
    val studentSummaries: StateFlow<List<StudentBillingSummary>> = combine(
        allStudentsFlow,
        allPaymentsFlow
    ) { students, payments ->
        students.map { student ->
            val studentPayments = payments.filter { it.studentId == student.id }
            BillingEngine.computeStudentSummary(student, studentPayments, today)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Students List
    val filteredStudents: StateFlow<List<StudentBillingSummary>> = combine(
        studentSummaries,
        _searchQuery,
        _studentFilter
    ) { summaries, query, filter ->
        summaries.filter { summary ->
            val matchesQuery = query.isBlank() ||
                    summary.student.name.contains(query, ignoreCase = true) ||
                    summary.student.phone.contains(query)
            val matchesFilter = when (filter) {
                StudentFilter.ALL -> true
                StudentFilter.PAID -> summary.totalDue == 0.0 && (summary.currentPeriod == null || summary.currentPeriod.remainingAmount <= 0.0)
                StudentFilter.PENDING -> summary.totalDue == 0.0 && (summary.currentPeriod?.remainingAmount ?: 0.0) > 0.0
                StudentFilter.OVERDUE -> summary.totalDue > 0.0
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Statistics strictly isolated by selected year
    val dashboardStats: StateFlow<DashboardStats> = combine(
        studentSummaries,
        allPaymentsFlow,
        _selectedYear
    ) { summaries, payments, year ->
        BillingEngine.computeDashboardStats(summaries, payments, today, year)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    // Monthly collections for the selected year
    val monthlyCollections: StateFlow<List<com.example.domain.MonthlyCollection>> = combine(
        allPaymentsFlow,
        _selectedYear
    ) { payments, year ->
        BillingEngine.computeYearlyMonthlyCollections(payments, year)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BillingEngine.computeYearlyMonthlyCollections(emptyList(), today.year))

    // Payments with Student Info
    val paymentsWithStudents: StateFlow<List<PaymentWithStudent>> = combine(
        allStudentsFlow,
        allPaymentsFlow
    ) { students, payments ->
        val studentMap = students.associateBy { it.id }
        payments.map { payment ->
            val student = studentMap[payment.studentId]
            PaymentWithStudent(
                payment = payment,
                studentName = student?.name ?: "অজ্ঞাত শিক্ষার্থী",
                studentPhone = student?.phone ?: ""
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dialog & Action States
    val showAddEditStudentDialog = MutableStateFlow(false)
    val studentToEdit = MutableStateFlow<StudentEntity?>(null)

    val showRecordPaymentDialog = MutableStateFlow(false)
    val preselectedStudentId = MutableStateFlow<Long?>(null)
    val preselectedPeriodIndex = MutableStateFlow<Int?>(null)

    val studentToDelete = MutableStateFlow<StudentEntity?>(null)
    val paymentToDelete = MutableStateFlow<PaymentEntity?>(null)

    fun openAddStudentDialog() {
        studentToEdit.value = null
        showAddEditStudentDialog.value = true
    }

    fun openEditStudentDialog(student: StudentEntity) {
        studentToEdit.value = student
        showAddEditStudentDialog.value = true
    }

    fun closeAddEditStudentDialog() {
        showAddEditStudentDialog.value = false
        studentToEdit.value = null
    }

    fun openRecordPaymentDialog(studentId: Long? = null, periodIndex: Int? = null) {
        preselectedStudentId.value = studentId
        preselectedPeriodIndex.value = periodIndex
        showRecordPaymentDialog.value = true
    }

    fun closeRecordPaymentDialog() {
        showRecordPaymentDialog.value = false
        preselectedStudentId.value = null
        preselectedPeriodIndex.value = null
    }

    // CRUD Operations
    fun saveStudent(
        name: String,
        phone: String,
        startDate: String,
        monthlyFee: Double,
        notes: String,
        existingId: Long = 0
    ) {
        viewModelScope.launch {
            try {
                if (existingId > 0) {
                    val current = studentToEdit.value
                    repository.updateStudent(
                        StudentEntity(
                            id = existingId,
                            name = name.trim(),
                            phone = phone.trim(),
                            startDate = startDate,
                            monthlyFee = monthlyFee,
                            notes = notes.trim(),
                            isPaused = current?.isPaused ?: false,
                            pausedDate = current?.pausedDate,
                            pauseHistoryJson = current?.pauseHistoryJson ?: "[]",
                            activeCycleStartDate = current?.activeCycleStartDate
                        )
                    )
                } else {
                    repository.insertStudent(
                        StudentEntity(
                            name = name.trim(),
                            phone = phone.trim(),
                            startDate = startDate,
                            monthlyFee = monthlyFee,
                            notes = notes.trim()
                        )
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("TuitionViewModel", "Error saving student: ${e.message}", e)
            } finally {
                closeAddEditStudentDialog()
            }
        }
    }

    fun confirmDeleteStudent(student: StudentEntity) {
        studentToDelete.value = student
    }

    fun dismissDeleteStudent() {
        studentToDelete.value = null
    }

    fun executeDeleteStudent() {
        val student = studentToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteStudent(student)
            studentToDelete.value = null
            // If current screen was details of this student, go back
            val current = _navStack.value.lastOrNull()
            if (current is Screen.StudentDetails && current.studentId == student.id) {
                navigateBack()
            }
        }
    }

    val paymentSuccessReceipt = MutableStateFlow<com.example.domain.PaymentConfirmation?>(null)

    fun dismissPaymentSuccessReceipt() {
        paymentSuccessReceipt.value = null
    }

    fun recordPayment(
        studentId: Long,
        periodIndex: Int,
        periodStartDate: String,
        periodEndDate: String,
        amount: Double,
        paymentDate: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertPayment(
                PaymentEntity(
                    studentId = studentId,
                    periodIndex = periodIndex,
                    periodStartDate = periodStartDate,
                    periodEndDate = periodEndDate,
                    amount = amount,
                    paymentDate = paymentDate,
                    notes = notes.trim()
                )
            )
            closeRecordPaymentDialog()

            val studentName = studentSummaries.value.find { it.student.id == studentId }?.student?.name ?: "শিক্ষার্থী"
            val pStart = try { LocalDate.parse(periodStartDate) } catch (e: Exception) { null }
            val pEnd = try { LocalDate.parse(periodEndDate) } catch (e: Exception) { null }
            val periodRange = if (pStart != null && pEnd != null) {
                com.example.util.BengaliFormatter.formatPeriodRange(pStart, pEnd)
            } else {
                "পিরিয়ড ${periodIndex + 1}"
            }

            paymentSuccessReceipt.value = com.example.domain.PaymentConfirmation(
                studentName = studentName,
                amount = amount,
                paymentDate = paymentDate,
                periodRange = periodRange,
                notes = notes.trim()
            )
        }
    }

    fun confirmDeletePayment(payment: PaymentEntity) {
        paymentToDelete.value = payment
    }

    fun dismissDeletePayment() {
        paymentToDelete.value = null
    }

    fun executeDeletePayment() {
        val payment = paymentToDelete.value ?: return
        viewModelScope.launch {
            repository.deletePayment(payment)
            paymentToDelete.value = null
        }
    }

    fun reloadSampleData() {
        viewModelScope.launch {
            repository.clearAllData()
            SampleDataProvider.populateSampleData(repository)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    // Backup & Restore
    val pendingBackupData = MutableStateFlow<com.example.util.BackupData?>(null)
    val backupStatusMessage = MutableStateFlow<String?>(null)

    fun dismissBackupStatus() {
        backupStatusMessage.value = null
    }

    fun cancelImport() {
        pendingBackupData.value = null
    }

    fun prepareExportContent(onContentReady: (String?) -> Unit) {
        viewModelScope.launch {
            val students = repository.getAllStudentsList()
            val payments = repository.getAllPaymentsList()
            if (students.isEmpty() && payments.isEmpty()) {
                val emptyMsg = com.example.util.Strings.exportEmptyWarning(_currentLanguage.value)
                backupStatusMessage.value = emptyMsg
                onContentReady(null)
            } else {
                val json = com.example.util.BackupHelper.createBackupJson(students, payments)
                onContentReady(json)
            }
        }
    }

    fun writeBackupToUri(context: android.content.Context, uri: android.net.Uri) {
        viewModelScope.launch {
            val students = repository.getAllStudentsList()
            val payments = repository.getAllPaymentsList()
            val json = com.example.util.BackupHelper.createBackupJson(students, payments)
            val success = com.example.util.BackupHelper.writeStringToUri(context, uri, json)
            backupStatusMessage.value = if (success) {
                com.example.util.Strings.exportSuccessMessage(_currentLanguage.value)
            } else {
                if (_currentLanguage.value == com.example.util.AppLanguage.BN) "ব্যাকআপ ফাইল সেভ করতে ব্যর্থ হয়েছে!" else "Failed to save backup file!"
            }
        }
    }

    fun prepareImportFromUri(context: android.content.Context, uri: android.net.Uri) {
        viewModelScope.launch {
            val json = com.example.util.BackupHelper.readStringFromUri(context, uri)
            if (json.isNullOrBlank()) {
                backupStatusMessage.value = com.example.util.Strings.importErrorMessage(_currentLanguage.value)
                return@launch
            }
            try {
                val parsed = com.example.util.BackupHelper.parseBackupJson(json)
                if (parsed.students.isEmpty() && parsed.payments.isEmpty()) {
                    backupStatusMessage.value = if (_currentLanguage.value == com.example.util.AppLanguage.BN)
                        "ব্যাকআপ ফাইলে কোনো শিক্ষার্থী বা পেমেন্ট তথ্য পাওয়া যায়নি!"
                    else
                        "No student or payment data found in backup file!"
                } else {
                    pendingBackupData.value = parsed
                }
            } catch (e: Exception) {
                backupStatusMessage.value = com.example.util.Strings.importErrorMessage(_currentLanguage.value)
            }
        }
    }

    fun executeImport(replaceExisting: Boolean) {
        val backup = pendingBackupData.value ?: return
        viewModelScope.launch {
            val (studentsCount, paymentsCount) = repository.restoreBackup(
                backupStudents = backup.students,
                backupPayments = backup.payments,
                replaceExisting = replaceExisting
            )
            pendingBackupData.value = null
            val sStr = com.example.util.BengaliFormatter.formatDigits(studentsCount, _currentLanguage.value)
            val pStr = com.example.util.BengaliFormatter.formatDigits(paymentsCount, _currentLanguage.value)
            backupStatusMessage.value = com.example.util.Strings.importSuccessMessage(_currentLanguage.value, sStr, pStr)
        }
    }

    fun shareBackup(context: android.content.Context) {
        viewModelScope.launch {
            val students = repository.getAllStudentsList()
            val payments = repository.getAllPaymentsList()
            if (students.isEmpty() && payments.isEmpty()) {
                backupStatusMessage.value = com.example.util.Strings.exportEmptyWarning(_currentLanguage.value)
            } else {
                val json = com.example.util.BackupHelper.createBackupJson(students, payments)
                com.example.util.BackupHelper.shareBackupText(context, json)
            }
        }
    }

    fun pauseStudentBilling(studentId: Long, pauseDate: String = today.toString()) {
        viewModelScope.launch {
            val student = repository.getAllStudentsList().find { it.id == studentId } ?: return@launch
            val updated = student.copy(
                isPaused = true,
                pausedDate = pauseDate
            )
            repository.updateStudent(updated)
        }
    }

    fun resumeStudentBilling(studentId: Long, resumeDate: String = today.toString()) {
        viewModelScope.launch {
            val student = repository.getAllStudentsList().find { it.id == studentId } ?: return@launch
            val pauseDateStr = student.pausedDate ?: student.startDate
            val pauseDate = com.example.domain.BillingEngine.parseDate(pauseDateStr, today)
            val resDate = com.example.domain.BillingEngine.parseDate(resumeDate, today)
            val days = java.time.temporal.ChronoUnit.DAYS.between(pauseDate, resDate).coerceAtLeast(0)

            // "আর Pause এবং Resume এর ডেটা যদি ৩ দিনের বেশি Pause করে রাখে তাহলে সেটা তুলবে।"
            val updated = if (days > 3) {
                val currentHistory = com.example.domain.BillingEngine.parsePauseHistory(student.pauseHistoryJson).toMutableList()
                currentHistory.add(
                    com.example.domain.PauseRecord(
                        id = System.currentTimeMillis(),
                        pauseDate = pauseDateStr,
                        resumeDate = resumeDate,
                        daysPaused = days,
                        note = ""
                    )
                )
                student.copy(
                    isPaused = false,
                    pausedDate = null,
                    pauseHistoryJson = com.example.domain.BillingEngine.formatPauseHistory(currentHistory),
                    activeCycleStartDate = resumeDate
                )
            } else {
                student.copy(
                    isPaused = false,
                    pausedDate = null
                )
            }
            repository.updateStudent(updated)
        }
    }
}

