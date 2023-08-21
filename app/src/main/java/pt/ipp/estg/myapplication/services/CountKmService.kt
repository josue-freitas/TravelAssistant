package pt.ipp.estg.myapplication.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.helper.Location
import javax.inject.Inject


class CountKmService : LifecycleService() {

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val distanceCounted = MutableLiveData<Double>()
        val previousLocation = MutableLiveData<android.location.Location>()
    }

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        distanceCounted.postValue(0.0)

        isTracking.observe(this) {
            updateLocationTracking(it)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                "Start" -> {
                    Log.d("SERVICE", "START")
                    isTracking.postValue(true)
                    notifyUser()
                }
                "Stop" -> {
                    isTracking.postValue(false)
                    Log.d("SERVICE", "DISTANCE CONTEND" + distanceCounted.value.toString())
                    Log.d("SERVICE", "STOP")
                    stopForeground(true)
                    stopSelf()
                }
                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun notifyUser() {
        val CHANNELID = "Count km service";

        val channel = NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNELID)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.map
                )
            )
            .setSmallIcon(R.drawable.compass_logo)
            .setContentTitle("Your travel is being recorded")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("We're using your location!")
            )

        startForeground(1001, notification.build())
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            val locationRequest: LocationRequest = LocationRequest.create().apply {
                interval = 5000
                fastestInterval = 2500
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.let { locations ->
                for (location in locations) {
                    previousLocation.postValue(location)
                    if (previousLocation.value != null) {
                        var distance = distanceCounted.value
                        distance = distance?.plus(
                            Location.calculateByDistance(
                                LatLng(
                                    previousLocation.value!!.latitude,
                                    previousLocation.value!!.longitude,
                                ), LatLng(location.latitude, location.longitude)
                            )
                        )
                        distanceCounted.postValue(distance)
                    }
                }
            }
        }
    }
}