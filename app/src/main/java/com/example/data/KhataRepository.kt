package com.example.data

import kotlinx.coroutines.flow.Flow

class KhataRepository(private val khataDao: KhataDao) {
    val allCustomers: Flow<List<Customer>> = khataDao.getAllCustomers()

    fun getCustomer(id: String): Flow<Customer?> = khataDao.getCustomerById(id)

    suspend fun insertCustomer(customer: Customer) {
        khataDao.insertCustomer(customer)
    }

    fun getTransactions(customerId: String): Flow<List<Transaction>> = khataDao.getTransactions(customerId)

    suspend fun addTransaction(transaction: Transaction) {
        khataDao.addTransaction(transaction)
    }

    suspend fun removeTransaction(transactionId: Long) {
        khataDao.removeTransaction(transactionId)
    }
}
