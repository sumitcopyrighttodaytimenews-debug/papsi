package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.data.KhataDatabase
import com.example.data.KhataRepository
import com.example.data.SecurityRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val database = remember { KhataDatabase.getDatabase(context) }
                val repository = remember { KhataRepository(database.khataDao()) }
                val securityRepository = remember { SecurityRepository(context) }
                val viewModelFactory = remember { MainViewModelFactory(repository, securityRepository) }
                val viewModel: MainViewModel = viewModel(factory = viewModelFactory)

                val navController = rememberNavController()
                val isUnlocked by viewModel.isUnlocked.collectAsState()

                LaunchedEffect(isUnlocked) {
                    if (isUnlocked) {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        if (isUnlocked && currentRoute != "auth") {
                            val title = when {
                                currentRoute == "home" -> "Khata Pro Max"
                                currentRoute == "add" -> "Naya Khata"
                                currentRoute?.startsWith("dashboard") == true -> "Action Panel"
                                currentRoute?.startsWith("passbook") == true -> "Passbook"
                                else -> "Khata Pro"
                            }
                            @OptIn(ExperimentalMaterial3Api::class)
                            TopAppBar(
                                title = { Text(title) },
                                navigationIcon = {
                                    if (currentRoute != "home" && currentRoute != "add") {
                                        IconButton(onClick = { navController.navigateUp() }) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    },
                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        if (isUnlocked && (currentRoute == "home" || currentRoute == "add")) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == "home",
                                    onClick = {
                                        navController.navigate("home") {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "add",
                                    onClick = {
                                        navController.navigate("add") {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = "New") },
                                    label = { Text("New") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "auth",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("auth") {
                            AppLockScreen(viewModel)
                        }
                        composable("home") {
                            HomeScreen(viewModel, onCustomerClick = { id ->
                                navController.navigate("dashboard/$id")
                            })
                        }
                        composable("add") {
                            AddCustomerScreen(viewModel, onCustomerAdded = { id ->
                                navController.navigate("dashboard/$id") {
                                    popUpTo("home")
                                }
                            })
                        }
                        composable("dashboard/{customerId}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("customerId") ?: ""
                            CustomerDashboardScreen(id, viewModel, onNavigateToPassbook = {
                                navController.navigate("passbook/$id")
                            })
                        }
                        composable("passbook/{customerId}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("customerId") ?: ""
                            PassbookScreen(id, viewModel)
                        }
                    }
                }
            }
        }
    }
}
