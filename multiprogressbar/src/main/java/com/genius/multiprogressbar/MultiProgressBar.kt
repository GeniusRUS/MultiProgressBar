package com.genius.multiprogressbar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.os.Parcel
import android.view.animation.LinearInterpolator
import kotlin.math.min

@Suppress("UNUSED")
class MultiProgressBar @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(
    context,
    attributeSet,
    defStyle
) {

    private val paint = Paint()
    private var progressColor: Int
    private var lineColor: Int
    private var progressPadding: Float
    private var progressWidth = 10F
    private var singleProgressWidth: Float = 0F
    private var countOfProgressSteps: Int = 1
    private var isNeedRestoreProgressAfterRecreate: Boolean = false
    private var singleDisplayedTime: Float = 1F

    private var stepChangeListener: ProgressStepChangeListener? = null
    private var progressPercents: Int

    private var currentAbsoluteProgress = 0F
    private var animatedAbsoluteProgress = 0F
    private var isProgressIsRunning = false
    private var displayedStepForListener = -1
    private var activeAnimator: ValueAnimator? = null

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MultiProgressBar)
        lineColor = typedArray.getColor(R.styleable.MultiProgressBar_lineColor, Color.GRAY)
        progressColor = typedArray.getColor(R.styleable.MultiProgressBar_progressColor, Color.WHITE)
        progressPadding = typedArray.getDimension(R.styleable.MultiProgressBar_progressPadding, MIN_PADDING.toPx)
        countOfProgressSteps = typedArray.getInt(R.styleable.MultiProgressBar_progressSteps, 1)
        progressWidth = typedArray.getDimension(R.styleable.MultiProgressBar_progressWidth, 10F)
        progressPercents = typedArray.getInt(R.styleable.MultiProgressBar_progressPercents, 100)
        isNeedRestoreProgressAfterRecreate = typedArray.getBoolean(R.styleable.MultiProgressBar_isNeedRestoreProgress, false)
        singleDisplayedTime = typedArray.getFloat(R.styleable.MultiProgressBar_singleDisplayedTime, 1F).coerceAtLeast(0.1F)
        typedArray.recycle()

        if (isInEditMode) {
            currentAbsoluteProgress = countOfProgressSteps / 2F * progressPercents
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 100
        val desiredHeight = progressWidth.toInt() + 5

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize //Must be this size
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize) //Can't be bigger than...
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> desiredWidth //Be whatever you want
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize //Must be this size
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize) //Can't be bigger than...
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> desiredHeight //Be whatever you want
        }

        setMeasuredDimension(width, height)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState() ?: return null

        return MultiProgressBarSavedState(superState).apply {
            progressColor = this@MultiProgressBar.progressColor
            lineColor = this@MultiProgressBar.lineColor
            countProgress = this@MultiProgressBar.countOfProgressSteps
            progressPercents = this@MultiProgressBar.progressPercents
            progressPadding = this@MultiProgressBar.progressPadding
            progressWidth = this@MultiProgressBar.progressWidth
            singleProgressWidth = this@MultiProgressBar.singleProgressWidth
            currentAbsoluteProgress = this@MultiProgressBar.currentAbsoluteProgress
            animatedAbsoluteProgress = this@MultiProgressBar.animatedAbsoluteProgress
            isProgressIsRunning = this@MultiProgressBar.isProgressIsRunning
            displayedStepForListener = this@MultiProgressBar.displayedStepForListener
            isNeedRestoreProgressAfterRecreate = this@MultiProgressBar.isNeedRestoreProgressAfterRecreate
            singleDisplayedTime = this@MultiProgressBar.singleDisplayedTime
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is MultiProgressBarSavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)

        progressColor = state.progressColor
        lineColor = state.lineColor
        countOfProgressSteps = state.countProgress
        progressPercents = state.progressPercents
        progressPadding = state.progressPadding
        progressWidth = state.progressWidth
        singleProgressWidth = state.singleProgressWidth
        currentAbsoluteProgress = state.currentAbsoluteProgress
        animatedAbsoluteProgress = state.animatedAbsoluteProgress
        displayedStepForListener = state.displayedStepForListener
        isNeedRestoreProgressAfterRecreate = state.isNeedRestoreProgressAfterRecreate
        isProgressIsRunning = state.isProgressIsRunning
        singleDisplayedTime = state.singleDisplayedTime

        if (isProgressIsRunning && isNeedRestoreProgressAfterRecreate) {
            pause()
            internalStartProgress()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        internalSetProgressStepsCount(countOfProgressSteps)
    }

    override fun onDraw(canvas: Canvas) {
        for (step in 0 until countOfProgressSteps) {
            val previousPaddingSum = progressPadding + progressPadding * step
            val startX = previousPaddingSum + singleProgressWidth * step
            val endX = if (step == countOfProgressSteps - 1) {
                measuredWidth - progressPadding
            } else {
                startX + singleProgressWidth
            }

            if (step > currentAbsoluteProgress / progressPercents - 1) {
                paint.changePaintModeToBackground()
            } else {
                paint.changePaintModeToProgress()
            }

            canvas.drawLine(startX, measuredHeight / 2F, endX, measuredHeight / 2F, paint)

            val progressMultiplier = currentAbsoluteProgress / progressPercents - step
            if (progressMultiplier < 1F && progressMultiplier > 0F) {
                val progressEndX = startX + singleProgressWidth * progressMultiplier
                paint.changePaintModeToProgress()
                canvas.drawLine(startX, measuredHeight / 2F, progressEndX, measuredHeight / 2F, paint)
            }
        }
    }

    fun setListener(stepChangeListener: ProgressStepChangeListener?) {
        this.stepChangeListener = stepChangeListener
    }

    fun setProgressStepsCount(progressSteps: Int) {
        internalSetProgressStepsCount(progressSteps)
    }

    fun getProgressStepsCount(): Int = countOfProgressSteps

    fun start() {
        if (isProgressIsRunning) return
        pause()
        internalStartProgress()
    }

    fun pause() {
        isProgressIsRunning = false
        activeAnimator?.cancel()
    }

    fun next() {
        if (isProgressIsRunning) {
            pause()

            val currentStep = (currentAbsoluteProgress / progressPercents).toInt()
            currentAbsoluteProgress = (currentStep + 1F).coerceAtMost(countOfProgressSteps.toFloat()) * progressPercents
            animatedAbsoluteProgress = currentAbsoluteProgress

            start()
        } else {
            val currentStep = (currentAbsoluteProgress / progressPercents).toInt()
            currentAbsoluteProgress = (currentStep + 1F).coerceAtMost(countOfProgressSteps.toFloat()) * progressPercents
            animatedAbsoluteProgress = currentAbsoluteProgress
            invalidate()
        }
    }

    fun previous() {
        if (isProgressIsRunning) {
            pause()

            val currentStep = (currentAbsoluteProgress / progressPercents).toInt()
            currentAbsoluteProgress = (currentStep - 1F).coerceAtLeast(0F) * progressPercents
            animatedAbsoluteProgress = currentAbsoluteProgress

            start()
        } else {
            val currentStep = (currentAbsoluteProgress / progressPercents).toInt()
            currentAbsoluteProgress = (currentStep - 1F).coerceAtLeast(0F) * progressPercents
            animatedAbsoluteProgress = currentAbsoluteProgress
            invalidate()
        }
    }

    fun clear() {
        if (isProgressIsRunning) {
            pause()
        }

        currentAbsoluteProgress = 0F
        animatedAbsoluteProgress = 0F
        displayedStepForListener = -1
        invalidate()
    }

    fun getCurrentStep(): Int {
        return (currentAbsoluteProgress / progressPercents).toInt()
    }

    fun setProgressPercents(progressPercents: Int) {
        this.progressPercents = progressPercents
    }

    fun getProgressPercents(): Int {
        return this.progressPercents
    }

    private fun internalStartProgress() {
        val maxValue = countOfProgressSteps * progressPercents.toFloat()
        activeAnimator = ValueAnimator.ofFloat(animatedAbsoluteProgress, maxValue).apply {
            duration = (singleDisplayedTime * 1000 * countOfProgressSteps * (1 - (animatedAbsoluteProgress / maxValue))).toLong()
            addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                isProgressIsRunning = value != maxValue

                if ((value / progressPercents).toInt() != displayedStepForListener) {
                    displayedStepForListener = (value / progressPercents).toInt()
                    stepChangeListener?.onProgressStepChange(displayedStepForListener)
                }

                if (isProgressIsRunning) {
                    internalUpdateProgress(value)
                    animatedAbsoluteProgress = value
                } else {
                    animator.removeAllUpdateListeners()
                    animatedAbsoluteProgress = 0F
                    displayedStepForListener = -1
                }
            }
            interpolator = LinearInterpolator()
        }
        activeAnimator?.start()
    }

    private fun internalUpdateProgress(progress: Float) {
        currentAbsoluteProgress = progress.coerceAtMost(countOfProgressSteps * progressPercents.toFloat())
        invalidate()
    }

    private fun internalSetProgressStepsCount(count: Int) {
        countOfProgressSteps = count
        singleProgressWidth = (measuredWidth - progressPadding * countOfProgressSteps - progressPadding) / countOfProgressSteps
        check(!(measuredWidth != 0 && singleProgressWidth < 0)) { "There is not enough space to draw a MultiProgressBar" }
    }

    private fun Paint.changePaintModeToProgress() {
        reset()
        strokeCap = Paint.Cap.ROUND
        strokeWidth = progressWidth
        style = Paint.Style.STROKE
        isDither = true
        isAntiAlias = true
        color = progressColor
    }

    private fun Paint.changePaintModeToBackground() {
        reset()
        strokeCap = Paint.Cap.ROUND
        strokeWidth = progressWidth
        style = Paint.Style.STROKE
        isDither = true
        isAntiAlias = true
        color = lineColor
    }

    private val Float.toPx: Float
        get() = this * context.resources.displayMetrics.density

    interface ProgressStepChangeListener {
        fun onProgressStepChange(newStep: Int)
    }

    companion object {
        private const val MIN_PADDING = 8F
    }

    private class MultiProgressBarSavedState : BaseSavedState {
        var progressColor: Int = 0
        var lineColor: Int = 0
        var progressPadding: Float = 0F
        var progressWidth = 10F
        var singleProgressWidth: Float = 0F
        var animatedAbsoluteProgress: Float = 0F
        var currentAbsoluteProgress = 0F
        var countProgress: Int = 1
        var progressPercents: Int = 0
        var displayedStepForListener: Int = -1
        var isProgressIsRunning: Boolean = false
        var isNeedRestoreProgressAfterRecreate: Boolean = false
        var singleDisplayedTime: Float = 1F

        constructor(superState: Parcelable) : super(superState)

        private constructor(`in`: Parcel) : super(`in`) {
            this.progressColor = `in`.readInt()
            this.lineColor = `in`.readInt()
            this.countProgress = `in`.readInt()
            this.progressPercents = `in`.readInt()
            this.progressPadding = `in`.readFloat()
            this.progressWidth = `in`.readFloat()
            this.singleProgressWidth = `in`.readFloat()
            this.currentAbsoluteProgress = `in`.readFloat()
            this.animatedAbsoluteProgress = `in`.readFloat()
            this.isProgressIsRunning = `in`.readInt() == 1
            this.isNeedRestoreProgressAfterRecreate = `in`.readInt() == 1
            this.displayedStepForListener = `in`.readInt()
            this.singleDisplayedTime = `in`.readFloat()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(this.progressColor)
            out.writeInt(this.lineColor)
            out.writeInt(this.countProgress)
            out.writeInt(this.progressPercents)
            out.writeFloat(this.progressPadding)
            out.writeFloat(this.progressWidth)
            out.writeFloat(this.singleProgressWidth)
            out.writeFloat(this.currentAbsoluteProgress)
            out.writeFloat(this.animatedAbsoluteProgress)
            out.writeInt(if (this.isProgressIsRunning) 1 else 0)
            out.writeInt(if (this.isNeedRestoreProgressAfterRecreate) 1 else 0)
            out.writeInt(displayedStepForListener)
            out.writeFloat(singleDisplayedTime)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<MultiProgressBarSavedState> {
            override fun createFromParcel(parcel: Parcel): MultiProgressBarSavedState {
                return MultiProgressBarSavedState(parcel)
            }

            override fun newArray(size: Int): Array<MultiProgressBarSavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}