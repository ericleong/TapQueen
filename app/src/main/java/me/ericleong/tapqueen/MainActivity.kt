package me.ericleong.tapqueen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var started = false
    var count = 0
    var highScores = mutableMapOf(Pair(3L, 0), Pair(10L, 0), Pair(30L, 0))
    var lastScores = mutableMapOf(Pair(3L, 0), Pair(10L, 0), Pair(30L, 0))
    var duration = TimeUnit.SECONDS.toMillis(3)
    var maxDurations = listOf(3L, 10L, 30L);

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val highScore = findViewById<TextView>(R.id.highScore)
        highScore.text = resources.getString(R.string.high_score, 0)

        val lastScore = findViewById<TextView>(R.id.lastScore)
        lastScore.text = resources.getString(R.string.last_score, 0)

        val button = findViewById<Button>(R.id.button)
        val chronometer = findViewById<Chronometer>(R.id.chronometer)

        val display =
                (getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay()
        val frameDuration = (TimeUnit.SECONDS.toMillis(1) / display.refreshRate).toLong()

        val durations = findViewById<Spinner>(R.id.durations)
        val adapter = ArrayAdapter.createFromResource(this, R.array.durations,
                R.layout.item_duration)
        durations.adapter = adapter
        durations.setSelection(0)
        durations.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                duration = 3
                highScore.text = resources.getString(R.string.high_score, highScores[duration])
                lastScore.text = resources.getString(R.string.last_score, lastScores[duration])
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                duration = maxDurations.get(position);
                highScore.text = resources.getString(R.string.high_score, highScores[duration])
                lastScore.text = resources.getString(R.string.last_score, lastScores[duration])
            }
        }

        val gestureListener = object : GestureDetector.OnGestureListener {
            override fun onShowPress(e: MotionEvent?) {
                button.background.setState(intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled))
            }

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                return false
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                return false
            }

            override fun onLongPress(e: MotionEvent?) {

            }

            override fun onDown(e: MotionEvent?): Boolean {
                if (!started) {
                    started = true
                    chronometer.base =
                            SystemClock.elapsedRealtime() + TimeUnit.SECONDS.toMillis(duration)
                    chronometer.start()
                    durations.isEnabled = false
                } else {
                    count++
                }

                button.text = count.toString()

                return false
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                button.background.setState(intArrayOf())

                return false
            }
        }
        val gestureDetector = GestureDetector(this, gestureListener)

        button.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        chronometer.setOnChronometerTickListener { c ->
            if (SystemClock.elapsedRealtime() >= c.base) {
                started = false
                c.stop()

                lastScores[duration] = count
                lastScore.text = resources.getString(R.string.last_score, count)

                if (count > highScores[duration] ?: 0) {
                    highScores[duration] = count

                    highScore.text = resources.getString(R.string.high_score, count)
                }

                button.isEnabled = false
                button.postDelayed({
                    button.setText(R.string.start)
                    button.isEnabled = true
                    durations.isEnabled = true
                    count = 0
                }, TimeUnit.SECONDS.toMillis(1))
            }
        }
    }
}
