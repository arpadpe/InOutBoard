package com.board.out.`in`.inoutboard

import kotlin.jvm.javaClass
import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var mLocationManager : LocationManager? = null
    private var mConnectivityManager : ConnectivityManager? = null
    private var mTVLocation : TextView? = null
    private var mTVNetwork : TextView? = null
    private val RECORD_REQUEST_CODE = 101
    private var mWifiManager : WifiManager? = null

    @SuppressLint("MissingPermission", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTVLocation = findViewById(R.id.tv_location)

        mTVNetwork = findViewById(R.id.tv_network)

        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        mWifiManager = getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager?

        if (!checkPermissions()) requestPermissions()
        //mLocationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, mLocationListener)
        /*
        var networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_WIFI_P2P)
                .addCapability(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                .build()
        mConnectivityManager?.requestNetwork(networkRequest, mNetworkCallback)
        mConnectivityManager?.registerDefaultNetworkCallback(mNetworkCallback)*/
        /*mWifiManager?.startScan()
        if (mWifiManager?.isWifiEnabled == true)
            mWifiManager?.connectionInfo*/
        //mTVNetwork?.setText("Network: ${mWifiManager?.connectionInfo}")

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                if (WifiManager.WIFI_STATE_ENABLED == intent?.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)) {
                    val location = mLocationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    mTVLocation?.setText("Lat: ${location?.latitude}, Lon: ${location?.longitude}")
                    //mTVNetwork?.setText("Network: ${mWifiManager?.connectionInfo?.macAddress}, ${mWifiManager?.connectionInfo?.ssid}")
                    context?.startService(Intent(context, WifiService::class.java))
                } else {

                }
            }
        }

    }

    private class WifiService: Service() {

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            val wifiManager: WifiManager? = getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager?

            Handler().postDelayed({
                kotlin.run {
                    val info = wifiManager?.connectionInfo
                    val mac = info?.macAddress
                    val ssid = info?.ssid
                    //mTVNetwork?.setText("Network: $mac, $ssid")
                }
            }, 5000)

            return super.onStartCommand(intent, flags, startId)
        }

        override fun onBind(Intent: Intent?): IBinder {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private val mLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            mTVLocation?.setText("Lat: ${location?.latitude}, Lon: ${location?.longitude}")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String?) {}

        override fun onProviderDisabled(provider: String?) {}
    }

    private val mNetworkCallback: ConnectivityManager.NetworkCallback = object: ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            super.onAvailable(network)
            mTVNetwork?.setText("Network: ${network.toString()}")
        }

        override fun onLost(network: Network?) {
            super.onLost(network)
        }

    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_REQUEST_CODE)
    }
}
