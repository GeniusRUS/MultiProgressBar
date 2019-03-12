package com.genius.multiprogressbarexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.genius.multiprogressbar.MultiProgressBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MultiProgressBar.ProgressStepChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mpb_main.setListener(this)

        b_start.setOnClickListener {
            mpb_main.start()
        }

        b_pause.setOnClickListener {
            mpb_main.pause()
        }

        b_next.setOnClickListener {
            mpb_main.next()
        }

        b_previous.setOnClickListener {
            mpb_main.previous()
        }

        b_clear.setOnClickListener {
            mpb_main.clear()
        }
    }

    override fun onProgressStepChange(newStep: Int) {
        Log.d("STEP", "Current step is $newStep")
    }
}
