package com.genius.multiprogressbar

import android.os.Handler
import android.os.Looper
import java.util.*

internal class TimerHandler(fromValue: Float, toValue: Float, durationInMillis: Long) :
    AnimationHandler(fromValue, toValue, durationInMillis) {

    private val timer = Timer()
    private var value = fromValue
    private val period = durationInMillis / (toValue - fromValue)
    private val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    override fun start() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                value += 1F
                val runnable = Runnable {
                    listener?.onChange(value)
                }
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    runnable.run()
                } else {
                    mainHandler.post(runnable)
                }
            }
        }, 0, period.toLong())
    }

    override fun cancel() {
        timer.cancel()
    }
}