package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Customer
import com.example.data.KhataRepository
import com.example.data.SecurityRepository
import com.example.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(
    private val khataRepository: KhataRepository,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val customers: StateFlow<List<Customer>> = combine(
        khataRepository.allCustomers, _searchQuery
    ) { allCustomers, query ->
        if (query.isBlank()) allCustomers else allCustomers.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBakaaya: StateFlow<Int> = khataRepository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        .let { flow ->
            combine(flow, kotlinx.coroutines.flow.flowOf(Unit)) { list, _ ->
                list.sumOf { it.balance }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun unlockApp() {
        _isUnlocked.value = true
    }

    fun addCustomer(name: String, phone: String, onNavigate: (String) -> Unit) {
        viewModelScope.launch {
            val id = "u_${System.currentTimeMillis()}"
            val customer = Customer(id, name, phone, 0)
            khataRepository.insertCustomer(customer)
            onNavigate(id)
        }
    }

    fun getCustomer(id: String): StateFlow<Customer?> {
        return khataRepository.getCustomer(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun getTransactions(customerId: String): StateFlow<List<Transaction>> {
        return khataRepository.getTransactions(customerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addTransaction(customerId: String, amount: Int, description: String, isUdhaar: Boolean) {
        viewModelScope.launch {
            val type = if (isUdhaar) "udhaari" else "jama"
            val timestamp = System.currentTimeMillis()
            val timeString = SimpleDateFormat("dd MMM hh:mm a", Locale.ENGLISH).format(Date(timestamp))
            val transaction = Transaction(
                id = timestamp,
                customerId = customerId,
                amount = amount,
                description = description,
                type = type,
                time = timeString,
                timestampMs = timestamp
            )
            khataRepository.addTransaction(transaction)
        }
    }

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            khataRepository.removeTransaction(transactionId)
        }
    }

    fun deleteCustomer(customerId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            khataRepository.deleteCustomer(customerId)
            onDeleted()
        }
    }
}

class MainViewModelFactory(
    private val khataRepository: KhataRepository,
    private val securityRepository: SecurityRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(khataRepository, securityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
