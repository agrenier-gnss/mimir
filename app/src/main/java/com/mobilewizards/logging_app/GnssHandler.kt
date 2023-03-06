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



private lateinit var locationManager: LocationManager
private lateinit var gpsLocationListener: LocationListener
private lateinit var networkLocationListener: LocationListener
private lateinit var gnssMeasurementsEventListener: Callback

class GnssHandler{

    protected var context: Context


    constructor(context: Context) : super() {
        this.context = context.applicationContext
    }



    public fun setUpLogging(){
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        logLocation(locationManager)
        logGNSS(locationManager)
    }

    private fun logLocation(locationManager: LocationManager) {



        //Locationlistener for Gps
        gpsLocationListener = object : LocationListener{

            override fun onLocationChanged(location: Location){

                val longitudeGps = location.longitude
                val latitudeGps = location.latitude
                val altitudeGps = location.altitude

                Log.d("Longitude from Gps", longitudeGps.toString())
                Log.d("Latitude from Gps", latitudeGps.toString())
                Log.d("Altitude from Gps", altitudeGps.toString())
            }

            override fun onFlushComplete(requestCode: Int) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}

        }

        networkLocationListener = object : LocationListener{
            override fun onLocationChanged(location: Location){

                val longitudeNetwork = location.longitude
                val latitudeNetwork = location.latitude
                val altitudeNetwork = location.altitude

                Log.d("Longitude from network", longitudeNetwork.toString())
                Log.d("Latitude from network", latitudeNetwork.toString())
                Log.d("Altitude from network", altitudeNetwork.toString())
            }
            override fun onFlushComplete(requestCode: Int) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}
        }

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000, 0F, gpsLocationListener
                )

            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000, 0F, networkLocationListener
                )

            }

        }
        catch(e: SecurityException){
            Log.d("Error", "No permission for location fetching")
        }


    }





    private fun logGNSS(locationManager: LocationManager) {

        gnssMeasurementsEventListener = object : Callback(){

            override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
                for (measurement in event.measurements) {

                    val svid = measurement.svid
                    val tosNanos = measurement.timeOffsetNanos
                    val state = measurement.state
                    val cn0DbHz = measurement.cn0DbHz
                    val carrierF = measurement.carrierFrequencyHz
                    val pseudorangeRMPS = measurement.pseudorangeRateMetersPerSecond
                    val pseudoraneRUMPS = measurement.pseudorangeRateUncertaintyMetersPerSecond

                    Log.d("SvId", svid.toString())
                    Log.d("Time offset in nanos", tosNanos.toString())

                    Log.d("State", state.toString())

                    Log.d("cn0DbHz", cn0DbHz.toString())

                    Log.d("carrierFrequencyHz" , carrierF.toString())

                    Log.d("pseudorangeRateMeterPerSecond", pseudorangeRMPS.toString())

                    Log.d("pseudorangeRateUncertaintyMeterPerSecond", pseudoraneRUMPS.toString())

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


    fun stopLogging(){
        locationManager.removeUpdates(gpsLocationListener)
        locationManager.removeUpdates(networkLocationListener)
        locationManager.unregisterGnssMeasurementsCallback(gnssMeasurementsEventListener)
    }


}