package com.mobilewizards.logging_app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.GnssMeasurement
import android.location.GnssMeasurementsEvent
import android.location.GnssMeasurementsEvent.Callback
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat


class GnssHandler{

    protected var context: Context




    constructor(context: Context) : super() {
        this.context = context.applicationContext
    }

    private fun setUpLogging(){
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        logLocation(locationManager)
        logGNSS(locationManager)
    }

    private fun logLocation(locationManager: LocationManager) {



        //Locationlistener for Gps
        val gpsLocationListener = object : LocationListener{

            override fun onLocationChanged(location: Location){
                Log.d("Longitude from Gps", location.longitude.toString())
                Log.d("Latitude from Gps", location.latitude.toString())
                Log.d("Altitude from Gps", location.altitude.toString())
            }

            override fun onFlushComplete(requestCode: Int) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}

        }

        val networkLocationListener = object : LocationListener{
            override fun onLocationChanged(location: Location){
                Log.d("Longitude from network", location.longitude.toString())
                Log.d("Latitude from network", location.latitude.toString())
                Log.d("Altitude from network", location.altitude.toString())
            }
            override fun onFlushComplete(requestCode: Int) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}
        }
        try{
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000, 0F, gpsLocationListener
                )

            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000, 0F, networkLocationListener
                )

            }
        }
        catch (e: SecurityException){
            Log.d("Error", "No permission for location fetching")
        }



    }





    private fun logGNSS(locationManager: LocationManager) {

        val gnssMeasurementsEventListener = object : Callback(){

            override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
                for (measurement in event.measurements) {
                    Log.d("SvId", measurement.svid.toString())
                    Log.d("Time offset in nanos", measurement.timeOffsetNanos.toString())

                    Log.d("State", measurement.state.toString())

                    Log.d("cn0DbHz", measurement.cn0DbHz.toString())

                    Log.d("carrierFrequencyHz" ,measurement.carrierFrequencyHz.toString())

                    Log.d("pseudorangeRateMeterPerSecond", measurement.pseudorangeRateMetersPerSecond.toString())

                    Log.d("pseudorangeRateUncertaintyMeterPerSecond",measurement.pseudorangeRateUncertaintyMetersPerSecond.toString())
                }
            }

            override fun onStatusChanged(status: Int) {
            }
        }
        try{
            locationManager.registerGnssMeasurementsCallback(gnssMeasurementsEventListener)
        }
        catch (e: SecurityException){
            Log.d("Error", "No permission for location fetching")
        }

    }
}