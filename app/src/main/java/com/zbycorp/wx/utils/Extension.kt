package com.zbycorp.wx.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun String.log() {
    Log.i("qfxl", this)
}

fun String?.isEmpty() = TextUtils.isEmpty(this)

inline fun <T> async(crossinline block: () -> T) =
    AsyncTaskExecutor.execute(Callable { block() })

object AsyncTaskExecutor : Executor {
    private val service = Executors.newCachedThreadPool()

    fun <TResult> execute(callable: Callable<TResult>) {
        service.submit(callable)
    }

    override fun execute(runnable: Runnable) {
        service.submit(runnable)
    }
}

inline fun <T> sync(crossinline block: () -> T) =
    Handler(Looper.getMainLooper()).post {
        block()
    }