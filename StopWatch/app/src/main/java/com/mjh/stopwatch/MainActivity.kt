package com.mjh.stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private var time = 0
    private var timerTask : Timer? = null
    private var isRunning = false
    private var lap = 1
    private var lapArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState != null)
        {
            time = savedInstanceState.getInt("time")
            isRunning = savedInstanceState.getBoolean("isRunning")
            lap = savedInstanceState.getInt("lap")
            lapArrayList = savedInstanceState.getStringArrayList("lapArrayList") as ArrayList<String>

            val sec = time / 100
            val milli = time % 100
            runOnUiThread {
                secTextView.text = "$sec"
                milliTextView.text = "$milli"
            }
            for(i : Int in 0 until lap - 1)
            {
                val textView = TextView(this)
                textView.text = lapArrayList?.get(i)
                // 맨 위에 랩타임 추가
                lapLayout.addView(textView, 0)
            }
            if(isRunning)
                start()
            else
                pause()
        }
        fab.setOnClickListener {
            isRunning = !isRunning
            if(isRunning)
                start()
            else
                pause()
        }
        lapButton.setOnClickListener {
            recordLapTime()
        }
        resetFab.setOnClickListener {
            reset()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("time", time)
        outState.putBoolean("isRunning", isRunning)
        outState.putInt("lap",lap)
        outState.putStringArrayList("lapArrayList", lapArrayList)
    }
    private fun start()
    {
        fab.setImageResource(R.drawable.ic_pause_black_24dp) // fab 이미지를 일시정지 아이콘으로 변경
        timerTask = timer(period = 10)
        {
            time++
            val sec = time / 100
            val milli = time % 100

            runOnUiThread {
                secTextView.text = "$sec"
                milliTextView.text = "$milli"
            }
        }
    }
    private fun pause()
    {
        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        timerTask?.cancel()
    }
    private fun reset()
    {
        timerTask?.cancel()

        // 모든 변수 초기화
        time = 0
        isRunning = false
        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        runOnUiThread {
            secTextView.text = "0"
            milliTextView.text = "00"
        }
        // 모든 랩타임 제거
        lapLayout.removeAllViews()
        lap = 1
        lapArrayList.removeAll(lapArrayList)
    }
    private fun recordLapTime()
    {
        val lapTime = this.time
        val textView = TextView(this)
        textView.text = "$lap LAB : ${lapTime / 100}.${lapTime % 100}"
        lapArrayList.add(textView.text.toString())

        // 맨 위에 랩타임 추가
        lapLayout.addView(textView, 0)
        lap++
    }
}
