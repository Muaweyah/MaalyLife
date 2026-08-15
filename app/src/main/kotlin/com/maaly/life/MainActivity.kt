package com.maaly.life

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.maaly.life.data.AppSettings
import com.maaly.life.ui.TaskViewModel
import com.maaly.life.ui.calendar.CalendarScreen
import com.maaly.life.ui.focus.PomodoroScreen
import com.maaly.life.ui.settings.SettingsScreen
import com.maaly.life.ui.stats.StatsScreen
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings = remember { AppSettings(applicationContext) }
            var themeMode by remember { mutableStateOf(settings.themeMode) }

            val useDarkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            MaterialTheme(
                colorScheme = if (useDarkTheme) darkColorScheme() else lightColorScheme()
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(onThemeChanged = { themeMode = it })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(onThemeChanged: (String) -> Unit) {
    val navController = rememberNavController()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            if (currentRoute != "settings") {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_title))
                        }
                    }
                )
            }
        },
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            if (currentRoute != "settings") {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "daily",
                        onClick = { navController.navigate("daily") },
                        icon = { Icon(Icons.Filled.CheckCircle, contentDescription = stringResource(R.string.nav_today)) },
                        label = { Text(stringResource(R.string.nav_today)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "calendar",
                        onClick = { navController.navigate("calendar") },
                        icon = { Icon(Icons.Filled.DateRange, contentDescription = stringResource(R.string.nav_calendar)) },
                        label = { Text(stringResource(R.string.nav_calendar)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "focus",
                        onClick = { navController.navigate("focus") },
                        icon = { Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.nav_focus)) },
                        label = { Text(stringResource(R.string.nav_focus)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "stats",
                        onClick = { navController.navigate("stats") },
                        icon = { Icon(Icons.Filled.List, contentDescription = stringResource(R.string.nav_stats)) },
                        label = { Text(stringResource(R.string.nav_stats)) }
                    )
                }
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
            composable("focus") { PomodoroScreen() }
            composable("stats") { StatsScreen() }
            composable("settings") { SettingsScreen(onThemeChanged = onThemeChanged) }
        }
    }
}

@Composable
fun DailyScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasksForCurrentDate().collectAsState(initial = emptyList())
    val visibleCategories by viewModel.visibleCategories.collectAsState()
    var newTaskTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<com.maaly.life.data.Category?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(visibleCategories) {
        if (selectedCategory == null && visibleCategories.isNotEmpty()) {
            selectedCategory = visibleCategories.first()
        }
    }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val chooseCategoryText = stringResource(R.string.daily_choose_category)
    val noReminderText = stringResource(R.string.daily_no_reminder)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = stringResource(R.string.daily_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = newTaskTitle,
            onValueChange = { newTaskTitle = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.daily_new_task)) }
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(text = selectedCategory?.let { "${it.icon} ${it.nameAr}" } ?: chooseCategoryText)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    visibleCategories.forEach { cat ->
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

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(onClick = {
                android.app.TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        reminderTime = String.format("%02d:%02d", hour, minute)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }) {
                Icon(Icons.Filled.Notifications, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = reminderTime ?: noReminderText)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val cat = selectedCategory
                if (newTaskTitle.isNotBlank() && cat != null) {
                    viewModel.addTask(newTaskTitle, cat.id, reminderTime)
                    newTaskTitle = ""
                    reminderTime = null
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.daily_add)) }

        Spacer(modifier = Modifier.height(16.dp))

        if (tasks.isEmpty()) {
            Text(text = stringResource(R.string.daily_empty))
        } else {
            LazyColumn {
                items(tasks) { task ->
                    val cat = visibleCategories.find { it.id == task.category }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { viewModel.toggleTask(task) }
                            )
                            Column {
                                Text(text = "${cat?.icon ?: ""} ${task.title}")
                                if (task.reminderTime != null) {
                                    Text(
                                        text = "⏰ ${task.reminderTime}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                        IconButton(onClick = { viewModel.deleteTask(task) }) {
                            Icon(Icons.Filled.Close, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
