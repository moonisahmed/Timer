package com.example.mooni.timerapp

import android.content.IntentSender
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.example.mooni.timerapp.R.menu.menu_timer
import com.example.mooni.timerapp.util.PrefUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_timer.*

class MainActivity : AppCompatActivity() {

    enum class TimerState{
        Paused, Running, Stopped
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
/*
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "     Timer"
*/
        fap_pause.setOnClickListener { v ->
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }
        fab_play.setOnClickListener { v ->
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }
        fap_stop.setOnClickListener { v->
            timer.cancel()
            onTimerFinished()
        }

    }

    private fun startTimer() {
        timerState = TimerState.Running
        timer = object : CountDownTimer(secondsRemaining *1000, 1000){
            override fun onFinish() = onTimerFinished()
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished/1000
                updateCountDownUI()
            }
        }.start()
    }



    private fun updateButtons() {
        when(timerState){
            TimerState.Running ->{
                fab_play.isEnabled = false
                fap_pause.isEnabled = true
                fap_stop.isEnabled = true

            }
            TimerState.Stopped ->{
                fab_play.isEnabled = true
                fap_pause.isEnabled = true
                fap_stop.isEnabled = false

            }
            TimerState.Paused ->{
                fab_play.isEnabled = true
                fap_pause.isEnabled = false
                fap_stop.isEnabled = true

            }
        }
    }

    override fun onResume() {
        super.onResume()
        initTimer()
    }

    override fun onPause() {
        super.onPause()

        if(timerState == TimerState.Running){
            timer.cancel()
            //TODO

        }

        else if(timerState == TimerState.Paused){
            //TODO
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds,this)
        PrefUtil.setSecondsRemaining(secondsRemaining,this)
        PrefUtil.setTimerState(timerState,this)
    }

    private fun initTimer(){
        timerState = PrefUtil.getTImerState(this)
        if(timerState == TimerState.Stopped){
            setNewTimerLength()
        }
        else
            setPreviousTimerLength()

        secondsRemaining = if(timerState== TimerState.Running || timerState == TimerState.Paused){PrefUtil.getSecondsRemaining(this)}
        else
            timerLengthSeconds


        if(timerState == TimerState.Running)
            startTimer()

        updateButtons()
        updateCountDownUI()
    }

    private fun updateCountDownUI() {
        val minutesUntilFinished = secondsRemaining/60
        val secondsInMinutesUntilFinished = secondsRemaining - minutesUntilFinished * 60

        val secondStr = secondsInMinutesUntilFinished.toString()
        textView_countdown.text ="$minutesUntilFinished:${
        if(secondStr.length==2) secondStr
        else "0" + secondStr}"

        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes*60L)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun onTimerFinished(){
        timerState = TimerState.Stopped
        setNewTimerLength()

        progress_countdown.progress = 0
        PrefUtil.setSecondsRemaining(timerLengthSeconds,this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountDownUI()
    }

}

