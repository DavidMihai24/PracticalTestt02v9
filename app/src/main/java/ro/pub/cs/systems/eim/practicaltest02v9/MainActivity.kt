package ro.pub.cs.systems.eim.practicaltest02v9

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var wordEditText: EditText
    private lateinit var sizeEditText: EditText
    private lateinit var resultTextView: TextView
    private lateinit var receiver: BroadcastReceiver
    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v9_main)

        wordEditText = findViewById(R.id.wordEditText)
        sizeEditText = findViewById(R.id.sizeEditText)
        resultTextView = findViewById(R.id.resultTextView)
        val fetchButton = findViewById<Button>(R.id.fetchButton)

        fetchButton.setOnClickListener {
            val word = wordEditText.text.toString().trim()
            if (word.isNotEmpty()) {
                fetchAnagrams(word)
            }
        }

        setupBroadcastReceiver()
    }

    private fun setupBroadcastReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val anagrams = intent?.getStringExtra("anagrams") ?: "No anagrams found"
                resultTextView.text = anagrams
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter("com.example.ANAGRAM_UPDATE"))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    private fun fetchAnagrams(word: String) {
        val url = "http://www.anagramica.com/all/$word"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NetworkError", "Failed to fetch data", e)
                runOnUiThread { resultTextView.text = "Error: ${e.message}" }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("ServerError", "Server responded with error")
                        runOnUiThread { resultTextView.text = "Server Error" }
                    } else {
                        val responseData = response.body?.string()
                        val result = parseResult(responseData, sizeEditText.text.toString().toInt())
                        runOnUiThread {
                            resultTextView.text = result
                            LocalBroadcastManager.getInstance(this@MainActivity).sendBroadcast(Intent("com.example.ANAGRAM_UPDATE").apply {
                                putExtra("anagrams", result)
                            })
                        }
                    }
                }
            }
        })
    }

    private fun parseResult(jsonData: String?, minSize: Int): String {
        if (jsonData.isNullOrEmpty()) return "No data received"

        return try {
            val jsonObject = JSONObject(jsonData)
            val anagrams = jsonObject.getJSONArray("all")
            (0 until anagrams.length())
                .map { anagrams.getString(it) }
                .filter { it.length >= minSize }
                .joinToString(separator = "\n").also {
                    Log.d("ParsedAnagrams", it)
                }
        } catch (e: Exception) {
            Log.e("JSONError", "Failed to parse JSON", e)
            "Failed to parse data"
        }
    }
}
