package com.mobilewizards.watchlogger

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import com.mobilewizards.logging_app.BuildConfig
import com.mobilewizards.logging_app.startTime
import java.io.File
import java.util.*

/*Class to handle GNSS data logging and writing it in CSV-files. Logging works the same way it does
on phone.*/
@Suppress("DEPRECATION")
class WatchGNSSHandler {
    //All of the needed listeners + LocationManager that registers location listeners.
    private lateinit var locationManager: LocationManager
    private lateinit var gpsLocationListener: LocationListener
    private lateinit var networkLocationListener: LocationListener
    private lateinit var gnssMeasurementsEventListener: GnssMeasurementsEvent.Callback
    private lateinit var gnssNavigationMessageListener: android.location.GnssNavigationMessage.Callback
    protected var context: Context
    private val TAG = "watchLogger"
    var isLogging = false

    //A list that will collect all logged data in CSV-format. When logging stops, this list will be written
    //in the file.
    private var gnssMeasurementsList = mutableListOf<String>()

    constructor(context: Context) : super() {
        this.context = context.applicationContext
    }

    /* Function that is called in ActivityHandler when logging is started.*/
    fun setUpLogging(){
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        logLocation(WatchActivityHandler.getFrequency().toLong())
        logGNSS(WatchActivityHandler.getFrequency().toLong())
        logGnssNavigationMessages(WatchActivityHandler.getFrequency().toLong())

        isLogging = true
    }

    /* Function that logs location by creating two listeners. One for gps provider and
    one for network provider, if they are enabled. */
    private fun logLocation(samplingFrequency: Long) {
        //Locationlistener for Gps
        gpsLocationListener = object : LocationListener{

            override fun onLocationChanged(location: Location){
                //Collects data in a string in a wanted format and then added in the data list.
                val locationStream: String = java.lang.String.format(
                    Locale.US,
                    "Fix,%s,%f,%f,%f,%f,%f,%d",
                    location.provider,
                    location.latitude,
                    location.longitude,
                    location.altitude,
                    location.speed,
                    location.accuracy,
                    location.time
                )
                gnssMeasurementsList.add(locationStream)
            }
            override fun onFlushComplete(requestCode: Int) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}
        }

        networkLocationListener = object : LocationListener{
            override fun onLocationChanged(location: Location){
                val locationStream: String = java.lang.String.format(
                    Locale.US,
                    "Fix,%s,%f,%f,%f,%f,%f,%d",
                    location.provider,
                    location.latitude,
                    location.longitude,
                    location.altitude,
                    location.speed,
                    location.accuracy,
                    location.time
                )
                gnssMeasurementsList.add(locationStream)
            }
            override fun onFlushComplete(requestCode: Int) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}
        }

        //Check if user has enabled the location fethcing. If yes, then check providers.
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    samplingFrequency, 0F, gpsLocationListener
                )
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    samplingFrequency, 0F, networkLocationListener
                )
            }
        }
        catch(e: SecurityException){
            Log.d("Error", "No permission for location fetching")
        }
    }

    /* Logging of GNSS measurements by creating a listener in this function. Parameter samplingFrequency
    * is given to make sure data isn't logged too fast. Contains deprecated methods.*/
    private fun logGNSS( samplingFrequency: Long) {
        gnssMeasurementsEventListener = object : android.location.GnssMeasurementsEvent.Callback(){
            var lastMeasurementTime = 0L
            override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {

                //Testing if enough time has passed since the last event.
                val currentTime = SystemClock.elapsedRealtime()
                if (currentTime - lastMeasurementTime >= samplingFrequency) {
                    val measurementsList = mutableListOf<String>()
                    var clock: GnssClock = event.clock

                    //First part of the logged rows.
                    val clockString = "Raw,"+
                            "$currentTime," +
                            "${clock.timeNanos}," +
                            "${clock.getTimeNanos()}," +
                            "${if (clock.hasLeapSecond()) clock.leapSecond else ""},"+
                            "${if(clock.hasTimeUncertaintyNanos()) clock.getTimeUncertaintyNanos() else ""},"+
                            "${clock.getFullBiasNanos()},"+
                            "${if(clock.hasBiasNanos()) clock.getBiasNanos() else ""},"+
                            "${if(clock.hasBiasUncertaintyNanos() ) clock.getBiasUncertaintyNanos() else ""},"+
                            "${if(clock.hasDriftNanosPerSecond()) clock.getDriftNanosPerSecond() else ""},"+
                            "${if(clock.hasDriftUncertaintyNanosPerSecond()) clock.getDriftUncertaintyNanosPerSecond() else ""},"+
                            "${clock.getHardwareClockDiscontinuityCount()}" + ","

                    for (measurement in event.measurements) {

                        //Second part of the logged rows.
                        val measurementString =
                                "${measurement.getSvid()}," +
                                "${measurement.getTimeOffsetNanos()}," +
                                "${measurement.getState()}," +
                                "${measurement.getReceivedSvTimeNanos()}," +
                                "${measurement.getReceivedSvTimeUncertaintyNanos()}," +
                                "${measurement.getCn0DbHz()}," +
                                "${measurement.getPseudorangeRateMetersPerSecond()}," +
                                "${measurement.getPseudorangeRateUncertaintyMetersPerSecond()}," +
                                "${measurement.getAccumulatedDeltaRangeState()}," +
                                "${measurement.getAccumulatedDeltaRangeMeters()}," +
                                "${measurement.getAccumulatedDeltaRangeUncertaintyMeters()}," +
                                "${if(measurement.hasCarrierFrequencyHz()) measurement.getCarrierFrequencyHz() else ""}," +
                                "${if(measurement.hasCarrierCycles()) measurement.carrierCycles else ""},"+
                                "${if(measurement.hasCarrierPhase()) measurement.carrierPhase else ""}," +
                                "${if(measurement.hasCarrierPhaseUncertainty()) measurement.carrierPhaseUncertainty else ""}," +
                                "${measurement.getMultipathIndicator()}," +
                                "${if(measurement.hasSnrInDb()) measurement.getSnrInDb() else ""}," +
                                "${measurement.getConstellationType()}," +
                                "${if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && measurement.hasAutomaticGainControlLevelDb())
                                    measurement.getAutomaticGainControlLevelDb() else ""}"

                        val eventString = clockString + measurementString

                        measurementsList.add(eventString)
                        Log.d("GNSS Measurement", measurementString)
                    }
                    gnssMeasurementsList.addAll(measurementsList)
                    lastMeasurementTime = currentTime
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(status: Int) {}
        }

        try {
            locationManager.registerGnssMeasurementsCallback(gnssMeasurementsEventListener)
        }  catch (e: SecurityException) {
            Log.d("Error", "No permission for location fetching")
        }

    }

    /* Logging for GNSS navigation messages. */
    fun logGnssNavigationMessages(samplingFrequency: Long){
        gnssNavigationMessageListener = object : GnssNavigationMessage.Callback(){
            var lastMeasurementTime = 0L
            override fun onGnssNavigationMessageReceived(event: GnssNavigationMessage?) {

                val currentTime = SystemClock.elapsedRealtime()
                if (currentTime - lastMeasurementTime >= samplingFrequency) {
                    var gnssNavigationMessageString = "Nav," +
                            "${event?.svid}," +
                            "${event?.type}," +
                            "${event?.status}," +
                            "${event?.messageId}," +
                            "${event?.submessageId},"

                    val data: ByteArray? = event?.getData()
                    if (data != null) {
                        for (word in data) {
                            gnssNavigationMessageString += "${word.toInt()},"
                        }
                    }
                    gnssMeasurementsList.add(gnssNavigationMessageString)
                }
            }
        }
        locationManager.registerGnssNavigationMessageCallback(gnssNavigationMessageListener)
    }

    /* Function that stops logging and writes collected data to the CSV-file. */
    fun stopLogging(context: Context) {
        locationManager.removeUpdates(gpsLocationListener)
        locationManager.removeUpdates(networkLocationListener)
        locationManager.unregisterGnssMeasurementsCallback(gnssMeasurementsEventListener)
        locationManager.unregisterGnssNavigationMessageCallback(gnssNavigationMessageListener)

        isLogging = false

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "watch_gnss_measurements_${
                SimpleDateFormat("ddMMyyyy_hhmmssSSS").format(
                    startTime
                )}.csv")
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        Log.d("uri", uri.toString())
        
        //Logging in wanted format. First hard coded comment to the file then data.
        uri?.let { mediaUri ->
            context.contentResolver.openOutputStream(mediaUri)?.use { outputStream ->
                outputStream.write("# ".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("Header Description:".toByteArray());
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("Version: ".toByteArray())
                var manufacturer: String = Build.MANUFACTURER
                var model: String = Build.MODEL
                var fileVersion: String = "${BuildConfig.VERSION_CODE}" + " Platform: " +
                        "${Build.VERSION.RELEASE}" + " " + "Manufacturer: "+
                        "${manufacturer}" + " " + "Model: " + "${model}"

                outputStream.write(fileVersion.toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write(
                    "Raw,ElapsedRealtimeMillis,TimeNanos,LeapSecond,TimeUncertaintyNanos,FullBiasNanos,".toByteArray()
                            + "BiasNanos,BiasUncertaintyNanos,DriftNanosPerSecond,DriftUncertaintyNanosPerSecond,".toByteArray()
                            + "HardwareClockDiscontinuityCount,Svid,TimeOffsetNanos,State,ReceivedSvTimeNanos,".toByteArray()
                            + "ReceivedSvTimeUncertaintyNanos,Cn0DbHz,PseudorangeRateMetersPerSecond,".toByteArray()
                            + "PseudorangeRateUncertaintyMetersPerSecond,".toByteArray()
                            + "AccumulatedDeltaRangeState,AccumulatedDeltaRangeMeters,".toByteArray()
                            + "AccumulatedDeltaRangeUncertaintyMeters,CarrierFrequencyHz,CarrierCycles,".toByteArray()
                            + "CarrierPhase,CarrierPhaseUncertainty,MultipathIndicator,SnrInDb,".toByteArray()
                            + "ConstellationType,AgcDb".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write(
                    "Fix,Provider,Latitude,Longitude,Altitude,Speed,Accuracy,(UTC)TimeInMs".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("Nav,Svid,Type,Status,MessageId,Sub-messageId,Data(Bytes)".toByteArray());
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("\n".toByteArray())
                gnssMeasurementsList.forEach { measurementString ->
                    outputStream.write("$measurementString\n".toByteArray())
                }
                outputStream.flush()
            }
        }

        var filePath = ""
        fun getRealPathFromUri(contentResolver: ContentResolver, uri: Uri): String {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(uri, projection, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            val path = columnIndex?.let { cursor?.getString(it) }
            cursor?.close()
            return path ?: ""
        }
        uri?.let { getRealPathFromUri(context.contentResolver, it) }
            ?.let { Log.d("uri", it)
                filePath = it}
        WatchActivityHandler.setFilePaths(File(filePath))
        Log.d(TAG, "gnss file path $filePath")
    }
}