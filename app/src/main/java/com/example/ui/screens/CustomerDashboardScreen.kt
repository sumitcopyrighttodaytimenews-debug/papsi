package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.MainViewModel
import com.example.ui.components.Avatar
import com.example.ui.theme.Danger
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.Success
import com.example.ui.theme.Water
import com.example.ui.theme.Wine

@Composable
fun CustomerDashboardScreen(
    customerId: String,
    viewModel: MainViewModel,
    onNavigateToPassbook: () -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val customer by remember(customerId) { viewModel.getCustomer(customerId) }.collectAsState(null)
    var udhaarAmt by remember { mutableStateOf("") }
    var jamaAmt by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (customer == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header Profile
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PrimaryLight),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Avatar(name = customer!!.name)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = customer!!.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = customer!!.phone.ifEmpty { "No Number" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "₹${customer!!.balance}",
                    color = if (customer!!.balance > 0) Danger else if (customer!!.balance < 0) Success else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${customer!!.phone}")
                    }
                    if (customer!!.phone.isNotEmpty()) context.startActivity(intent)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Success),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call")
            }
            
            Button(
                onClick = {
                    val msg = "Namaste ${customer!!.name}, aapka Khata Pro par kul bakaaya ₹${customer!!.balance} hai. Kripaya jald se jald jama karein."
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, msg)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Reminder"))
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Reminder")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reminder")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "QUICK ENTRY",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.addTransaction(customerId, 50, "1 Glass", true) },
                colors = ButtonDefaults.buttonColors(containerColor = Wine),
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.WineBar, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("1 Glass")
            }
            Button(
                onClick = { viewModel.addTransaction(customerId, 30, "Half Glass", true) },
                colors = ButtonDefaults.buttonColors(containerColor = Water),
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.LocalDrink, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Half Glass")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "MANUAL ENTRY",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = udhaarAmt,
                        onValueChange = { udhaarAmt = it },
                        placeholder = { Text("₹ Udhaar") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = {
                            val amt = udhaarAmt.toIntOrNull()
                            if (amt != null && amt > 0) {
                                viewModel.addTransaction(customerId, amt, "Custom Udhaari", true)
                                udhaarAmt = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Danger),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Udhaar")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = jamaAmt,
                        onValueChange = { jamaAmt = it },
                        placeholder = { Text("₹ Jama") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = {
                            val amt = jamaAmt.toIntOrNull()
                            if (amt != null && amt > 0) {
                                viewModel.addTransaction(customerId, amt, "Cash Jama", false)
                                jamaAmt = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Success),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Jama")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToPassbook,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.History, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Passbook Dekhein", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { showDeleteConfirm = true },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Danger,
                contentColor = androidx.compose.ui.graphics.Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Khata Delete Karein", fontWeight = FontWeight.Bold)
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete Khata?") },
                text = { Text("Kya aap sach me is customer ka khata hamesha ke liye delete karna chahte hain? Iski saari entry delete ho jayengi.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteCustomer(customerId) {
                            onDeleteSuccess()
                        }
                    }) {
                        Text("Haan, Delete Karein", color = Danger)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
