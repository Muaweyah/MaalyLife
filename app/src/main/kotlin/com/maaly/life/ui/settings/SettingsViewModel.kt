package com.maaly.life.ui.settings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maaly.life.data.AppDatabase
import com.maaly.life.data.AppSettings
import com.maaly.life.data.Category
import com.maaly.life.data.CategoryDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.util.UUID

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryDao: CategoryDao = AppDatabase.getInstance(application).categoryDao()
    private val settings = AppSettings(application)

    val categories: StateFlow<List<Category>> = categoryDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _language = MutableStateFlow(settings.language)
    val language: StateFlow<String> = _language

    private val _themeMode = MutableStateFlow(settings.themeMode)
    val themeMode: StateFlow<String> = _themeMode

    private val _isSignedIn = MutableStateFlow(settings.isSignedIn)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn

    private val _userEmail = MutableStateFlow(settings.userEmail)
    val userEmail: StateFlow<String?> = _userEmail

    fun setLanguage(lang: String) {
        settings.language = lang
        _language.value = lang
        val locales = LocaleListCompat.forLanguageTags(lang)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun setThemeMode(mode: String) {
        settings.themeMode = mode
        _themeMode.value = mode
    }

    fun toggleCategoryVisibility(category: Category) {
        viewModelScope.launch {
            categoryDao.update(category.copy(isHidden = !category.isHidden))
        }
    }

    fun addCustomCategory(nameAr: String, icon: String) {
        viewModelScope.launch {
            categoryDao.insert(
                Category(
                    id = "custom_${UUID.randomUUID()}",
                    nameAr = nameAr,
                    nameEn = nameAr,
                    icon = icon,
                    colorHex = "#9E9E9E",
                    isCustom = true
                )
            )
        }
    }

    fun deleteCustomCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.delete(category)
        }
    }

    fun signOut() {
        settings.isSignedIn = false
        settings.userEmail = null
        _isSignedIn.value = false
        _userEmail.value = null
    }
}
