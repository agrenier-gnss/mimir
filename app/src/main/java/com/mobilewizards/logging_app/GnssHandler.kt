package com.mobilewizards.logging_app

import android.content.ContentValues
import android.content.Context
import android.location.GnssMeasurementsEvent
import android.location.GnssMeasurementsEvent.Callback
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast


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

    private var gnssMeasurementsList = mutableListOf<String>()
    private fun logGNSS(locationManager: LocationManager) {
        gnssMeasurementsEventListener = object : Callback() {
            override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
                val measurementsList = mutableListOf<String>()

                for (measurement in event.measurements) {
                    val svid = measurement.svid
                    val tosNanos = measurement.timeOffsetNanos
                    val state = measurement.state
                    val cn0DbHz = measurement.cn0DbHz
                    val carrierF = measurement.carrierFrequencyHz
                    val pseudorangeRMPS = measurement.pseudorangeRateMetersPerSecond
                    val pseudoraneRUMPS = measurement.pseudorangeRateUncertaintyMetersPerSecond

                    val measurementString =
                        "$svid," +
                        "$tosNanos," +
                        "$state," +
                        "$cn0DbHz," +
                        "$carrierF," +
                        "$pseudorangeRMPS," +
                        "$pseudoraneRUMPS"

                    measurementsList.add(measurementString)
                    Log.d("GNSS Measurement", measurementString)
                }

                gnssMeasurementsList.addAll(measurementsList)
            }

            override fun onStatusChanged(status: Int) {}
        }

        try {
            locationManager.registerGnssMeasurementsCallback(gnssMeasurementsEventListener)
        } catch (e: SecurityException) {
            Log.e("Error", "No permission for location fetching")
        }

    }

    fun stopLogging(context: Context) {
        locationManager.removeUpdates(gpsLocationListener)
        locationManager.removeUpdates(networkLocationListener)
        locationManager.unregisterGnssMeasurementsCallback(gnssMeasurementsEventListener)

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "gnss_measurements.csv")
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        Log.d("uri", uri.toString())
        uri?.let { mediaUri ->
            context.contentResolver.openOutputStream(mediaUri)?.use { outputStream ->
                outputStream.write("SvId,Time offset in nanos,State,cn0DbHz,carrierFrequencyHz,pseudorangeRateMeterPerSecond,pseudorangeRateUncertaintyMeterPerSecond\n".toByteArray())
                gnssMeasurementsList.forEach { measurementString ->
                    outputStream.write("$measurementString\n".toByteArray())
                }
                outputStream.flush()
            }

            Toast.makeText(context, "GNSS Measurements saved to Downloads folder", Toast.LENGTH_SHORT).show()
        }
    }
}