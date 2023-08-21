package pt.ipp.estg.myapplication.helper

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleUtils {
    // [AppPrefs] is sharedpreferences or datastore
    fun setLocale(c: Context, prefLanguage: String?) =
        updateResources(c, prefLanguage ?: "en") //use locale codes

    private fun updateResources(context: Context, language: String) {
        context.resources.apply {
            val locale = Locale(language)
            val config = Configuration(configuration)
            context.createConfigurationContext(configuration)
            Locale.setDefault(locale)
            config.setLocale(locale)
            context.resources.updateConfiguration(config, displayMetrics)
        }
    }
}