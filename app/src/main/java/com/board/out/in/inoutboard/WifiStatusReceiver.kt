package com.board.out.`in`.inoutboard

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.location.LocationServices
import java.text.DecimalFormat

class WifiStatusReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val action = intent?.action //Check if intent is right intent...

        getPositionAndSendIntent(context)
    }


    companion object {
        @JvmStatic
        fun sendIntent(context: Context, viewData: ViewData) {
            val statusIntent = Intent("status")
            statusIntent.putExtra("viewdata", viewData)
            context.sendBroadcast(statusIntent)
        }

        @SuppressLint("MissingPermission")
        @JvmStatic
        fun getPositionAndSendIntent(context: Context) {
            AsyncTask.execute {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val wifiManager = context.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
                Log.d("wifiwifiwifiwifi", wifiManager.connectionInfo.ssid)
                val ssid = wifiManager.connectionInfo.ssid.replace("\"", "")
                if (ssid == "eduroam") {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        val dec = DecimalFormat("#.#")
                        sendIntent(context, ViewData("Lat ${dec.format(it.latitude)} long ${dec.format(it.longitude)}", ssid, true))
                    }.addOnFailureListener {
                        sendIntent(context, ViewData("Unknown", ssid, true))
                    }
                } else {
                    val viewData = ViewData("Outside of school", "Unknown", false)
                    sendIntent(context, viewData)
                }
            }
        }
    }

}



