package com.board.out.`in`.inoutboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.board.out.`in`.inoutboard.WifiStatusReceiver.Companion.sendIntent
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val RECORD_REQUEST_CODE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!checkPermissions()) requestPermissions()
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        checkInitial(wifiManager)
    }

    @SuppressLint("MissingPermission")
    private fun checkInitial(wifiManager: WifiManager) {
        val ssid = wifiManager.connectionInfo.ssid.replace("\"", "")
        if (ssid == "eduroam") {
            LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
                sendIntent(this, ViewData("Lat ${it.latitude} long ${it.longitude}", ssid, true))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, IntentFilter("status"));
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val viewData = intent.extras.get("viewdata") as ViewData
            tv_location.text = viewData.location
            tv_network.text = viewData.network
            background.setBackgroundColor(if (viewData.connected) Color.GREEN else Color.RED)
        }
    }

    private fun checkPermissions() = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION),
                RECORD_REQUEST_CODE)
    }
}