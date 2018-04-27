package com.board.out.`in`.inoutboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class WifiStatusReceiver : BroadcastReceiver() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var connected = false

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        if (!::fusedLocationClient.isInitialized) fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val action = intent?.action //Check if intent is right intent...

        AsyncTask.execute {
            val wifiManager = context.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            Log.d("wifi network", wifiManager.connectionInfo.ssid)
            val ssid = wifiManager.connectionInfo.ssid.replace("\"", "")

            if (ssid == "eduroam") {
                connected = true
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    sendIntent(context, ViewData("Lat ${it.latitude} long ${it.longitude}", ssid, true))
                }
            } else {
                connected = false
                val viewData = ViewData("Outside of school", "Unknown", false)
                sendIntent(context, viewData)
            }
        }
    }


    companion object {
        fun sendIntent(context: Context, viewData: ViewData) {
            val statusIntent = Intent("status")
            statusIntent.putExtra("viewdata", viewData)
            context.sendBroadcast(statusIntent)
        }
    }

}



