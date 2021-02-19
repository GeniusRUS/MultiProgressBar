package com.genius.multiprogressbarexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.genius.multiprogressbar.MultiProgressBar

class MainActivity : AppCompatActivity(R.layout.activity_main), MultiProgressBar.ProgressStepChangeListener,
    View.OnClickListener, MultiProgressBar.ProgressFinishListener {

    private val progressBar: MultiProgressBar by lazy { findViewById(R.id.mpb_main) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressBar.setListener(this)
        progressBar.setFinishListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.b_start -> progressBar.start()
            R.id.b_pause -> progressBar.pause()
            R.id.b_next -> progressBar.next()
            R.id.b_previous -> progressBar.previous()
            R.id.b_plus_second -> progressBar.setSingleDisplayTime(progressBar.getSingleDisplayTime() + 1F)
            R.id.b_minus_second -> progressBar.setSingleDisplayTime(progressBar.getSingleDisplayTime() - 1F)
            R.id.b_clear -> progressBar.clear()
        }
    }

    override fun onProgressStepChange(newStep: Int) {
        Log.d("STEP", "Current step is $newStep")
    }

    override fun onProgressFinished() {
        Log.d("PROGRESS", "Progress finished")
    }
}
