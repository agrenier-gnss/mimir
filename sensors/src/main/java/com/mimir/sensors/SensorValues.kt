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

    // GNSS
    TYPE_GNSS_LOCATION(100),
    TYPE_GNSS_MEASUREMENTS(101),
    TYPE_GNSS_MESSAGES(102),

    // BLE
    TYPE_BLUETOOTH(200)
}