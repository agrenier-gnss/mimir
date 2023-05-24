package com.mobilewizards.watchlogger

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.mobilewizards.logging_app.BuildConfig
import com.mobilewizards.logging_app.startTime
import java.io.File

private var acSensor : Sensor? = null
private var biasSensor : Sensor? = null
private var gyroSensor : Sensor? = null
private var unCalGyroSensor : Sensor? = null
private var gravSensor : Sensor? = null
private var stepSensor : Sensor? = null
private var magnetometer : Sensor? = null
private var barometer : Sensor? = null

private var side: Float = Float.MIN_VALUE
private var upDown: Float = Float.MIN_VALUE
private var vertical: Float = Float.MIN_VALUE

private var sideNoBias : Float = Float.MIN_VALUE
private var upDownNoBias: Float = Float.MIN_VALUE
private var verticalNoBias: Float = Float.MIN_VALUE
private var sideBias : Float = Float.MIN_VALUE
private var upDownBias : Float = Float.MIN_VALUE
private var verticalBias : Float = Float.MIN_VALUE

private var gravX: Float = Float.MIN_VALUE
private var gravY: Float = Float.MIN_VALUE
private var gravZ: Float = Float.MIN_VALUE

private var rotationX: Float = Float.MIN_VALUE
private var rotationY: Float = Float.MIN_VALUE
private var rotationZ: Float = Float.MIN_VALUE

private var rotationXNoDrift: Float = Float.MIN_VALUE
private var rotationYNoDrift: Float = Float.MIN_VALUE
private var rotationZNoDrift: Float = Float.MIN_VALUE
private var rotationXDrift: Float = Float.MIN_VALUE
private var rotationYDrift: Float = Float.MIN_VALUE
private var rotationZDrift: Float = Float.MIN_VALUE

data class AccelerometerValues(val timestamp: Long, val sideTilt: Float, val upDownTilt: Float, val verticalTilt: Float)
private var accelerometerValues = mutableListOf<AccelerometerValues>()

data class UnCalibratedAccelerometerValues(val timestamp: Long, val sideX: Float, val upDownY: Float, val verticalZ: Float, val sideXB: Float, val upDownYB: Float, val verticalZB: Float)
private var unCalibratedAccelerometer = mutableListOf<UnCalibratedAccelerometerValues>()

data class GravityValues(val timestamp: Long, val gravityX: Float, val gravityY: Float, val gravityZ: Float)
private var gravityValues = mutableListOf<GravityValues>()

data class GyroscopeValues(val timestamp: Long, val rotX: Float, val rotY: Float, val rotZ: Float)
private var gyroscopeValues = mutableListOf<GyroscopeValues>()

data class UnCalibratedGyroscopeValues(val timestamp: Long, val noDriftX: Float, val noDriftY: Float, val noDriftZ: Float, val driftX: Float, val driftY: Float, val driftZ: Float)
private var unCalibratedGyroscopeValues = mutableListOf<UnCalibratedGyroscopeValues>()

private var stepCount: Float = Float.MIN_VALUE

data class MagnetometerValues(val timestamp: Long, val x: Float, val y: Float, val z: Float)

private var magnetometerValues = mutableListOf<MagnetometerValues>()
private var barometerValues = mutableListOf<Pair<Long,Float>>()

private const val VERSION_TAG = "Version: "
private const val COMMENT_START = "# "

// Inside your activity or service
class IMUHandlerWatch(context: Context): SensorEventListener {

    protected var context = context.applicationContext

    // Obtain a reference to the sensor manager
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun setUpSensors(imuFrequency: Int, magnetometerFrequency: Int, barometerFrequency: Int) {

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
                acSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                sensorManager.registerListener(this, acSensor, imuFrequency)
            }  else {

                Log.i("Does not have sensor for ACCELEROMETER", acSensor.toString())
            }
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED) != null){
                biasSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)
                sensorManager.registerListener(this, biasSensor, imuFrequency)

            } else {

                Log.i("Does not have sensor for UNCALIBRATED ACCELEROMETER", biasSensor.toString())
            }
            if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
                gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
                sensorManager.registerListener(this, gyroSensor, imuFrequency)
            }  else {

                Log.i("Does not have sensor for GYROSCOPE", gyroSensor.toString())
            }

            if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED) != null){
                unCalGyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED)
                sensorManager.registerListener(this, unCalGyroSensor, imuFrequency)
            }  else {

                Log.i("Does not have sensor for UNCALIBRATED GYROSCOPE", unCalGyroSensor.toString())
            }
            if(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
                gravSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
                sensorManager.registerListener(this, gravSensor, imuFrequency)
            }   else {

                Log.i("Does not have sensor for GRAVITY", gravSensor.toString())
            }

            if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
                stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                sensorManager.registerListener(this, stepSensor, imuFrequency)
            }   else {

                Log.i("Does not have sensor for STEP COUNTER", stepSensor.toString())
            }
            if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            sensorManager.registerListener(this, magnetometer, magnetometerFrequency)
            }

            if(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
                barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
                sensorManager.registerListener(this, barometer, barometerFrequency)
            }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == acSensor?.type) {
            logAccelerometer(event)
        }

        if (event?.sensor?.type == biasSensor?.type) {
            logUnCalibratedAccelerometer(event)
        }

        if(event?.sensor?.type == Sensor.TYPE_GRAVITY){
            logGravity(event)
        }

        if (event?.sensor?.type == gyroSensor?.type) {
            logGyroscope(event)
        }

        if (event?.sensor?.type == unCalGyroSensor?.type) {
            logUnCalibratedGyroscope(event)
        }

        if (event?.sensor?.type == stepSensor?.type) {
            logSteps(event)
        }

        if (event?.sensor?.type == magnetometer?.type) {
            logMagnetometer(event)
        }

        if (event?.sensor?.type == barometer?.type) {
            logBarometer(event)
        }
    }

    private fun logAccelerometer(event: SensorEvent?) {
        if (event?.sensor?.type == acSensor?.type && event?.sensor?.type != null) {

            val sideTilt = event.values[0]
            if (sideTilt != side) {
                side = sideTilt
                Log.d("Tilting from side to side | Acceleration force along the X axis | Includes gravity", sideTilt.toString())

            }

            val upDownTilt = event.values[1]
            if (upDownTilt != upDown) {
                upDown = upDownTilt
                Log.d("Tilting up or down | Acceleration force along the Y axis | Includes gravity", upDownTilt.toString())
            }

            val verticalTilt = event.values[2]
            if (verticalTilt != vertical) {
                vertical = verticalTilt
                Log.d("Tilting vertically | Acceleration force along the Z axis | Includes gravity", verticalTilt.toString())
            }

            accelerometerValues.add(AccelerometerValues(event.timestamp,sideTilt,upDownTilt,verticalTilt))
        }
    }


    private fun logUnCalibratedAccelerometer(event: SensorEvent?) {

        if (event?.sensor?.type == biasSensor?.type && event?.sensor?.type != null) {

            val sideX = event.values[0]
            val upDownY = event.values[1]
            val verticalZ = event.values[2]
            val sideXB = event.values[3]
            val upDownYB = event.values[4]
            val verticalZB = event.values[5]

            if (sideX != sideNoBias) {
                sideNoBias = sideX
                Log.d("Acceleration along the X axis | NO bias compensation", sideX.toString())

            }

            if (upDownY != upDownNoBias) {
                upDownNoBias = upDownY
                Log.d("Acceleration along the Y axis | NO bias compensation", upDownY.toString())
            }

            if (verticalZ != verticalNoBias) {
                verticalNoBias = verticalZ
                Log.d("Acceleration along the Z axis | NO bias compensation", verticalZ.toString())
            }

            if (sideXB != sideBias) {
                sideBias = sideXB
                Log.d("Acceleration along the X axis | WITH bias compensation", sideXB.toString())

            }

            if (upDownYB != upDownBias) {
                upDownBias = upDownYB
                Log.d("Acceleration along the Y axis | WITH bias compensation", upDownYB.toString())
            }

            if (verticalZB != verticalBias) {
                verticalBias = verticalZB
                Log.d("Acceleration along the Z axis | WITH bias compensation", verticalZB.toString())
            }

            unCalibratedAccelerometer.add(UnCalibratedAccelerometerValues(event.timestamp,sideX,upDownY,verticalZ,sideXB,upDownYB,verticalZB))
        }
    }
    private fun logGravity(event: SensorEvent?) {

        if (event?.sensor?.type == gravSensor?.type && event?.sensor?.type != null) {
            val gravityX = event.values[0]
            val gravityY = event.values[1]
            val gravityZ = event.values[2]

            if (gravityX != gravX) {
                gravX = gravityX
                Log.d("Gravity along X", gravityX.toString())
            }

            if (gravityY != gravY) {
                gravY = gravityY
                Log.d("Gravity along Y", gravityY.toString())
            }

            if (gravityZ != gravZ) {
                gravZ = gravityZ
                Log.d("Gravity along Z", gravityZ.toString())
            }

            gravityValues.add(GravityValues(event.timestamp,gravityX,gravityY,gravityZ))
        }
    }

    private fun logGyroscope(event: SensorEvent?) {

        if (event?.sensor?.type == gyroSensor?.type && event?.sensor?.type != null) {
            val rotX = event.values[0]
            val rotY = event.values[1]
            val rotZ = event.values[2]

            if (rotX != rotationX) {
                rotationX = rotX
                Log.d("Rotation along the X axis", rotX.toString())
            }

            if (rotY != rotationY) {
                rotationY = rotY
                Log.d("Rotation along Y axis", rotY.toString())
            }

            if (rotZ != rotationZ) {
                rotationZ = rotZ
                Log.d("Rotation along Z axis", rotZ.toString())
            }

            gyroscopeValues.add(GyroscopeValues(event.timestamp,rotX,rotY,rotZ))
        }
    }

    private fun logUnCalibratedGyroscope(event: SensorEvent?) {

        if (event?.sensor?.type == biasSensor?.type && event?.sensor?.type != null) {
            val noDriftX = event.values[0]
            val noDriftY = event.values[1]
            val noDriftZ = event.values[2]

            val driftX = event.values[3]
            val driftY = event.values[4]
            val driftZ = event.values[5]

            if (noDriftX != rotationXNoDrift) {
                rotationXNoDrift = noDriftX
                Log.d("Rotation along the X axis| NO drift compensation", noDriftX.toString())
            }

            if (noDriftY != rotationYNoDrift) {
                rotationYNoDrift =  noDriftY
                Log.d("Rotation along the Y axis| NO drift compensation", noDriftY.toString())
            }

            if (noDriftZ != rotationZNoDrift) {
                rotationZNoDrift = noDriftZ
                Log.d("Rotation along the Z axis| NO drift compensation", noDriftZ.toString())
            }

            if (driftX != rotationXDrift) {
                rotationXDrift = driftX
                Log.d("Rotation along the X axis| WITH drift compensation", driftX.toString())

            }

            if (driftY != rotationYDrift) {
                rotationYDrift = driftY
                Log.d("Rotation along the Y axis| WITH drift compensation", driftY.toString())
            }

            if (driftZ != rotationZDrift) {
                rotationZDrift = driftZ
                Log.d("Rotation along the Z axis| WITH drift compensation", driftZ.toString())
            }

            unCalibratedGyroscopeValues.add(UnCalibratedGyroscopeValues(event.timestamp,noDriftX,noDriftY,noDriftZ,driftX,driftY,driftZ))
        }
    }

    private fun logSteps(event: SensorEvent?) {

        if (event?.sensor?.type == stepSensor?.type && event?.sensor?.type != null) {
            val steps = event.values[0]

            if (steps != stepCount) {
                stepCount += steps
                Log.d("Steps", steps.toString())
            }
        }
    }

    private fun logMagnetometer(event: SensorEvent?) {

        if (event?.sensor?.type == magnetometer?.type && event?.sensor?.type != null) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            magnetometerValues.add(MagnetometerValues(event.timestamp,x,y,z))
        }
    }

    private fun logBarometer(event: SensorEvent?) {

        if (event?.sensor?.type == barometer?.type && event?.sensor?.type != null) {

            val pressure = event.values[0]

            barometerValues.add(Pair(event.timestamp,pressure))
        }
    }

    private fun writeIMU() {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "watch_imu_${SimpleDateFormat("ddMMyyyy_hhmmssSSS").format(
                startTime)}.csv")
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        Log.d("uri", uri.toString())
        uri?.let { mediaUri ->
            context.contentResolver.openOutputStream(mediaUri)?.use { outputStream ->
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray());
                outputStream.write("Header Description:".toByteArray());
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write(VERSION_TAG.toByteArray())
                var manufacturer: String = Build.MANUFACTURER
                var model: String = Build.MODEL
                var fileVersion: String = "${BuildConfig.VERSION_CODE}" + " Platform: " +
                        "${Build.VERSION.RELEASE}" + " " + "Manufacturer: " +
                        "${manufacturer}" + " " + "Model: " + "${model}"

                outputStream.write(fileVersion.toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("AccelerometerValues,timestamp,sideTilt,upDownTilt,verticalTilt".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("UnCalibratedAccelerometerValues,timestamp,sideX,upDownY,verticalZ,sideXB,upDownYB,verticalZB".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("GravityValues,timestamp,gravityX,gravityY,gravityZ".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("GyroscopeValues,timestamp,rotX,rotY,rotZ".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("UnCalibratedGyroscopeValues,timestamp,noDriftX,noDriftY,noDriftZ,driftX,driftY,driftZ".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("StepCount,stepCount".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write(COMMENT_START.toByteArray())
                outputStream.write("\n".toByteArray())

                accelerometerValues.forEach { measurementString ->
                    outputStream.write("accelerometerValues,${measurementString.timestamp},${measurementString.sideTilt},${measurementString.upDownTilt},${measurementString.verticalTilt}\n".toByteArray())
                }
                unCalibratedAccelerometer.forEach { measurementString ->
                    outputStream.write("unCalibratedAccelerometer,${measurementString.timestamp},${measurementString.sideX},${measurementString.upDownY},${measurementString.verticalZ},${measurementString.sideXB},${measurementString.upDownYB},${measurementString.verticalZB}\n".toByteArray())
                }
                gravityValues.forEach { measurementString ->
                    outputStream.write("gravityValues,${measurementString.timestamp},${measurementString.gravityX},${measurementString.gravityY},${measurementString.gravityZ}\n".toByteArray())
                }
                gyroscopeValues.forEach { measurementString ->
                    outputStream.write("gyroscopeValues,${measurementString.timestamp},${measurementString.rotX},${measurementString.rotY},${measurementString.rotZ}\n".toByteArray())
                }
                unCalibratedGyroscopeValues.forEach { measurementString ->
                    outputStream.write("unCalibratedGyroscopeValues,${measurementString.timestamp},${measurementString.noDriftX},${measurementString.noDriftY},${measurementString.noDriftZ},${measurementString.driftX},${measurementString.driftY},${measurementString.driftZ}\n".toByteArray())
                }
                outputStream.write("StepCount,$stepCount\n".toByteArray())

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

    }

    private fun writeMagnetometer() {

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, "watch_magnetometer_${SimpleDateFormat("ddMMyyyy_hhmmssSSS").format(startTime)}.csv")
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            Log.d("uri", uri.toString())
            uri?.let { mediaUri ->
                context.contentResolver.openOutputStream(mediaUri)?.use { outputStream ->
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray());
                    outputStream.write("Header Description:".toByteArray());
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write(VERSION_TAG.toByteArray())
                    var manufacturer: String = Build.MANUFACTURER
                    var model: String = Build.MODEL
                    var fileVersion: String = "${BuildConfig.VERSION_CODE}" + " Platform: " +
                            "${Build.VERSION.RELEASE}" + " " + "Manufacturer: " +
                            "${manufacturer}" + " " + "Model: " + "${model}"

                    outputStream.write(fileVersion.toByteArray())
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write("timestamp,x,y,z".toByteArray())
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write("\n".toByteArray())

                    magnetometerValues.forEach { measurementString ->
                        outputStream.write("${measurementString.timestamp},${measurementString.x},${measurementString.y},${measurementString.z}\n".toByteArray())
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

        } catch(e: Exception){
            Log.e("Error", "An error occurred while saving magnetometer results")
            val view = (context as Activity).findViewById<View>(android.R.id.content)
            val snackbar = Snackbar.make(view, "Error. An error occurred while saving magnetometer results-", Snackbar.LENGTH_LONG)
            snackbar.setAction("Close") {
                snackbar.dismiss()
            }
            snackbar.show()
        }
    }

    private fun writeBarometer() {
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, "watch_barometer_${SimpleDateFormat("ddMMyyyy_hhmmssSSS").format(startTime)}.csv")
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            Log.d("uri", uri.toString())
            uri?.let { mediaUri ->
                context.contentResolver.openOutputStream(mediaUri)?.use { outputStream ->
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray());
                    outputStream.write("Header Description:".toByteArray());
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write(VERSION_TAG.toByteArray())
                    var manufacturer: String = Build.MANUFACTURER
                    var model: String = Build.MODEL
                    var fileVersion: String = "${BuildConfig.VERSION_CODE}" + " Platform: " +
                            "${Build.VERSION.RELEASE}" + " " + "Manufacturer: " +
                            "${manufacturer}" + " " + "Model: " + "${model}"

                    outputStream.write(fileVersion.toByteArray())
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write("timestamp,pressure".toByteArray())
                    outputStream.write("\n".toByteArray())
                    outputStream.write(COMMENT_START.toByteArray())
                    outputStream.write("\n".toByteArray())

                    barometerValues.forEach { measurementString ->
                        outputStream.write("${measurementString.first},${measurementString.second}\n".toByteArray())
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

        } catch(e: Exception){
            Log.e("Error", "An error occurred while saving barometer results")
            val view = (context as Activity).findViewById<View>(android.R.id.content)
            val snackbar = Snackbar.make(view, "Error. An error occurred while saving barometer results-", Snackbar.LENGTH_LONG)
            snackbar.setAction("Close") {
                snackbar.dismiss()
            }
            snackbar.show()
        }
    }

    fun stopLogging() {

        try {
            sensorManager.unregisterListener(this)

            writeIMU()
            writeMagnetometer()
            writeBarometer()

        } catch(e: Exception){
            Log.e("Error", "An error occurred while saving motion sensors results")
            val view = (context as Activity).findViewById<View>(android.R.id.content)
            val snackbar = Snackbar.make(view, "Error. An error occurred while saving motion sensors results", Snackbar.LENGTH_LONG)
            snackbar.setAction("Close") {
                snackbar.dismiss()
            }
            snackbar.show()
        }
    }
    // TODO: Implement this when we know accuracy parameters
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }
}