# Mimir

Mimir is an open-source Android app, designed for research purposes at Tampere University (TAU). 

Its main objectives are: 
  - Centralised sensors measurement logging inside one single app; 
  - Target both smartphone and smartwatch environments (WearOS 3.5/4.0)

Both smartphone and smartwatch apps are available on the repository, with a common logging library. Note that the apps are independant, and do not required to be installed on both companion devices to work. 

Currently the following sensors are supported for logging:

Smartphones & smartwatches:
- GNSS receiver: raw measurements, positions from GPS provider, navigation messages;
- Motion sensors: accelerometers, gyroscopes, magnetometers;
- Barometer sensor;
- Step counter & detector

Smartwatches:
- Health sensors: ECG, PPG, GSR 

The app targets devices with Android 14 / WearOS 4.0 (API 34), and has been tested on the following devices: 
- Google Pixel 7 (GP7)
- Samsung A52 5G (A52)
- Google Pixel Watch (GPW)
- Samsung Galaxy Watch 6 (GW6)

If you want to cite our work or see some of the sensors results from these devices, please refer to the following publications:

Grenier, A., Lohan, E.S., Ometov, A. and Nurmi, J., 2023. Towards Smarter Positioning through Analyzing Raw GNSS and Multi-Sensor Data from Android Devices: A Dataset and an Open-Source Application. Electronics, 12(23), p.4781. https://www.mdpi.com/2079-9292/12/23/4781 

## Sensor settings

Sensors can be enabled/disabled in the settings activity inside the app. For motion sensors, the uncalibrated sensor event will always be logged first if available on the device, as the event contains both uncalibrated/calibrated data. If not available, the app will revert to the calibrated event. If not sensor of this type is available, it will be discarded.

> [!WARNING]
> Only partial sensor logging is natively enabled on Samsung Galaxy Watch devices. Testing on the GW6 have shown that uncalibrated accelerometers and health sensors cannot be logged.

The sampling frequency can be selected per sensors, either 1Hz / 5Hz / 10Hz / 50 Hz / 100 Hz / 200 Hz / Inf (fastest). Note that these rates are approximate in Android, and might differs from the rates logged in the files. Interpolation to precise rates in post-processing might be required. 

## File location and structure

On smartphones, the files are saved in the external memory, by default in the Download folder, with the following name format ''log_mimir_[YYYYMMDDHHMMSS].txt''.

On Smartwatches, the files are logged in the app internal memory, by default in ./Data/Data/com.mobilewizards.logging_app/files/. 

> [!NOTE]
> File location are different due to an issue with current WearOS 4.0 version, for more details see the following issue https://issuetracker.google.com/issues/299174252. 

To extract the files from the smartwatch memory, the easiest is to use Android Studio, connect the device through wireless debugging and use the Device Explorer. This requires to enabled Developer Options on the device. You can refer to the following documentation https://developer.android.com/tools/adb.

## Specific case of the health sensors

Health sensors are only present on the smartwatch version of the app. To access the raw measurement of the sensors, the name of the sensor is explicitly specified inside the app (Google Pixel Watch uses the AFE4950). Thus for installation on other devices, this must modified in the code. 

As mentionned before, Samsung Galaxy Watch devices **DO NOT** allow access natively to any of the health sensors. 

> [!NOTE]
> On the Google Pixel Watch, ECG and GSR cannot be acquired concurently. Only ECG+PPG or PPG+GSR is possible.