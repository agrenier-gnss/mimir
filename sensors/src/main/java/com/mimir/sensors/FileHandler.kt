package com.mimir.sensors

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class FileHandler (context: Context, looper: Looper): Handler(looper) {

    //var mFileWriter: BufferedWriter
    //var file : File
    lateinit var mOutputStream : OutputStream

    val mSensorsResults = mutableListOf<String>()

    // ---------------------------------------------------------------------------------------------

    init {

        // Creating the log file
        val date = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val formatted = date.format(formatter)

        // Defining Download directory
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "mimir_log_$formatted")
            put(MediaStore.Downloads.MIME_TYPE, "text/txt")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues) as Uri
        mOutputStream = context.contentResolver.openOutputStream(uri) as OutputStream

        // Write basic header
        writeToFile(getHeader())
    }

    // ---------------------------------------------------------------------------------------------

    fun getHeader() : String{

        var str = ""

        str += "#\n"
        str += "# Header Description:\n"
        str += "#\n"
        str += "# Version: " + "1.0" + " Platform: " + Build.MANUFACTURER + " Model: " + Build.MODEL + "\n"
        str += "#"

        return str
    }

    // ---------------------------------------------------------------------------------------------

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)

        // Buffer messages to avoid constant interaction with file
        mSensorsResults.add(msg.obj as String)
        if(mSensorsResults.size > 100) {
            for(str in mSensorsResults){
                writeToFile(str)
            }
            mOutputStream.flush()
            mSensorsResults.clear()
        }
    }

    // ---------------------------------------------------------------------------------------------

    fun writeToFile(mobject : String ){
        //Log.d("FileHandler", "Message written to file")
        mOutputStream.write(mobject.toByteArray())
        mOutputStream.write("\n".toByteArray())
    }

    // ---------------------------------------------------------------------------------------------

    fun closeFile(){

        // Write everything still in buffer before closing
        for(str in mSensorsResults){
            writeToFile(str)
        }
        mOutputStream.flush()
        mSensorsResults.clear()

        mOutputStream.close()
    }

}