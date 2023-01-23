package ru.dikoresearch.blesimplecontrollerapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BleSimpleControllerApp: Application() {
    override fun onCreate() {
        super.onCreate()

        //Add log analytics
    }
}