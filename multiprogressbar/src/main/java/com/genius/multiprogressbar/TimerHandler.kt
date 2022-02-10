package com.genius.multiprogressbar

import java.util.*

internal class TimerHandler(fromValue: Float, toValue: Float, durationInMillis: Long) :
    AnimationHandler(fromValue, toValue, durationInMillis) {

    private val timer = Timer()
    private var value = fromValue
    private val period = durationInMillis / (toValue - fromValue)

    override fun start() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                value += 1F
                listener?.onChange(value)
            }
        }, 0, period.toLong())
    }

    override fun cancel() {
        timer.cancel()
    }
}