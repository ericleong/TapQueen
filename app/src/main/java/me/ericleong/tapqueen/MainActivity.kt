package me.ericleong.tapqueen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var started = false
    var count = 0
    var high = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val highScore = findViewById(R.id.highScore) as TextView
        highScore.text = resources.getString(R.string.high_score, 0)

        val button = findViewById(R.id.button) as Button
        val chronometer = findViewById(R.id.chronometer) as Chronometer

        val display =
                (getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay();
        val frameDuration = (TimeUnit.SECONDS.toMillis(1) / display.refreshRate).toLong()

        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                if (!started) {
                    started = true
                    chronometer.base =
                            SystemClock.elapsedRealtime() + TimeUnit.SECONDS.toMillis(3) + frameDuration
                    chronometer.start()
                } else {
                    count++
                }

                button.text = count.toString()

                return true
            }
        }
        val gestureDetector = GestureDetector(this, gestureListener)

        button.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        chronometer.setOnChronometerTickListener { c ->
            if (SystemClock.elapsedRealtime() >= c.base) {
                started = false
                c.stop()
                button.setText(R.string.start)
                Toast.makeText(this, count.toString(), Toast.LENGTH_SHORT).show()
                if (count > high) {
                    high = count

                    highScore.text = resources.getString(R.string.high_score, count)
                }
                count = 0
            }
        }
    }
}
