package com.genius.multiprogressbar

import android.os.Parcel
import android.os.Parcelable
import android.view.View

internal class MultiProgressBarSavedState : View.BaseSavedState {
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
    var isCompactMode: Boolean = false
    var orientation: Int = MultiProgressBar.Orientation.TO_RIGHT

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
        this.isCompactMode = `in`.readInt() == 1
        this.orientation = `in`.readInt()
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
        out.writeInt(if (this.isCompactMode) 1 else 0)
        out.writeInt(this.orientation)
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