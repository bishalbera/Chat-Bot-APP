package com.example.chatbotapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val question = findViewById<EditText>(R.id.etQuestion)
        val btnSubmit = findViewById<Button>(R.id.btnsubmit)
        val textResponse = findViewById<TextView>(R.id.tvResponse)

        btnSubmit.setOnClickListener {
            val question = question.text.toString().trim()
            Toast.makeText(this, "question", Toast.LENGTH_SHORT).show()
            if(question.isNotEmpty()){
                getresponse(question) {response ->
                    runOnUiThread{
                        textResponse.text = response
                    }

                }
            }
        }
    }

    private fun getresponse(question: String, callback: (String) -> Unit) {
        val apikey = "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"
        val url = "https://api.openai.com/v1/completions"

        val requestBody="""
            {
            "model": "text-davinci-003",
            "prompt": "$question",
            "max_tokens": 7,
            "temperature": 0
  
            }
        """.trimIndent()
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apikey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error","API failed",e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body=response.body?.string()
                if (body != null) {
                    Log.v("data",body)
                }
                else{
                    Log.v("data","empty")
                }
                val jsonObject= JSONObject(body)
                val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
                val textResult=jsonArray.getJSONObject(0).getString("text")
                callback(textResult)
            }
        })
    }
}