package com.example.httpexample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

private const val ENDPOINT =
    "http://192.168.100.7:3000"  // Im using json-server running on my localhost and device
private const val BOOKS_URI = "/books"
private const val TITLE = "title"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            val book = editText.text
            editText.text = null
            Thread {
                addBook(book.toString())
            }.start()
        }
        Thread {
            getBooksAndShowIt()
        }.start()
    }

    @WorkerThread
    fun getBooksAndShowIt() {
        var httpUrlConnection: HttpURLConnection? = null
        try {
            httpUrlConnection = URL(ENDPOINT + BOOKS_URI).openConnection() as HttpURLConnection
            httpUrlConnection.apply {
                connectTimeout = 10000 // 10 seconds
                requestMethod = "GET"
                doInput = true
            }
            if (httpUrlConnection.responseCode != HttpURLConnection.HTTP_OK) {
                // show error toast
                return
            }
            val streamReader = InputStreamReader(httpUrlConnection.inputStream)
            var text: String = ""
            streamReader.use {
                text = it.readText()
            }

            val books = mutableListOf<String>()
            val json = JSONArray(text)
            for (i in 0 until json.length()) {
                val jsonBook = json.getJSONObject(i)
                val title = jsonBook.getString(TITLE)
                books.add(title)
            }
            Handler(Looper.getMainLooper()).post {
                textView.text = books.reduce { acc, s -> "$acc\n$s" }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            httpUrlConnection?.disconnect()
        }
    }

    @WorkerThread
    fun addBook(book: String) {
        var httpUrlConnection: HttpURLConnection? = null
        try {
            httpUrlConnection = URL(ENDPOINT + BOOKS_URI).openConnection() as HttpURLConnection
            val body = JSONObject().apply {
                put(TITLE, book)
            }
            httpUrlConnection.apply {
                connectTimeout = 10000 // 10 seconds
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
            OutputStreamWriter(httpUrlConnection.outputStream).use {
                it.write(body.toString())
            }
            httpUrlConnection.responseCode
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            httpUrlConnection?.disconnect()
        }
        getBooksAndShowIt()
    }
}