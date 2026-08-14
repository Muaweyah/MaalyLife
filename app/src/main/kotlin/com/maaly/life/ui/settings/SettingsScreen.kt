package com.maaly.life.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(onThemeChanged: (String) -> Unit = {}, viewModel: SettingsViewModel = viewModel()) {
    val categories by viewModel.categories.collectAsState()
    val language by viewModel.language.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isSignedIn by viewModel.isSignedIn.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    var newCatName by remember { mutableStateOf("") }
    var newCatIcon by remember { mutableStateOf("🔖") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "الإعدادات", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            SectionTitle("الحساب")
            if (isSignedIn) {
                Text(text = "مسجل الدخول: ${userEmail ?: ""}")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { viewModel.signOut() }) { Text("تسجيل الخروج") }
            } else {
                Text(text = "غير مسجل الدخول")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* سيُفعّل لاحقاً عبر Google Sign-In */ }) {
                    Text("تسجيل الدخول عبر جوجل (قريباً)")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionTitle("اللغة")
            Row {
                FilterChip(
                    selected = language == "ar",
                    onClick = { viewModel.setLanguage("ar") },
                    label = { Text("العربية") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = language == "en",
                    onClick = { viewModel.setLanguage("en") },
                    label = { Text("English") }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionTitle("المظهر")
            Row {
                FilterChip(
                    selected = themeMode == "system",
                    onClick = { viewModel.setThemeMode("system"); onThemeChanged("system") },
                    label = { Text("تلقائي") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = themeMode == "light",
                    onClick = { viewModel.setThemeMode("light"); onThemeChanged("light") },
                    label = { Text("فاتح") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = themeMode == "dark",
                    onClick = { viewModel.setThemeMode("dark"); onThemeChanged("dark") },
                    label = { Text("داكن") }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionTitle("التصنيفات")
            Text(
                text = "فعّل أو أخفِ أي تصنيف، أو أضف تصنيفك الخاص",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(categories) { cat ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${cat.icon} ${cat.nameAr}")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = !cat.isHidden,
                        onCheckedChange = { viewModel.toggleCategoryVisibility(cat) }
                    )
                    if (cat.isCustom) {
                        TextButton(onClick = { viewModel.deleteCustomCategory(cat) }) {
                            Text("حذف")
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                OutlinedTextField(
                    value = newCatIcon,
                    onValueChange = { if (it.length <= 2) newCatIcon = it },
                    modifier = Modifier.width(70.dp),
                    label = { Text("رمز") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("تصنيف جديد") }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (newCatName.isNotBlank()) {
                        viewModel.addCustomCategory(newCatName, newCatIcon)
                        newCatName = ""
                        newCatIcon = "🔖"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("إضافة تصنيف") }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))
}
