package ru.dikoresearch.blesimplecontrollerapp.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.dikoresearch.blesimplecontrollerapp.domain.entities.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalSettingsRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val gson: Gson,
) {
    private val prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

    fun loadApplicationSettings(): ApplicationSettings{
        val s = prefs.getString(APPLICATION_SETTINGS, "") ?: return defaultApplicationSettings
        return try {
            gson.fromJson(s, ApplicationSettings::class.java)
        }
        catch (e: Exception){
            Log.e(TAG, "Exception during conversation $e")
            defaultApplicationSettings
        }
    }

    fun loadControllerSettings(): ControllerSettings{
        val s = prefs.getString(CONTROLLER_SETTINGS, "") ?: return defaultControllerSettings
        return try {
            gson.fromJson(s, ControllerSettings::class.java)
        }
        catch (e: Exception){
            Log.e(TAG, "Exception during conversation $e")
            defaultControllerSettings
        }
    }

    fun setApplicationSettings(settings: ApplicationSettings){
        try {
            val s = gson.toJson(settings)
            prefs.edit().putString(APPLICATION_SETTINGS, s).apply()
        }
        catch (e: Exception){
            Log.e(TAG, "Exception during conversation object -> json $e")
        }
    }

    fun setControllerSettings(settings: ControllerSettings){
        try {
            val s = gson.toJson(settings)
            prefs.edit().putString(CONTROLLER_SETTINGS, s).apply()
        }
        catch (e: Exception){
            Log.e(TAG, "Exception during conversation object -> json $e")
        }
    }

    fun clearAllSettings(){
        setApplicationSettings(defaultApplicationSettings)
        setControllerSettings(defaultControllerSettings)
    }

    companion object {
        const val TAG = "Local Settings Repository"
        const val SHARED_PREFS_NAME = "BASIC_PREFERENCES"
        const val APPLICATION_SETTINGS = "APPLICATION_SETTINGS"
        const val CONTROLLER_SETTINGS = "CONTROLLER_SETTINGS"
        const val REMOTE_SETTINGS = "REMOTE_SETTINGS"

        val defaultPressureSensor = PressureSensor(
            name = "Standart",
            alias = "",
            info = "",
            maxPressureBar = 20.0f,
            k = 3.45f,
            b = -1.725f
        )

        val defaultApplicationSettings = ApplicationSettings(
            controllerType = ControllerType.SimplePressureController,
            controlScheme = ControlScheme.OneWayController,
            pressureSensor = defaultPressureSensor,
            listOfPressureSensors = listOf(defaultPressureSensor),
            pressureUnits = PressureUnits.BAR,

            showTankPressure = false,
            showControls = true,
            showPressureRegulation = false
        )

        val defaultControllerSettings = ControllerSettings(
            controllerName = "",
            controllerAddress = "",
            controllerVersion = ""
        )
    }
}