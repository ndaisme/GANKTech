package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class ChecklistItem(
    val name: String,
    val isChecked: Boolean = false
)

fun defaultChecklist() = listOf(
    ChecklistItem("LCD Display"),
    ChecklistItem("Touchscreen Panel"),
    ChecklistItem("Mic & Receiver"),
    ChecklistItem("Earpiece & Speaker"),
    ChecklistItem("Wifi & Bluetooth"),
    ChecklistItem("GPS & Sensors"),
    ChecklistItem("SIM & Network Signal"),
    ChecklistItem("Charging Port & IC"),
    ChecklistItem("Cameras (Front & Back)"),
    ChecklistItem("Flashlight / Torch"),
    ChecklistItem("Vibrator Motor"),
    ChecklistItem("Biometrics (FP / Face)")
)

data class ServiceJob(
    val id: String = UUID.randomUUID().toString(),
    val notaNumber: String = "",
    val customerName: String,
    val hpModel: String,
    val problem: String,
    val status: String, // "WAITING", "DIAGNOSA", "PENGERJAAN", "QC", "SELESAI", "DIAMBIL"
    val cost: Long,
    val date: String,
    val checklistBefore: List<ChecklistItem> = defaultChecklist(),
    val checklistAfter: List<ChecklistItem> = defaultChecklist()
)

class GankViewModel : ViewModel() {

    private val _services = MutableStateFlow<List<ServiceJob>>(emptyList())
    val services: StateFlow<List<ServiceJob>> = _services.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Filter status
    private val _selectedStatusFilter = MutableStateFlow("ALL")
    val selectedStatusFilter = _selectedStatusFilter.asStateFlow()

    // Theme toggle state (false = Light mode, true = Dark mode)
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Standalone Diagnostic Hardware Checklist state (global quick tool)
    private val _checklist = MutableStateFlow(defaultChecklist())
    val checklist: StateFlow<List<ChecklistItem>> = _checklist.asStateFlow()

    // UI Dialog & Inputs
    private val _isAddJobDialogOpen = MutableStateFlow(false)
    val isAddJobDialogOpen = _isAddJobDialogOpen.asStateFlow()

    private val _isChecklistDialogOpen = MutableStateFlow(false)
    val isChecklistDialogOpen = _isChecklistDialogOpen.asStateFlow()

    // Job Specific Checklist Dialog State
    private val _activeJobChecklistId = MutableStateFlow<String?>(null)
    val activeJobChecklistId: StateFlow<String?> = _activeJobChecklistId.asStateFlow()

    private val _activeJobChecklistIsBefore = MutableStateFlow(true)
    val activeJobChecklistIsBefore: StateFlow<Boolean> = _activeJobChecklistIsBefore.asStateFlow()

    // Temporary custom checklist during job creation
    private val _tempChecklistBefore = MutableStateFlow(defaultChecklist())
    val tempChecklistBefore: StateFlow<List<ChecklistItem>> = _tempChecklistBefore.asStateFlow()

    // Add Job Input Fields
    val inputNotaNumber = MutableStateFlow("")
    val inputCustomerName = MutableStateFlow("")
    val inputHpModel = MutableStateFlow("")
    val inputProblem = MutableStateFlow("")
    val inputCost = MutableStateFlow("")
    val inputStatus = MutableStateFlow("WAITING")

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChanged(status: String) {
        _selectedStatusFilter.value = status
    }

    fun toggleChecklistItem(index: Int) {
        val current = _checklist.value.toMutableList()
        val item = current[index]
        current[index] = item.copy(isChecked = !item.isChecked)
        _checklist.value = current
    }

    fun resetChecklist() {
        _checklist.value = defaultChecklist()
    }

    fun openAddJobDialog() {
        // Reset fields
        inputNotaNumber.value = ""
        inputCustomerName.value = ""
        inputHpModel.value = ""
        inputProblem.value = ""
        inputCost.value = ""
        inputStatus.value = "WAITING"
        _tempChecklistBefore.value = defaultChecklist()
        _isAddJobDialogOpen.value = true
    }

    fun closeAddJobDialog() {
        _isAddJobDialogOpen.value = false
    }

    fun openChecklistDialog() {
        _isChecklistDialogOpen.value = true
    }

    fun closeChecklistDialog() {
        _isChecklistDialogOpen.value = false
    }

    // Toggle items during service creation
    fun toggleTempChecklistItem(index: Int) {
        val current = _tempChecklistBefore.value.toMutableList()
        val item = current[index]
        current[index] = item.copy(isChecked = !item.isChecked)
        _tempChecklistBefore.value = current
    }

    // Open checklist editor for a specific job
    fun openJobChecklistEditor(jobId: String, isBefore: Boolean) {
        _activeJobChecklistId.value = jobId
        _activeJobChecklistIsBefore.value = isBefore
    }

    fun closeJobChecklistEditor() {
        _activeJobChecklistId.value = null
    }

    // Toggle check for specific job
    fun toggleJobChecklistItem(jobId: String, isBefore: Boolean, index: Int) {
        _services.value = _services.value.map { job ->
            if (job.id == jobId) {
                if (isBefore) {
                    val currentList = job.checklistBefore.toMutableList()
                    val item = currentList[index]
                    currentList[index] = item.copy(isChecked = !item.isChecked)
                    job.copy(checklistBefore = currentList)
                } else {
                    val currentList = job.checklistAfter.toMutableList()
                    val item = currentList[index]
                    currentList[index] = item.copy(isChecked = !item.isChecked)
                    job.copy(checklistAfter = currentList)
                }
            } else {
                job
            }
        }
    }

    fun addServiceJob() {
        val name = inputCustomerName.value.trim()
        val model = inputHpModel.value.trim()
        val prob = inputProblem.value.trim()
        val price = inputCost.value.toLongOrNull() ?: 0L
        val stat = inputStatus.value
        val nota = inputNotaNumber.value.trim()

        if (name.isNotEmpty() && model.isNotEmpty() && prob.isNotEmpty()) {
            val finalNota = if (nota.isEmpty()) {
                "GANK-" + (1000..9999).random()
            } else {
                nota
            }
            val newJob = ServiceJob(
                notaNumber = finalNota,
                customerName = name,
                hpModel = model,
                problem = prob,
                status = stat,
                cost = price,
                date = "Today",
                checklistBefore = _tempChecklistBefore.value,
                checklistAfter = defaultChecklist() // Starts fresh
            )
            _services.value = listOf(newJob) + _services.value
            closeAddJobDialog()
        }
    }

    fun updateJobStatus(jobId: String, newStatus: String) {
        _services.value = _services.value.map {
            if (it.id == jobId) it.copy(status = newStatus) else it
        }
    }

    fun deleteJob(jobId: String) {
        _services.value = _services.value.filter { it.id != jobId }
    }
}
