package com.genius.multiprogressbar

internal abstract class AnimationHandler(fromValue: Float, toValue: Float, durationInMillis: Long) {
    var listener: AnimationChangeListener? = null
    abstract fun cancel()
    abstract fun start()
}

internal fun interface AnimationChangeListener {
    fun onChange(value: Float)
}