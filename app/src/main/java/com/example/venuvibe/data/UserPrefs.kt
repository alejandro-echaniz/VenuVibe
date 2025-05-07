package com.example.venuvibe.data

import android.content.Context
import androidx.annotation.ColorInt

object UserPrefs {
    private const val PREFS_NAME = "venuvibe_prefs"
    private const val KEY_DARK_MODE = "dark_mode_enabled"
    private const val KEY_SHOW_PAST = "show_past_enabled"
    private const val KEY_ACCENT_COLOR = "accent_color"

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Dark Mode
    fun saveDarkModeEnabled(ctx: Context, enabled: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }
    fun isDarkModeEnabled(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_DARK_MODE, false)

    fun saveShowPastEnabled(ctx: Context, enabled: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_SHOW_PAST, enabled).apply()
    }

    fun isShowPastEnabled(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_SHOW_PAST, false)

    fun saveAccentColor(ctx: Context, @ColorInt color: Int) {
        prefs(ctx).edit()
            .putInt(KEY_ACCENT_COLOR, color)
            .apply()
    }

    @ColorInt
    fun getAccentColor(ctx: Context): Int =
        prefs(ctx).getInt(KEY_ACCENT_COLOR, 0xFF6200EE.toInt())
}
