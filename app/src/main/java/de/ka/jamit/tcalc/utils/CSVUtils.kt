package de.ka.jamit.tcalc.utils

import android.os.Environment
import android.widget.Toast
import com.opencsv.CSVReader
import java.io.File
import java.io.FileReader


/**
 * Offers utility functions for handling CSV export and import.
 *
 * Created by Thomas Hofmann on 08.07.20
 **/
class CSVUtils {

    fun importCSV(){
//        try {
//            val csvfile = File(Environment.getExternalStorageDirectory().toString() + "/csvfile.csv")
//            val reader = CSVReader(FileReader(csvfile.getAbsolutePath()))
//            var nextLine: Array<String>
//            while (reader.readNext().also { nextLine = it } != null) {
//                // nextLine[] is an array of values from the line
//                println(nextLine[0] + nextLine[1] + "etc...")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show()
//        }
    }
}