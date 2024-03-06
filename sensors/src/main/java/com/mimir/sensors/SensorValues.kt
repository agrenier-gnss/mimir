package com.mimir.sensors

import android.location.Location

// =================================================================================================

enum class SensorType(val value: Int){
    // Values for type retrieved from Android Sensor types, as defined in https://developer.android.com/reference/android/hardware/Sensor

    // Android Sensor types
    TYPE_ACCELEROMETER(1),
    TYPE_GYROSCOPE(4),
    TYPE_MAGNETIC_FIELD(2),
    TYPE_ACCELEROMETER_UNCALIBRATED(35),
    TYPE_GYROSCOPE_UNCALIBRATED(16),
    TYPE_MAGNETIC_FIELD_UNCALIBRATED(14),
    TYPE_PRESSURE(6),
    TYPE_HEART_RATE(21),
    TYPE_STEP_DETECTOR(18),
    TYPE_STEP_COUNTER(19),

    // GLOBAL TYPE (for settings only)
    TYPE_GNSS(100),
    TYPE_IMU(200),
    TYPE_HEALTH(300),
    TYPE_STEPS(400),
    TYPE_BLUETOOTH(500),

    // GNSS
    TYPE_GNSS_LOCATION(101),
    TYPE_GNSS_MEASUREMENTS(102),
    TYPE_GNSS_MESSAGES(103),
    TYPE_FUSED_LOCATION(104),

    // Specific, to target a specific model sensor
    TYPE_SPECIFIC_ECG(301),
    TYPE_SPECIFIC_PPG(302),
    TYPE_SPECIFIC_GSR(303)

}