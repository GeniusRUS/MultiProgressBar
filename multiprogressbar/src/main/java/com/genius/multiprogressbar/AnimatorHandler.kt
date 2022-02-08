package com.genius.multiprogressbar

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

internal class AnimatorHandler(fromValue: Float, toValue: Float, durationInMillis: Long) :
    AnimationHandler(fromValue, toValue, durationInMillis) {

    private var animator: Animator = ValueAnimator.ofFloat(fromValue, toValue).apply {
        duration = durationInMillis
        addUpdateListener { animator ->
            val value = animator.animatedValue as Float
            listener?.onChange(value)
        }
        interpolator = LinearInterpolator()
    }

    override fun start() {
        animator.start()
    }

    override fun cancel() {
        animator.removeAllListeners()
        animator.cancel()
    }
}