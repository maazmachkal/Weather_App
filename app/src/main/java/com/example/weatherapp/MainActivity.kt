package com.example.weatherapp

import android.R.attr
import android.R.attr.data
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.location.Location
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.R.attr.path
import android.content.Context
import android.widget.Toast
import java.io.OutputStreamWriter
import java.lang.Exception
import android.os.Environment





class MainActivity : AppCompatActivity() {

    // weather url to get JSON
    var weather_url1 = ""

    // api id for url for https://www.weatherapi.com/
    //var api_id1 = "4d86ed5be06541b19d272510211910"

    // api id for url for https://www.weatherbit.io/
    var api_id1 = "11eeea77813f4005902dd7f428fe0c7b"

    private lateinit var textView: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // link the textView in which the
        // temperature will be displayed
        textView = findViewById(R.id.textView)

        // create an instance of the Fused
        // Location Provider Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Log.e("lat", weather_url1)

        // on clicking this button function to
        // get the coordinates will be called
        btVar1.setOnClickListener {
            Log.e("lat", "onClick")
            // function to find the coordinates
            // of the last location
            obtainLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtainLocation() {
        Log.e("lat", "function")
        // get the last location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // get the latitude and longitude
                // and create the http URL
                weather_url1 = "https://api.weatherbit.io/v2.0/current?" + "lat=" + location?.latitude + "&lon=" + location?.longitude + "&key=" + api_id1
                Log.e("lat", weather_url1.toString())
                // this function will
                // fetch data from URL
                getTemp()
            }
    }



    fun getTemp() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = weather_url1
        Log.e("lat", url)

        // Request a string response
        // from the provided URL.
        val stringReq = StringRequest(Request.Method.GET, url,
            { response ->
                Log.e("lat", response.toString())

                // get the JSON object
                val obj = JSONObject(response)

                // get the Array from obj of name - "data"
                val arr = obj.getJSONArray("data")
                Log.e("lat obj1", arr.toString())

                // get the JSON object from the
                // array at index position 0
                val obj2 = arr.getJSONObject(0)
                var latitude = weather_url1.split("lon=")[1]
                latitude = latitude.split("&")[0]
                var longitude = weather_url1.split("lat=")[1]
                longitude = longitude.split("&")[0]
                Log.e("lat obj2", obj2.toString())
                Log.e("latitude", latitude.toString())
                Log.e("longitude", longitude.toString())

                // set the temperature and the city
                // name using getString() function
                textView.text = obj2.getString("temp") + " deg Celcius in " + obj2.getString("city_name")
                textView2.text = obj2.getString("temp") + " deg Celcius in " + obj2.getString("city_name")
                textView2.text = "latitude: " + latitude.toString() + "   longitude: " + longitude.toString()
                try {
                    val root = Environment.getExternalStorageDirectory().toString()
                    val myDir = File("$root")
                    var FILE_NAME = myDir.toString() + "/" +"mytextfile.txt"
                    Toast.makeText(this, FILE_NAME,
                        Toast.LENGTH_LONG).show();
                    val fileout = openFileOutput(FILE_NAME, MODE_PRIVATE)
                    val outputWriter = OutputStreamWriter(fileout)
                    outputWriter.write(textView2.getText().toString())
                    outputWriter.close()

                    //display file saved message
                    Toast.makeText(
                        baseContext, "File saved successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val builder = AlertDialog.Builder(this)
                val text = "the tempraure is : " +obj2.getString("temp") + " deg Celcius and the lat: " +latitude.toString() + " and the long: "+longitude.toString()
                builder.setTitle("do you want to exit ?")
                builder.setMessage(text.toString())
                builder.setPositiveButton("yes",{ dialogInterface: DialogInterface, i: Int ->
                    finish()
                })
                builder.setNegativeButton("no",{ dialogInterface: DialogInterface, i: Int -> })
                builder.show()
            },
            // In case of any error
            { textView!!.text = "That didn't work!" })
        queue.add(stringReq)
    }
}