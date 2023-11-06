package com.wear.mobileapp

import android.annotation.SuppressLint
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataApi
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import java.util.Random

class MainActivity : AppCompatActivity(), DataApi.DataListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    private var googleApiClient: GoogleApiClient? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(Wearable.API)
            .build()
        googleApiClient?.connect()

        findViewById<TextView>(R.id.titleText).setOnClickListener {
            val path = "/your/data/path/Hello"
            val sendDataMapRequest = PutDataMapRequest.create(path)
            sendDataMapRequest.dataMap.putString(
                "data_key", "\"Event From MobileApp -${
                    Random().nextInt(
                        50
                    )
                }\""
            )
            Wearable.DataApi.putDataItem(googleApiClient!!, sendDataMapRequest.asPutDataRequest())
        }

    }

    override fun onResume() {
        super.onResume()
        googleApiClient?.connect()
    }

    override fun onPause() {
        super.onPause()
        googleApiClient?.disconnect()
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        for (event in dataEventBuffer) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == "/your/data/path/Wear") {
                    val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                    val receivedData = dataMapItem.dataMap.getString("data_key")
                    println("------>$receivedData")

                    // Handle the received data
                    // For example, update UI with receivedData
                }
            }
        }
    }

    override fun onConnected(p0: Bundle?) {
        googleApiClient?.let { Wearable.DataApi.addListener(it, this) }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }
}