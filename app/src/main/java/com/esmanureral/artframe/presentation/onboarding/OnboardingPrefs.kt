package com.esmanureral.artframe.presentation.onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class OnboardingPrefs(context: Context) {

    private val appContext = context.applicationContext

    companion object {
        private val KEY_DONE = booleanPreferencesKey("onboarding_done")
    }

    fun observeOnboardingDone(): Flow<Boolean> {
        return appContext.dataStore.data
            .map { prefs -> readDoneValue(prefs) }
    }

    suspend fun isDone(): Boolean {
        return observeOnboardingDone().first()
    }

    suspend fun setDone(done: Boolean) {
        saveDoneValue(done)
    }

    private fun readDoneValue(
        prefs: androidx.datastore.preferences.core.Preferences
    ): Boolean {
        return prefs[KEY_DONE] ?: false
    }

    private suspend fun saveDoneValue(done: Boolean) {
        appContext.dataStore.edit { prefs ->
            prefs[KEY_DONE] = done
        }
    }
}