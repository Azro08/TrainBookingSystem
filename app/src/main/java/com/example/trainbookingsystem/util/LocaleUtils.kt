package com.example.trainbookingsystem.util

import android.content.Context
import java.util.Locale

class LocaleUtils(private val context: Context) {

    fun saveLang(locale: String) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.LANGUAGE_KEY, locale).apply()
    }

}

fun Context.setLocale(languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    return createConfigurationContext(config)
}