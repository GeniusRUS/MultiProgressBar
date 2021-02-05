package com.genius.multiprogressbarexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.genius.multiprogressbar.MultiProgressBar

class MainActivity : AppCompatActivity(), MultiProgressBar.ProgressStepChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressBar = findViewById<MultiProgressBar>(R.id.mpb_main)
        val buttonStart = findViewById<Button>(R.id.b_start)
        val buttonPause = findViewById<Button>(R.id.b_pause)
        val buttonNext = findViewById<Button>(R.id.b_next)
        val buttonPrevious = findViewById<Button>(R.id.b_previous)
        val buttonClear = findViewById<Button>(R.id.b_clear)

        progressBar.setListener(this)

        buttonStart.setOnClickListener {
            progressBar.start()
        }

        buttonPause.setOnClickListener {
            progressBar.pause()
        }

        buttonNext.setOnClickListener {
            progressBar.next()
        }

        buttonPrevious.setOnClickListener {
            progressBar.previous()
        }

        buttonClear.setOnClickListener {
            progressBar.clear()
        }
    }

    override fun onProgressStepChange(newStep: Int) {
        Log.d("STEP", "Current step is $newStep")
    }
}
