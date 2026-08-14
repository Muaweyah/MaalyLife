package com.maaly.life.data

import android.content.Context
import android.content.SharedPreferences

class AppSettings(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("maaly_settings", Context.MODE_PRIVATE)

    var language: String
        get() = prefs.getString("language", "ar") ?: "ar"
        set(value) = prefs.edit().putString("language", value).apply()

    var themeMode: String
        get() = prefs.getString("theme_mode", "system") ?: "system"
        set(value) = prefs.edit().putString("theme_mode", value).apply()

    var isSignedIn: Boolean
        get() = prefs.getBoolean("is_signed_in", false)
        set(value) = prefs.edit().putBoolean("is_signed_in", value).apply()

    var userEmail: String?
        get() = prefs.getString("user_email", null)
        set(value) = prefs.edit().putString("user_email", value).apply()
}
