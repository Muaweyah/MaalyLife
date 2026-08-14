package com.maaly.life

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.maaly.life.data.Category
import com.maaly.life.ui.TaskViewModel
import com.maaly.life.ui.calendar.CalendarScreen
import com.maaly.life.ui.stats.StatsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                NavigationBarItem(
                    selected = currentRoute == "daily",
                    onClick = { navController.navigate("daily") },
                    icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "اليوم") },
                    label = { Text("اليوم") }
                )
                NavigationBarItem(
                    selected = currentRoute == "calendar",
                    onClick = { navController.navigate("calendar") },
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = "التقويم") },
                    label = { Text("التقويم") }
                )
                NavigationBarItem(
                    selected = currentRoute == "stats",
                    onClick = { navController.navigate("stats") },
                    icon = { Icon(Icons.Filled.List, contentDescription = "الإحصائيات") },
                    label = { Text("الإحصائيات") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "daily",
            modifier = Modifier.padding(padding)
        ) {
            composable("daily") { DailyScreen() }
            composable("calendar") { CalendarScreen() }
            composable("stats") { StatsScreen() }
        }
    }
}

@Composable
fun DailyScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasksForCurrentDate().collectAsState(initial = emptyList())
    var newTaskTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(viewModel.categories.first()) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "مهام اليوم", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = newTaskTitle,
            onValueChange = { newTaskTitle = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("مهمة جديدة") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(text = "${selectedCategory.icon} ${selectedCategory.nameAr}")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                viewModel.categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text("${cat.icon} ${cat.nameAr}") },
                        onClick = {
                            selectedCategory = cat
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newTaskTitle.isNotBlank()) {
                    viewModel.addTask(newTaskTitle, selectedCategory.id)
                    newTaskTitle = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("إضافة") }

        Spacer(modifier = Modifier.height(16.dp))

        if (tasks.isEmpty()) {
            Text(text = "لا توجد مهام بعد — أضف أول مهمة لك")
        } else {
            LazyColumn {
                items(tasks) { task ->
                    val cat = viewModel.categories.find { it.id == task.category }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { viewModel.toggleTask(task) }
                        )
                        Text(text = "${cat?.icon ?: ""} ${task.title}")
                    }
                }
            }
        }
    }
}
