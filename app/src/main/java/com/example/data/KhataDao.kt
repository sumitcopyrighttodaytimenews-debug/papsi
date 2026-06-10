package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction as RoomTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface KhataDao {
    @Query("SELECT * FROM customers")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    fun getCustomerById(customerId: String): Flow<Customer?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Query("UPDATE customers SET balance = balance + :amount WHERE id = :customerId")
    suspend fun increaseBalance(customerId: String, amount: Int)

    @Query("UPDATE customers SET balance = balance - :amount WHERE id = :customerId")
    suspend fun decreaseBalance(customerId: String, amount: Int)

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestampMs DESC")
    fun getTransactions(customerId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Long): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransaction(transactionId: Long)

    @RoomTransaction
    suspend fun addTransaction(transaction: Transaction) {
        insertTransaction(transaction)
        if (transaction.type == "udhaari") {
            increaseBalance(transaction.customerId, transaction.amount)
        } else {
            decreaseBalance(transaction.customerId, transaction.amount)
        }
    }

    @RoomTransaction
    suspend fun removeTransaction(transactionId: Long) {
        val transaction = getTransactionById(transactionId) ?: return
        deleteTransaction(transactionId)
        if (transaction.type == "udhaari") {
            decreaseBalance(transaction.customerId, transaction.amount)
        } else {
            increaseBalance(transaction.customerId, transaction.amount)
        }
    }
}
