package ru.dikoresearch.blesimplecontrollerapp.repository

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.util.Log
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.dikoresearch.blesimplecontrollerapp.service.DEVICE_DATA
import ru.dikoresearch.blesimplecontrollerapp.service.NotificationService
import javax.inject.Inject

@AndroidEntryPoint
class SimpleControllerService: NotificationService() {

    @Inject
    lateinit var repository: SimpleControllerRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val device = intent!!.getParcelableExtra<BluetoothDevice>(DEVICE_DATA)!!

        repository.start(device, lifecycleScope)

//        repository.hasBeenDisconnected.onEach {
//            if (it){
//                Log.e("Simple Controller Service", "found disconnecting device")
//                stopSelf()
//            }
//        }.launchIn(lifecycleScope)

        return START_REDELIVER_INTENT

    }

    override fun onDestroy() {
        Log.e("SimpleControllerService", "stopping service")
        repository.release()
        super.onDestroy()
    }
}