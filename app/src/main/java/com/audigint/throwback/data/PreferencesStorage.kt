package com.audigint.throwback.data

import android.content.Context
import com.audigint.throwback.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface PreferencesStorage {
    var year: Int
    var position: Int
    var accessToken: String
    var tokenExp: Long
}

class SharedPreferencesStorage @Inject constructor(
    @ApplicationContext context: Context
) : PreferencesStorage {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override var year: Int
        get() = prefs.getInt(PREFS_KEY_YEAR, Constants.DEFAULT_YEAR)
        set(value) = prefs.edit().putInt(PREFS_KEY_YEAR, value).apply()

    override var position: Int
        get() = prefs.getInt(PREFS_KEY_POSITION, Constants.DEFAULT_POSITION)
        set(value) = prefs.edit().putInt(PREFS_KEY_POSITION, value).apply()

    override var tokenExp: Long
        get() = prefs.getLong(PREFS_KEY_TOKEN_EXP, -1)
        set(value) = prefs.edit().putLong(PREFS_KEY_TOKEN_EXP, value).apply()

    override var accessToken: String
        get() = prefs.getString(PREFS_KEY_ACCESS_TOKEN, "") ?: ""
        set(value) = prefs.edit().putString(PREFS_KEY_ACCESS_TOKEN, value).apply()

    companion object {
        const val PREFS_NAME = "throwback"
        const val PREFS_KEY_YEAR = "year"
        const val PREFS_KEY_POSITION = "position"
        const val PREFS_KEY_ACCESS_TOKEN = "accessToken"
        const val PREFS_KEY_TOKEN_EXP = "tokenExpTime"
    }
}