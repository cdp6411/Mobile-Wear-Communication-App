package com.wear.mobileapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataApi
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import java.util.Random

class NewActivity : AppCompatActivity(),GoogleApiClient.ConnectionCallbacks, DataApi.DataListener {

    private var googleApiClient: GoogleApiClient? = null
    var timer: CountDownTimer? = null
    var isTimerRunning = false
    var timeRemaining: Long = 15000
    var timeElapsed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addApi(Wearable.API)
            .build()

        googleApiClient?.connect()

        findViewById<AppCompatTextView>(R.id.textTitleActivity).setOnClickListener {
            val path = "/your/data/path/Wear"
            val sendDataMapRequest = PutDataMapRequest.create(path)
            sendDataMapRequest.dataMap.putString("data_key", "Event From WearOS -${Random().nextInt(50)}")
            Wearable.DataApi.putDataItem(googleApiClient!!, sendDataMapRequest.asPutDataRequest())
//            findViewById<ConstraintLayout>(R.id.secondLayout).visibility = View.VISIBLE
//            findViewById<ConstraintLayout>(R.id.firstLayout).visibility = View.GONE
//            startTimer()
        }

        findViewById<AppCompatButton>(R.id.btnPauseResume).setOnClickListener {
            pauseResumeTimer()
        }

        findViewById<AppCompatButton>(R.id.btnNext).setOnClickListener {
            findViewById<ConstraintLayout>(R.id.firstLayout).visibility = View.VISIBLE
            findViewById<ConstraintLayout>(R.id.thirdLayout).visibility = View.GONE
        }
    }

    override fun onConnected(bundle: Bundle?) {
        googleApiClient?.let { Wearable.DataApi.addListener(it, this) }
    }

    override fun onConnectionSuspended(i: Int) {
        // Connection to the Google API client is suspended on the watch.
    }

    private fun startTimer() {
        if (timer == null) {
            timer = object : CountDownTimer(timeRemaining - timeElapsed, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeElapsed = timeRemaining - millisUntilFinished
                    updateTimerDisplay(millisUntilFinished)
                }

                override fun onFinish() {
                    timer = null
                    isTimerRunning = false
                    timeElapsed = 0
                    findViewById<ConstraintLayout>(R.id.thirdLayout).visibility = View.VISIBLE
                    findViewById<ConstraintLayout>(R.id.secondLayout).visibility = View.GONE
                }
            }
            timer?.start()
            isTimerRunning = true
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun pauseResumeTimer() {
        updateButtonState()
        if (isTimerRunning) {
            timer?.cancel()
            timer = null
            isTimerRunning = false
        } else {
            startTimer()
        }
    }

    private fun updateButtonState() {
        if(isTimerRunning){
            findViewById<AppCompatButton>(R.id.btnPauseResume).text = "Resume"
            findViewById<AppCompatButton>(R.id.btnPauseResume).setTextColor(ContextCompat.getColor(this,
                R.color.colorWhite
            ))
            findViewById<AppCompatButton>(R.id.btnPauseResume).setBackgroundResource(R.drawable.rounded_background_orange)
        }else{
            findViewById<AppCompatButton>(R.id.btnPauseResume).text = "Pause"
            findViewById<AppCompatButton>(R.id.btnPauseResume).setTextColor(ContextCompat.getColor(this,
                R.color.colorBlack
            ))
            findViewById<AppCompatButton>(R.id.btnPauseResume).setBackgroundResource(R.drawable.rounded_background_light)
        }
    }

    fun updateTimerDisplay(millisUntilFinished: Long) {
        val minutes = (millisUntilFinished / 1000) / 60
        val seconds = (millisUntilFinished / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        findViewById<AppCompatTextView>(R.id.timer).text = timeFormatted
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        for (event in dataEventBuffer) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == "/your/data/path/Hello") {
                    val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                    val receivedData = dataMapItem.dataMap.getString("data_key")
                    println("------>$receivedData")

                    // Handle the received data
                    // For example, update UI with receivedData
                }
            }
        }
    }
}