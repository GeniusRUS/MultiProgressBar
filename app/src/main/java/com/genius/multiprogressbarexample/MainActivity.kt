package com.genius.multiprogressbarexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.genius.multiprogressbar.MultiProgressBar

class MainActivity : AppCompatActivity(R.layout.activity_main), MultiProgressBar.ProgressStepChangeListener,
    View.OnClickListener, MultiProgressBar.ProgressFinishListener {

    private val progressBar: MultiProgressBar by lazy { findViewById(R.id.mpb_main) }
    private val startFrom: EditText by lazy { findViewById(R.id.et_start_from) }

    private var isDiscreteMode = false
    private var lastStep = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressBar.setListener(this)
        progressBar.setFinishListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.b_start -> {
                val startFromText = startFrom.text?.toString()
                val startFrom = startFromText?.toIntOrNull()
                progressBar.start(fromPosition = startFrom)
            }
            R.id.b_pause -> progressBar.pause()
            R.id.b_next -> progressBar.next()
            R.id.b_previous -> progressBar.previous()
            R.id.b_plus_second -> progressBar.setSingleDisplayTime(progressBar.getSingleDisplayTime() + 1F)
            R.id.b_minus_second -> progressBar.setSingleDisplayTime(progressBar.getSingleDisplayTime() - 1F)
            R.id.b_clear -> {
                progressBar.clear()
                this.isDiscreteMode = false
                this.lastStep = -1
            }
            R.id.b_discrete -> {
                this.isDiscreteMode = true
                progressBar.start()
            }
        }
    }

    override fun onProgressStepChange(newStep: Int) {
        Log.d("STEP", "Current step is $newStep")
        if (isDiscreteMode) {
            if (lastStep != newStep && newStep != progressBar.getProgressStepsCount()) {
                this.lastStep = newStep
                progressBar.pause()
                progressBar.postDelayed({
                    progressBar.start()
                }, 1000)
            }
        }
    }

    override fun onProgressFinished() {
        Log.d("PROGRESS", "Progress finished")
        this.isDiscreteMode = false
    }
}
