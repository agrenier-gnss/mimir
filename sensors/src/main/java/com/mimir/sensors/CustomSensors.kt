package com.mimir.sensors

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.*
import android.os.SystemClock
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.*


// =================================================================================================

abstract class CustomSensor(
    _context: Context,
    _fileHandler: FileHandler,
    _type : SensorType,
    _typeTag : String,
    _sampling : Int,
    _mvalues: MutableList<Any>)
    : SensorEventListener {

    var isRegistered : Boolean = false
    protected val context  : Context    = _context.applicationContext
    protected val type     : SensorType = _type
    protected val typeTag  : String     = _typeTag
    protected val sampling : Int        = _sampling

    lateinit var sensor : Sensor
    lateinit var sensorManager : SensorManager
    lateinit var sensorSummary : String

    protected val fileHandler = _fileHandler

    var mvalues = _mvalues

    // ---------------------------------------------------------------------------------------------
    // Override methods

    override fun onSensorChanged(event: SensorEvent) {
        logSensor(event)
    }

    // ---------------------------------------------------------------------------------------------

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    // ---------------------------------------------------------------------------------------------
    // Methods

    open fun logSensor(event : SensorEvent){
        // Log.d("%s".format(sensor.stringType), event.toString())
        // To be override
    }

    // ---------------------------------------------------------------------------------------------

    open fun registerSensor(){

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (sensorManager.getDefaultSensor(type.value) != null) {
            sensor = sensorManager.getDefaultSensor(type.value)
            sensorManager.registerListener(this, sensor, sampling)
            sensorSummary = sensor.toString()
            this.isRegistered = true
            Log.i("Sensor", "$typeTag sensor registered")
        }
        else {
            Log.i("Sensor", "Does not have sensor for %s".format(typeTag))
        }

        // Create file header
        fileHandler.obtainMessage().also { msg ->
            msg.obj = getHeader()
            fileHandler.sendMessage(msg)
        }
    }

    // ---------------------------------------------------------------------------------------------

    open fun unregisterSensor(){
        sensorManager.unregisterListener(this)
        this.isRegistered = false
    }

    // ---------------------------------------------------------------------------------------------

    open fun getLogLine() : String{
        return ""
    }

    // ---------------------------------------------------------------------------------------------

    open fun getHeader() : String{
        val str : String

        if(!isRegistered){
            str = "# Sensor $typeTag disabled"
        } else{
            str = "# Sensor $typeTag enabled, $sensorSummary\n"
        }

        return str
    }
}

// =================================================================================================

class MotionSensor(
    context: Context,
    _fileHandler: FileHandler,
    _type: SensorType,
    _typeTag: String,
    _samplingFrequency: Int,
    _mvalues: MutableList<Any>)
    : CustomSensor(context, _fileHandler, _type, _typeTag, _samplingFrequency, _mvalues) {

    // ---------------------------------------------------------------------------------------------

    override fun logSensor(event: SensorEvent) {
        super.logSensor(event)

        // Log the values
        mvalues.add(event)
        //Log.d(typeTag, e.toString())

        // Send the values to the file
        fileHandler.obtainMessage().also { msg ->
            msg.obj = getLogLine(event)
            fileHandler.sendMessage(msg)
        }
    }

    // ---------------------------------------------------------------------------------------------

    fun getLogLine(event : SensorEvent): String {
        return String.format(
            Locale.US,
            "$typeTag,%s,%s,%s,%s,%s,%s",
            System.currentTimeMillis(),
            event.timestamp,
            event.values[0],
            event.values[1],
            event.values[2],
            event.accuracy)
    }

    // ---------------------------------------------------------------------------------------------

    override fun getHeader(): String {
        var str : String =  super.getHeader()

        str += String.format("# ${typeTag},utcTimeMillis,elapsedRealtime_nanosecond,")
        when(type){
            SensorType.TYPE_ACCELEROMETER
            -> str += String.format(
                "%s,%s,%s,%s",
                "x_meterPerSecond2",
                "y_meterPerSecond2",
                "z_meterPerSecond2",
                "accuracy")
            SensorType.TYPE_GYROSCOPE
            ->  str += String.format(
                "%s,%s,%s,%s",
                "x_radPerSecond",
                "y_radPerSecond",
                "z_radPerSecond",
                "accuracy")
            SensorType.TYPE_MAGNETIC_FIELD
            ->  str += String.format(
                "%s,%s,%s,%s",
                "x_microTesla",
                "y_microTesla",
                "z_microTesla",
                "accuracy")
            else -> Log.e("Sensors", "Invalid value $type")
        }
        str += "\n#"

        return str
    }
}

// =================================================================================================

class UncalibratedMotionSensor(
    context: Context,
    _fileHandler: FileHandler,
    _type: SensorType,
    _typeTag: String,
    _samplingFrequency: Int,
    _mvalues: MutableList<Any>)
    : CustomSensor(context, _fileHandler, _type, _typeTag, _samplingFrequency, _mvalues) {

    // ---------------------------------------------------------------------------------------------

    override fun logSensor(event: SensorEvent) {
        super.logSensor(event)

        // Log the values
        mvalues.add(event)
        //Log.d(typeTag, event.toString())

        fileHandler.obtainMessage().also { msg ->
            msg.obj = getLogLine(event)
            fileHandler.sendMessage(msg)
        }
    }

    // ---------------------------------------------------------------------------------------------

    fun getLogLine(event : SensorEvent): String {
        return String.format(
            Locale.US,
            "$typeTag,%s,%s,%s,%s,%s,%s,%s,%s,%s",
            System.currentTimeMillis(),
            event.timestamp,
            event.values[0],
            event.values[1],
            event.values[2],
            event.values[3],
            event.values[4],
            event.values[5],
            event.accuracy)
    }

    // ---------------------------------------------------------------------------------------------

    override fun getHeader(): String {
        var str : String =  super.getHeader()

        str += String.format("# ${typeTag},utcTimeMillis,elapsedRealtime_nanosecond,")
        when(type){
            SensorType.TYPE_ACCELEROMETER_UNCALIBRATED
            -> str += String.format(
                "%s,%s,%s,%s,%s,%s,%s",
                "x_uncalibrated_meterPerSecond2",
                "y_uncalibrated_meterPerSecond2",
                "z_uncalibrated_meterPerSecond2",
                "x_bias_meterPerSecond2",
                "y_bias_meterPerSecond2",
                "z_bias_meterPerSecond2",
                "accuracy")
            SensorType.TYPE_GYROSCOPE_UNCALIBRATED
            ->  str += String.format(
                "%s,%s,%s,%s,%s,%s,%s",
                "x_uncalibrated_radPerSecond",
                "y_uncalibrated_radPerSecond",
                "z_uncalibrated_radPerSecond",
                "x_bias_radPerSecond",
                "y_bias_radPerSecond",
                "z_bias_radPerSecond",
                "accuracy")
            SensorType.TYPE_MAGNETIC_FIELD_UNCALIBRATED
            ->  str += String.format(
                "%s,%s,%s,%s,%s,%s,%s",
                "x_uncalibrated_microTesla",
                "y_uncalibrated_microTesla",
                "z_uncalibrated_microTesla",
                "x_bias_microTesla",
                "y_bias_microTesla",
                "z_bias_microTesla",
                "accuracy")
            else -> Log.e("Sensors", "Invalid value $type")
        }
        str += "\n#"

        return str
    }
}

// =================================================================================================

class EnvironmentSensor(
    context: Context,
    _fileHandler: FileHandler,
    _type: SensorType,
    _typeTag: String,
    _samplingFrequency: Int,
    _mvalues: MutableList<Any>)
    : CustomSensor(context, _fileHandler, _type, _typeTag, _samplingFrequency, _mvalues) {

    // ---------------------------------------------------------------------------------------------

    override fun logSensor(event: SensorEvent) {
        super.logSensor(event)

        // Log the values
        mvalues.add(event)
        //Log.d(typeTag, event.toString())

        fileHandler.obtainMessage().also { msg ->
            msg.obj = getLogLine(event)
            fileHandler.sendMessage(msg)
        }
    }

    // ---------------------------------------------------------------------------------------------

    fun getLogLine(event : SensorEvent): String {
        return String.format(
            Locale.US,
            "$typeTag,%s,%s,%s,%s",
            System.currentTimeMillis(),
            event.timestamp,
            event.values[0],
            event.accuracy)
    }

    // ---------------------------------------------------------------------------------------------

    override fun getHeader(): String {
        var str : String =  super.getHeader()

        str += String.format("# ${typeTag},utcTimeMillis,elapsedRealtime_nanosecond,")
        when(type){
            SensorType.TYPE_PRESSURE
            -> str += String.format(
                "%s,%s",
                "pressure_hPa",
                "accuracy")
            else -> Log.e("Sensors", "Invalid value $type")
        }
        str += "\n#"

        return str
    }
}

// =================================================================================================

class GnssLocationSensor(
    context: Context,
    _fileHandler: FileHandler,
    _mvalues: MutableList<Any>)
    : CustomSensor(context, _fileHandler, SensorType.TYPE_GNSS_LOCATION, "Fix", 1000, _mvalues) {

    private var mLocationManager : LocationManager = context.getSystemService(Activity.LOCATION_SERVICE) as LocationManager
    private lateinit var mLocationListener : LocationListener

    init {

    }

    // ---------------------------------------------------------------------------------------------

    override fun registerSensor() {

        mLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                logSensor(location)
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        try {
            mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0.0F,
                mLocationListener,
                null
            );
            sensorSummary = "{Receiver: ${mLocationManager.gnssHardwareModelName}}"
            this.isRegistered = true
            Log.i("Sensor", "GNSS Location sensor registered")
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        // Create file header
        fileHandler.obtainMessage().also { msg ->
            msg.obj = getHeader()
            fileHandler.sendMessage(msg)
        }
    }

    // ---------------------------------------------------------------------------------------------

    override fun unregisterSensor() {
        mLocationManager.removeUpdates(mLocationListener)
    }

    // ---------------------------------------------------------------------------------------------

    fun logSensor(location: Location) {

        // Log the values
        mvalues.add(location)
        //Log.d(typeTag, event.toString())

        fileHandler.obtainMessage().also { msg ->
            msg.obj = getLogLine(location)
            fileHandler.sendMessage(msg)
        }
    }

    // ---------------------------------------------------------------------------------------------

    private fun getLogLine(location: Location): String {

        // Based on GNSS logger app logging

        return String.format(
            Locale.US,
            "$typeTag,%s,%.8f,%.8f,%.3f,%.3f,%.3f,%f,%d,%f,%f,%d,%f,%f",
            location.provider.toString().uppercase(),
            location.latitude,
            location.longitude,
            location.altitude,
            location.speed,
            location.accuracy,
            location.bearing,
            location.time,
            location.speedAccuracyMetersPerSecond,
            location.bearingAccuracyDegrees,
            location.elapsedRealtimeNanos,
            location.verticalAccuracyMeters,
            location.elapsedRealtimeUncertaintyNanos)
    }

    // ---------------------------------------------------------------------------------------------

    override fun getHeader(): String {

        var str : String =  super.getHeader()

        str += String.format(
            "# $typeTag,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
            "Provider",
            "Latitude_decimalDegree",
            "Longitude_decimalDegree",
            "Altitude_meter",
            "Speed_meterPerSecond",
            "Accuracy_meter",
            "Bearing_degree",
            "UnixTime_millisecond",
            "SpeedAccuracy_meterPerSecond",
            "BearingAccuracy_degree",
            "ElapsedRealtime_nanosecond",
            "VerticalAccuracy_meter",
            "ElapsedRealtimeUncertainty_nanosecond")
        str += "\n#"

        return str
    }
}

// =================================================================================================

class GnssMeasurementSensor(
    context: Context,
    _fileHandler: FileHandler,
    _mvalues: MutableList<Any>)
    : CustomSensor(context, _fileHandler, SensorType.TYPE_GNSS_MEASUREMENTS, "Raw", 1000, _mvalues) {

    private var mLocationManager : LocationManager = context.getSystemService(Activity.LOCATION_SERVICE) as LocationManager
    private lateinit var mGnssMeasurementsEventCallback : GnssMeasurementsEvent.Callback

    init {

    }

    // ---------------------------------------------------------------------------------------------

    override fun registerSensor() {
        mGnssMeasurementsEventCallback = object : GnssMeasurementsEvent.Callback() {
            override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
                super.onGnssMeasurementsReceived(event)
                logSensor(event)
            }
        }

        // Register callback
        try {
            mGnssMeasurementsEventCallback.let {
                mLocationManager.registerGnssMeasurementsCallback(context.mainExecutor, it)
            }
            sensorSummary = "{Receiver: ${mLocationManager.gnssHardwareModelName}}"
            this.isRegistered = true
            Log.i("Sensor", "GNSS Measurement sensor registered")
        }
        catch (e: SecurityException) {
            e.printStackTrace()
        }

        // Create file header
        fileHandler.obtainMessage().also { msg ->
            msg.obj = getHeader()
            fileHandler.sendMessage(msg)
        }
    }

    // ---------------------------------------------------------------------------------------------

    override fun unregisterSensor() {
        mLocationManager.unregisterGnssMeasurementsCallback(mGnssMeasurementsEventCallback)
    }

    // ---------------------------------------------------------------------------------------------

    fun logSensor(event: GnssMeasurementsEvent) {

        // Log the values
        mvalues.add(event)
        //Log.d(typeTag, event.toString())

        event.measurements.forEach {
            fileHandler.obtainMessage().also { msg ->
                msg.obj = getLogLine(event.clock, it)
                fileHandler.sendMessage(msg)
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private fun getLogLine(gnssClock: GnssClock, measurement : GnssMeasurement): String {
        return String.format(
            Locale.US,
            "$typeTag,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
            System.currentTimeMillis(),
            gnssClock.timeNanos,
            if (gnssClock.hasLeapSecond()) gnssClock.leapSecond else "",
            if (gnssClock.hasTimeUncertaintyNanos()) gnssClock.timeUncertaintyNanos else "",
            gnssClock.fullBiasNanos,
            if (gnssClock.hasBiasNanos()) gnssClock.biasNanos else "",
            if (gnssClock.hasBiasUncertaintyNanos()) gnssClock.biasUncertaintyNanos else "",
            if (gnssClock.hasDriftNanosPerSecond()) gnssClock.driftNanosPerSecond else "",
            if (gnssClock.hasDriftUncertaintyNanosPerSecond()) gnssClock.driftUncertaintyNanosPerSecond else "",
            gnssClock.hardwareClockDiscontinuityCount.toString(),
            measurement.svid,
            measurement.timeOffsetNanos,
            measurement.state,
            measurement.receivedSvTimeNanos,
            measurement.receivedSvTimeUncertaintyNanos,
            measurement.cn0DbHz,
            measurement.pseudorangeRateMetersPerSecond,
            measurement.pseudorangeRateUncertaintyMetersPerSecond,
            measurement.accumulatedDeltaRangeState,
            measurement.accumulatedDeltaRangeMeters,
            measurement.accumulatedDeltaRangeUncertaintyMeters,
            if (measurement.hasCarrierFrequencyHz()) measurement.carrierFrequencyHz else "",
            if (measurement.hasCarrierCycles()) measurement.carrierCycles else "",
            if (measurement.hasCarrierPhase()) measurement.carrierPhase else "",
            if (measurement.hasCarrierPhaseUncertainty()) measurement.carrierPhaseUncertainty else "",
            measurement.multipathIndicator,
            if (measurement.hasSnrInDb()) measurement.snrInDb else "",
            measurement.constellationType,
            if (measurement.hasAutomaticGainControlLevelDb()) measurement.automaticGainControlLevelDb else "",
            if (measurement.hasBasebandCn0DbHz()) measurement.basebandCn0DbHz else "",
            if (measurement.hasFullInterSignalBiasNanos()) measurement.fullInterSignalBiasNanos else "",
            if (measurement.hasFullInterSignalBiasUncertaintyNanos()) measurement.fullInterSignalBiasUncertaintyNanos else "",
            if (measurement.hasSatelliteInterSignalBiasNanos()) measurement.satelliteInterSignalBiasNanos else "",
            if (measurement.hasSatelliteInterSignalBiasUncertaintyNanos()) measurement.satelliteInterSignalBiasUncertaintyNanos else "",
            if (measurement.hasCodeType()) measurement.codeType else "",
            gnssClock.elapsedRealtimeNanos
        )
    }

    // ---------------------------------------------------------------------------------------------

    override fun getHeader(): String {
        var str : String =  super.getHeader()

        str += String.format(
            "# $typeTag,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
            "utcTimeMillis",
            "TimeNanos",
            "LeapSecond",
            "TimeUncertaintyNanos",
            "FullBiasNanos",
            "BiasNanos",
            "BiasUncertaintyNanos",
            "DriftNanosPerSecond",
            "DriftUncertaintyNanosPerSecond",
            "HardwareClockDiscontinuityCount",
            "Svid",
            "TimeOffsetNanos",
            "State",
            "ReceivedSvTimeNanos",
            "ReceivedSvTimeUncertaintyNanos",
            "Cn0DbHz",
            "PseudorangeRateMetersPerSecond",
            "PseudorangeRateUncertaintyMetersPerSecond",
            "AccumulatedDeltaRangeState",
            "AccumulatedDeltaRangeMeters",
            "AccumulatedDeltaRangeUncertaintyMeters",
            "CarrierFrequencyHz",
            "CarrierCycles",
            "CarrierPhase",
            "CarrierPhaseUncertainty",
            "MultipathIndicator",
            "SnrInDb",
            "ConstellationType",
            "AgcDb",
            "BasebandCn0DbHz",
            "FullInterSignalBiasNanos",
            "FullInterSignalBiasUncertaintyNanos",
            "SatelliteInterSignalBiasNanos",
            "SatelliteInterSignalBiasUncertaintyNanos",
            "CodeType",
            "ChipsetElapsedRealtimeNanos")
        str += "\n#"

        return str
    }
}

// =================================================================================================

class GnssNavigationMessageSensor(
    context: Context,
    _fileHandler: FileHandler,
    _mvalues: MutableList<Any>)
    : CustomSensor(context, _fileHandler, SensorType.TYPE_GNSS_MESSAGES, "Nav", 0, _mvalues) {

    private var mLocationManager : LocationManager = context.getSystemService(Activity.LOCATION_SERVICE) as LocationManager
    private lateinit var mGnssNavigationMessageCallback : GnssNavigationMessage.Callback


    init {

    }

    // ---------------------------------------------------------------------------------------------

    override fun registerSensor() {
        mGnssNavigationMessageCallback = object : GnssNavigationMessage.Callback(){
            override fun onGnssNavigationMessageReceived(event: GnssNavigationMessage) {
                super.onGnssNavigationMessageReceived(event)
                logSensor(event)
            }
        }

        // Register callback
        try {
            mGnssNavigationMessageCallback.let {
                mLocationManager.registerGnssNavigationMessageCallback(context.mainExecutor, it)
            }
            sensorSummary = "{Receiver: ${mLocationManager.gnssHardwareModelName}}"
            this.isRegistered = true
            Log.i("Sensor", "GNSS Navigation Message sensor registered")
        }
        catch (e: SecurityException) {
            e.printStackTrace()
        }

        // Create file header
        fileHandler.obtainMessage().also { msg ->
            msg.obj = getHeader()
            fileHandler.sendMessage(msg)
        }
    }

    // ---------------------------------------------------------------------------------------------

    override fun unregisterSensor() {
        mLocationManager.unregisterGnssNavigationMessageCallback(mGnssNavigationMessageCallback)
    }

    // ---------------------------------------------------------------------------------------------

    fun logSensor(event : GnssNavigationMessage){

        // Log the values
        mvalues.add(event)
        //Log.d(typeTag, event.toString())

        fileHandler.obtainMessage().also { msg ->
            msg.obj = getLogLine(event)
            fileHandler.sendMessage(msg)
            //Log.d(typeTag, msg.obj.toString())
        }
    }

    // ---------------------------------------------------------------------------------------------

    private fun getLogLine(event : GnssNavigationMessage): String {
        return String.format(
            Locale.US,
            "$typeTag,%s,%s,%s,%s,%s,%s,%s",
            System.currentTimeMillis(),
            event.svid,
            event.type,
            event.status,
            event.messageId,
            event.submessageId,
            event.data.joinToString(separator = ","))
    }

    // ---------------------------------------------------------------------------------------------

    override fun getHeader(): String {

        var str : String =  super.getHeader()

        str += String.format(
            "# $typeTag,%s,%s,%s,%s,%s,%s,%s",
            "utcTimeMillis",
            "Svid",
            "Type",
            "Status",
            "MessageId",
            "Sub-messageId",
            "Data(Bytes)")
        str += "\n#"

        return str
    }
}

// =================================================================================================

class BluetoothSensor(
    context: Context,
    _fileHandler: FileHandler,
    _mvalues: MutableList<Any>)
    : CustomSensor(context, _fileHandler, SensorType.TYPE_BLUETOOTH, "BLE", 0, _mvalues) {

    private var mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var mBluetoothLeScanner: BluetoothLeScanner = mBluetoothAdapter.bluetoothLeScanner
    private lateinit var mBluetoothScanCallback: ScanCallback

    init {

    }

    // ---------------------------------------------------------------------------------------------

    override fun registerSensor() {
        mBluetoothScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                    logSensor(result)
                }
            }

        // Start
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mBluetoothLeScanner.startScan(mBluetoothScanCallback)
        Log.i("Sensor", "Bluetooth sensor registered")
    }

    // ---------------------------------------------------------------------------------------------

    override fun unregisterSensor() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mBluetoothLeScanner.stopScan(mBluetoothScanCallback)
    }

    // ---------------------------------------------------------------------------------------------

    fun logSensor(scan: ScanResult) {

        // Log the values
        mvalues.add(scan)
        //Log.d(typeTag, event.toString())

        fileHandler.obtainMessage().also { msg ->
            msg.obj = getLogLine(scan)
            fileHandler.sendMessage(msg)
            Log.d(typeTag, msg.obj.toString())
        }
    }

    // ---------------------------------------------------------------------------------------------

    fun getLogLine(scan: ScanResult) : String{

        return String.format(
            Locale.US,
            "$typeTag,%s,%s,%s,%s,%s,%s,%s,%s",
            SystemClock.currentGnssTimeClock().millis(),
            scan.timestampNanos,
            scan.device,
            scan.rssi,
            scan.advertisingSid,
            scan.txPower,
            scan.dataStatus,
            scan.scanRecord?.bytes.contentToString())
    }
}

// =================================================================================================

class HeartRateSensor(
    context: Context,
    _fileHandler: FileHandler,
    _type: SensorType,
    _typeTag: String,
    _samplingFrequency: Int,
    _mvalues: MutableList<Any>)
    : CustomSensor(context, _fileHandler, _type, _typeTag, 1000, _mvalues) {

    // ---------------------------------------------------------------------------------------------

    override fun logSensor(event: SensorEvent) {
        super.logSensor(event)

        // Log the values
        mvalues.add(event)
        //Log.d(typeTag, event.toString())

        fileHandler.obtainMessage().also { msg ->
            msg.obj = getLogLine(event)
            fileHandler.sendMessage(msg)
        }
    }

    // ---------------------------------------------------------------------------------------------

    fun getLogLine(event : SensorEvent): String {
        return String.format(
            Locale.US,
            "$typeTag,%s,%s,%s,%s",
            System.currentTimeMillis(),
            event.timestamp,
            event.values[0],
            event.accuracy)
    }

    // ---------------------------------------------------------------------------------------------

    override fun getHeader(): String {
        var str : String =  super.getHeader()

        str += String.format("# ${typeTag},utcTimeMillis,elapsedRealtime_nanosecond,")
        when(type){
            SensorType.TYPE_HEART_RATE
            -> str += String.format(
                "%s,%s",
                "rate_beatPerSecond",
                "accuracy")
            else -> Log.e("Sensors", "Invalid value $type")
        }
        str += "\n#"

        return str
    }
}

// =================================================================================================

class SpecificSensor(
    context: Context,
    _fileHandler: FileHandler,
    _name: String,
    _type: SensorType,
    _typeTag: String,
    _samplingFrequency: Int,
    _mvalues: MutableList<Any>)
    : CustomSensor(context, _fileHandler, _type, _typeTag, _samplingFrequency, _mvalues){

    private val name : String = _name

    // ---------------------------------------------------------------------------------------------

    override fun registerSensor() {

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        // Loop through the devices sensors to find the right one
        // TODO Maybe there is a better way to request a specific sensor by name?
        deviceSensors.forEach  {
            if (it.name == name) {
                sensor = it
                sensorManager.registerListener(this, sensor, sampling)
                this.isRegistered = true
            }
        }

        if(this.isRegistered){
            sensorSummary = sensor.toString()
            Log.i("Sensor", "$typeTag sensor registered")

            // Create file header
            fileHandler.obtainMessage().also { msg ->
                msg.obj = getHeader()
                fileHandler.sendMessage(msg)
            }
        }
        else {
            Log.i("Sensor", "Does not have sensor for %s".format(typeTag))
        }
    }

    // ---------------------------------------------------------------------------------------------

    override fun logSensor(event: SensorEvent) {
        super.logSensor(event)

        // Log the values
        mvalues.add(event)
        // Log.d(typeTag, event.toString())

        fileHandler.obtainMessage().also { msg ->
            msg.obj = getLogLine(event)
            fileHandler.sendMessage(msg)
        }
    }

    // ---------------------------------------------------------------------------------------------

    fun getLogLine(event : SensorEvent): String {
        return String.format(
            Locale.US,
            "$typeTag,%s,%s,%s,%s",
            System.currentTimeMillis(),
            event.timestamp,
            event.accuracy,
            event.values.joinToString(separator = ","))
    }

    // ---------------------------------------------------------------------------------------------

    override fun getHeader(): String {
        var str : String =  super.getHeader()

        str += String.format("# ${typeTag},utcTimeMillis,elapsedRealtime_nanosecond,")
        str += String.format(
                "%s,%s",
                "accuracy",
                "values")
        str += "\n#"

        return str
    }
}

// =================================================================================================

class StepSensor(
    context: Context,
    _fileHandler: FileHandler,
    _type: SensorType,
    _typeTag: String,
    _samplingFrequency: Int,
    _mvalues: MutableList<Any>)
    : CustomSensor(context, _fileHandler, _type, _typeTag, _samplingFrequency, _mvalues) {

    // ---------------------------------------------------------------------------------------------

    override fun logSensor(event: SensorEvent) {
        super.logSensor(event)

        // Log the values
        mvalues.add(event)
        //Log.d(typeTag, event.toString())

        fileHandler.obtainMessage().also { msg ->
            msg.obj = getLogLine(event)
            fileHandler.sendMessage(msg)
        }
    }

    // ---------------------------------------------------------------------------------------------

    fun getLogLine(event : SensorEvent): String {
        return String.format(
            Locale.US,
            "$typeTag,%s,%s,%s,%s",
            System.currentTimeMillis(),
            event.timestamp,
            event.values[0],
            event.accuracy)
    }

    // ---------------------------------------------------------------------------------------------

    override fun getHeader(): String {
        var str : String =  super.getHeader()

        str += String.format("# ${typeTag},utcTimeMillis,elapsedRealtime_nanosecond,")
        when(type){
            SensorType.TYPE_STEP_COUNTER
            -> str += String.format(
                "%s,%s",
                "steps_count",
                "accuracy")
            SensorType.TYPE_STEP_DETECTOR
            -> str += String.format(
                "%s,%s",
                "step_detected",
                "accuracy")
            else -> Log.e("Sensors", "Invalid value $type")
        }
        str += "\n#"

        return str
    }
}



















