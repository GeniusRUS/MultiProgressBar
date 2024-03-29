package com.genius.multiprogressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import androidx.annotation.IntRange

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
    private var finishListener: ProgressFinishListener? = null
    private var progressPercents: Int

    private var currentAbsoluteProgress = 0F
    private var animatedAbsoluteProgress = 0F
    private var isProgressIsRunning = false
    private var displayedStepForListener = -1
    private var activeAnimator: AnimationHandler? = null
    private var isCompactMode: Boolean = false
    @Orientation
    var orientation: Int = Orientation.TO_RIGHT
        set(value) {
            require(Orientation.ALL.contains(value))
            field = value
            invalidate()
        }

    val isPause: Boolean
        get() = !isProgressIsRunning

    private val relativePaddingStart: Int
        get() = when (orientation) {
            Orientation.TO_TOP -> paddingBottom
            Orientation.TO_LEFT -> paddingRight
            Orientation.TO_BOTTOM -> paddingTop
            Orientation.TO_RIGHT -> paddingLeft
            else -> 0
        }

    private val relativePaddingEnd: Int
        get() = when (orientation) {
            Orientation.TO_TOP -> paddingTop
            Orientation.TO_LEFT -> paddingLeft
            Orientation.TO_BOTTOM -> paddingBottom
            Orientation.TO_RIGHT -> paddingRight
            else -> 0
        }

    private val relativePaddingWidthStart: Int
        get() = when (orientation) {
            Orientation.TO_TOP -> paddingBottom
            Orientation.TO_LEFT -> paddingRight
            Orientation.TO_BOTTOM -> paddingTop
            Orientation.TO_RIGHT -> paddingLeft
            else -> 0
        }

    private val relativePaddingWidthEnd: Int
        get() = when (orientation) {
            Orientation.TO_TOP -> paddingBottom
            Orientation.TO_LEFT -> paddingRight
            Orientation.TO_BOTTOM -> paddingTop
            Orientation.TO_RIGHT -> paddingLeft
            else -> 0
        }

    private val relativeLength: Int
        get() = when (orientation) {
            Orientation.TO_TOP, Orientation.TO_BOTTOM -> measuredHeight
            Orientation.TO_LEFT, Orientation.TO_RIGHT -> measuredWidth
            else -> 0
        }

    private val relativeWidth: Int
        get() = when (orientation) {
            Orientation.TO_TOP, Orientation.TO_BOTTOM -> measuredWidth
            Orientation.TO_LEFT, Orientation.TO_RIGHT -> measuredHeight
            else -> 0
        }

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MultiProgressBar)
        lineColor = typedArray.getColor(R.styleable.MultiProgressBar_progressLineColor, Color.GRAY)
        progressColor = typedArray.getColor(R.styleable.MultiProgressBar_progressColor, Color.WHITE)
        progressPadding = typedArray.getDimension(R.styleable.MultiProgressBar_progressPadding, MIN_PADDING.toPx)
        countOfProgressSteps = typedArray.getInt(R.styleable.MultiProgressBar_progressSteps, 1)
        progressWidth = typedArray.getDimension(R.styleable.MultiProgressBar_progressWidth, DEFAULT_WIDTH.toPx)
        progressPercents = typedArray.getInt(R.styleable.MultiProgressBar_progressPercents, 100)
        isNeedRestoreProgressAfterRecreate = typedArray.getBoolean(R.styleable.MultiProgressBar_progressIsNeedRestoreProgress, true)
        singleDisplayedTime = typedArray.getFloat(R.styleable.MultiProgressBar_progressSingleDisplayedTime, 1F).coerceAtLeast(0.1F)
        orientation = typedArray.getInt(R.styleable.MultiProgressBar_progressOrientation, Orientation.TO_RIGHT)
        typedArray.recycle()

        if (isInEditMode) {
            currentAbsoluteProgress = countOfProgressSteps / 2F * progressPercents
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val progressAdditionalWidth = if (orientation == Orientation.TO_BOTTOM || orientation == Orientation.TO_TOP) {
            progressWidth.toInt() + 5
        } else {
            0
        }
        val progressAdditionalHeight = if (orientation == Orientation.TO_RIGHT || orientation == Orientation.TO_LEFT) {
            progressWidth.toInt() + 5
        } else {
            0
        }
        val minWidth = paddingLeft + paddingRight + suggestedMinimumWidth + progressAdditionalWidth
        val minHeight = paddingBottom + paddingTop + suggestedMinimumHeight + progressAdditionalHeight
        setMeasuredDimension(
            resolveSize(minWidth, widthMeasureSpec),
            resolveSize(minHeight, heightMeasureSpec)
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (isProgressIsRunning) {
            pause()
        }
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
            isCompactMode = this@MultiProgressBar.isCompactMode
            orientation = this@MultiProgressBar.orientation
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
        isCompactMode = state.isCompactMode
        orientation = state.orientation

        if (isProgressIsRunning && isNeedRestoreProgressAfterRecreate) {
            internalStartProgress()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        internalSetProgressStepsCount(countOfProgressSteps)
    }

    override fun onDraw(canvas: Canvas) {
        for (step in 0 until countOfProgressSteps) {
            val previousPaddingSum = progressPadding + progressPadding * step
            val startTrack = if (orientation == Orientation.TO_RIGHT || orientation == Orientation.TO_BOTTOM) {
                relativePaddingStart + previousPaddingSum + singleProgressWidth * step
            } else {
                relativeLength - relativePaddingEnd - previousPaddingSum - singleProgressWidth * step
            }
            val endTrack = if (orientation == Orientation.TO_RIGHT || orientation == Orientation.TO_BOTTOM) {
                if (step == countOfProgressSteps - 1) {
                    relativeLength - progressPadding - relativePaddingEnd
                } else {
                    startTrack + singleProgressWidth
                }
            } else {
                if (step == countOfProgressSteps - 1) {
                    progressPadding + relativePaddingStart
                } else {
                    startTrack - singleProgressWidth
                }
            }

            if (step > currentAbsoluteProgress / progressPercents - 1) {
                paint.changePaintModeToBackground(isCompactMode)
            } else {
                paint.changePaintModeToProgress(isCompactMode)
            }

            if (orientation == Orientation.TO_LEFT || orientation == Orientation.TO_RIGHT) {
                canvas.drawLine(
                    startTrack,
                    (relativeWidth - relativePaddingWidthStart - relativePaddingWidthEnd) / 2F + relativePaddingWidthStart,
                    endTrack,
                    (relativeWidth - relativePaddingWidthStart - relativePaddingWidthEnd) / 2F + relativePaddingWidthStart,
                    paint
                )
            } else {
                canvas.drawLine(
                    (relativeWidth - relativePaddingWidthStart - relativePaddingWidthEnd) / 2F + relativePaddingWidthStart,
                    startTrack,
                    (relativeWidth - relativePaddingWidthStart - relativePaddingWidthEnd) / 2F + relativePaddingWidthStart,
                    endTrack,
                    paint
                )
            }

            val progressMultiplier = currentAbsoluteProgress / progressPercents - step
            if (progressMultiplier < 1F && progressMultiplier > 0F) {
                val progressEndX = if (orientation == Orientation.TO_RIGHT || orientation == Orientation.TO_BOTTOM) {
                    startTrack + singleProgressWidth * progressMultiplier
                } else {
                    startTrack - singleProgressWidth * progressMultiplier
                }
                paint.changePaintModeToProgress(isCompactMode)
                if (orientation == Orientation.TO_LEFT || orientation == Orientation.TO_RIGHT) {
                    canvas.drawLine(
                        startTrack,
                        (relativeWidth - relativePaddingWidthStart - relativePaddingWidthEnd) / 2F + relativePaddingWidthStart,
                        progressEndX,
                        (relativeWidth - relativePaddingWidthStart - relativePaddingWidthEnd) / 2F + relativePaddingWidthStart,
                        paint
                    )
                } else {
                    canvas.drawLine(
                        (relativeWidth - relativePaddingWidthStart - relativePaddingWidthEnd) / 2F + relativePaddingWidthStart,
                        startTrack,
                        (relativeWidth - relativePaddingWidthStart - relativePaddingWidthEnd) / 2F + relativePaddingWidthStart,
                        progressEndX,
                        paint
                    )
                }
            }
        }
    }

    fun setListener(stepChangeListener: ProgressStepChangeListener?) {
        this.stepChangeListener = stepChangeListener
    }

    fun setFinishListener(finishListener: ProgressFinishListener?) {
        this.finishListener = finishListener
    }

    fun setProgressStepsCount(progressSteps: Int) {
        internalSetProgressStepsCount(progressSteps)
    }

    fun getProgressStepsCount(): Int = countOfProgressSteps

    @JvmOverloads
    fun start(fromPosition: Int? = null) {
        if (isProgressIsRunning) return
        pause()
        internalStartProgress(fromPosition)
    }

    fun pause() {
        activeAnimator?.cancel()
        isProgressIsRunning = false
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

    /**
     * Set the percent for each cell progress
     * This parameter affects the smoothness of the animation of filling the progress bar
     * @param progressPercents - progress in decimal value
     */
    fun setProgressPercents(@IntRange(from = 1) progressPercents: Int) {
        this.progressPercents = progressPercents
    }

    fun getProgressPercents(): Int {
        return this.progressPercents
    }

    /**
     * Set the single item displayed time
     * @param singleDisplayedTime - time in seconds
     */
    fun setSingleDisplayTime(@FloatRange(from = 0.1) singleDisplayedTime: Float) {
        this.singleDisplayedTime = singleDisplayedTime.coerceAtLeast(0.1F)
        if (isProgressIsRunning) {
            Handler(Looper.getMainLooper()).post {
                pause()
                internalStartProgress()
            }
        }
    }

    fun getSingleDisplayTime(): Float {
        return singleDisplayedTime
    }

    private fun internalStartProgress(fromPosition: Int? = null) {
        fromPosition?.let { startPosition ->
            currentAbsoluteProgress = startPosition.toFloat().coerceIn(0F, countOfProgressSteps.toFloat()) * progressPercents
            animatedAbsoluteProgress = currentAbsoluteProgress
        }
        val maxValue = countOfProgressSteps * progressPercents.toFloat()
        val duration = (singleDisplayedTime * 1000 * countOfProgressSteps * (1 - (animatedAbsoluteProgress / maxValue))).toLong()
        activeAnimator = TimerHandler(animatedAbsoluteProgress, maxValue, duration).apply {
            listener = AnimationChangeListener { value ->
                isProgressIsRunning = value != maxValue

                val isStepChange = if ((value / progressPercents).toInt() != displayedStepForListener && value != maxValue) {
                    displayedStepForListener = (value / progressPercents).toInt()
                    currentAbsoluteProgress = displayedStepForListener * progressPercents.toFloat()
                    animatedAbsoluteProgress = displayedStepForListener * progressPercents.toFloat()
                    stepChangeListener?.onProgressStepChange(displayedStepForListener)
                    true
                } else if (value == maxValue) {
                    currentAbsoluteProgress = maxValue
                    animatedAbsoluteProgress = maxValue
                    finishListener?.onProgressFinished()
                    true
                } else false

                if (value != maxValue) {
                    if (!isStepChange) {
                        currentAbsoluteProgress =
                            value.coerceAtMost(countOfProgressSteps * progressPercents.toFloat())
                    }
                    invalidate()
                    if (!isStepChange) {
                        animatedAbsoluteProgress = value
                    }
                } else {
                    activeAnimator?.cancel()
                    animatedAbsoluteProgress = 0F
                    displayedStepForListener = -1
                }
            }
        }

        activeAnimator?.start()
    }

    private fun internalSetProgressStepsCount(count: Int) {
        countOfProgressSteps = count
        singleProgressWidth = (relativeLength - progressPadding * countOfProgressSteps - progressPadding - relativePaddingStart - relativePaddingEnd) / countOfProgressSteps
        if (relativeLength != 0 && singleProgressWidth < 0) {
            val compactModeSingleProgressWidth = (relativeLength - relativePaddingStart - relativePaddingEnd) / countOfProgressSteps
            if (compactModeSingleProgressWidth > 0) {
                progressPadding = 0F
                singleProgressWidth = compactModeSingleProgressWidth.toFloat()
                isCompactMode = true
            } else {
                isCompactMode = false
            }
        }
    }

    private fun Paint.changePaintModeToProgress(isCompactMode: Boolean) {
        reset()
        strokeCap = if (isCompactMode) Paint.Cap.BUTT else Paint.Cap.ROUND
        strokeWidth = progressWidth
        style = Paint.Style.FILL
        isDither = true
        isAntiAlias = true
        color = progressColor
    }

    private fun Paint.changePaintModeToBackground(isCompactMode: Boolean) {
        reset()
        strokeCap = if (isCompactMode) Paint.Cap.BUTT else Paint.Cap.ROUND
        strokeWidth = progressWidth
        style = Paint.Style.FILL
        isDither = true
        isAntiAlias = true
        color = lineColor
    }

    private val Float.toPx: Float
        get() = this * context.resources.displayMetrics.density

    interface ProgressStepChangeListener {
        fun onProgressStepChange(newStep: Int)
    }

    interface ProgressFinishListener {
        fun onProgressFinished()
    }

    private companion object {
        private const val MIN_PADDING = 8F
        private const val DEFAULT_WIDTH = 4F
    }

    @IntDef(
        Orientation.TO_TOP,
        Orientation.TO_RIGHT,
        Orientation.TO_BOTTOM,
        Orientation.TO_LEFT
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class Orientation {
        companion object {
            const val TO_TOP = 0
            const val TO_RIGHT = 1
            const val TO_BOTTOM = 2
            const val TO_LEFT = 3
            internal val ALL = listOf(
                TO_TOP,
                TO_RIGHT,
                TO_BOTTOM,
                TO_LEFT
            )
        }
    }
}