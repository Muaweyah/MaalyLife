package com.maaly.life.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maaly.life.R

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
            Text(text = stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            SectionTitle(stringResource(R.string.settings_account))
            if (isSignedIn) {
                Text(text = stringResource(R.string.settings_signed_in, userEmail ?: ""))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { viewModel.signOut() }) { Text(stringResource(R.string.settings_sign_out)) }
            } else {
                Text(text = stringResource(R.string.settings_not_signed_in))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* سيُفعّل لاحقاً عبر Google Sign-In */ }) {
                    Text(stringResource(R.string.settings_sign_in_google))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionTitle(stringResource(R.string.settings_language))
            Row {
                FilterChip(
                    selected = language == "ar",
                    onClick = { viewModel.setLanguage("ar") },
                    label = { Text(stringResource(R.string.settings_lang_ar)) },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = language == "en",
                    onClick = { viewModel.setLanguage("en") },
                    label = { Text(stringResource(R.string.settings_lang_en)) }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionTitle(stringResource(R.string.settings_theme))
            Row {
                FilterChip(
                    selected = themeMode == "system",
                    onClick = { viewModel.setThemeMode("system"); onThemeChanged("system") },
                    label = { Text(stringResource(R.string.settings_theme_system)) },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = themeMode == "light",
                    onClick = { viewModel.setThemeMode("light"); onThemeChanged("light") },
                    label = { Text(stringResource(R.string.settings_theme_light)) },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = themeMode == "dark",
                    onClick = { viewModel.setThemeMode("dark"); onThemeChanged("dark") },
                    label = { Text(stringResource(R.string.settings_theme_dark)) }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionTitle(stringResource(R.string.settings_categories))
            Text(
                text = stringResource(R.string.settings_categories_hint),
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
                            Text(stringResource(R.string.settings_delete))
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
                    label = { Text(stringResource(R.string.settings_new_category_icon)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.settings_new_category_name)) }
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
            ) { Text(stringResource(R.string.settings_add_category)) }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))
}
