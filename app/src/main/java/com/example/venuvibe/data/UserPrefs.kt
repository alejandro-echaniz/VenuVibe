package com.example.venuvibe.data

import android.content.Context

object UserPrefs {
    private const val PREFS_NAME = "venuvibe_prefs"
    private const val KEY_DARK_MODE = "dark_mode_enabled"
    private const val KEY_NOTIFICATIONS = "notifications_enabled"

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Dark Mode
    fun saveDarkModeEnabled(ctx: Context, enabled: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }
    fun isDarkModeEnabled(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_DARK_MODE, false)

    // Notifications
    fun saveNotificationsEnabled(ctx: Context, enabled: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }
    fun areNotificationsEnabled(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_NOTIFICATIONS, false)
}
