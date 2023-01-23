package ru.dikoresearch.blesimplecontrollerapp.utils

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private val exceptionHandler = CoroutineExceptionHandler { _, t ->
    Log.e("COROUTINE-EXCEPTION", "Uncaught exception", t)
}

fun CoroutineScope.launchWithCatch(block: suspend CoroutineScope.() -> Unit) =
    launch(Job() + exceptionHandler) {
        block()
    }