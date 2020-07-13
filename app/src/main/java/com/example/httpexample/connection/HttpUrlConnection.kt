package com.example.httpexample.connection

import androidx.annotation.WorkerThread
import com.example.httpexample.Constants.DELETE
import com.example.httpexample.Constants.GET
import com.example.httpexample.Constants.PATCH
import com.example.httpexample.Constants.POST
import com.example.httpexample.model.Book
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
private const val ID = "id"

object HttpUrlConnection {

    @WorkerThread
    fun getBooks(): MutableList<Book> {
        val httpUrlConnection: HttpURLConnection = configureHttpURLConnection(GET)
        try {
            if (httpUrlConnection.responseCode != HttpURLConnection.HTTP_OK) return mutableListOf()

            val streamReader = InputStreamReader(httpUrlConnection.inputStream)
            var text = ""
            streamReader.use { text = it.readText() }

            val books = mutableListOf<Book>()
            val json = JSONArray(text)
            for (i in 0 until json.length()) {
                val jsonBook = json.getJSONObject(i)
                val id = jsonBook.getInt(ID)
                val title = jsonBook.getString(TITLE)
                books.add(Book(id, title))
            }
            return books
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            httpUrlConnection.disconnect()
        }
        return mutableListOf()
    }

    @WorkerThread
    fun addBook(book: String) {
        val httpUrlConnection = configureHttpURLConnection(POST)
        val body = JSONObject().apply {
            put(TITLE, book)
        }
        try {
            OutputStreamWriter(httpUrlConnection.outputStream).use {
                it.write(body.toString())
            }
            httpUrlConnection.responseCode
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            httpUrlConnection.disconnect()
        }
    }

    @WorkerThread
    fun editBook(book: String, id: String) {
        val httpUrlConnection = configureHttpURLConnection(PATCH, id)
        val body = JSONObject().apply {
            put(TITLE, book)
        }
        try {
            OutputStreamWriter(httpUrlConnection.outputStream).use {
                it.write(body.toString())
            }
            httpUrlConnection.responseCode
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            httpUrlConnection.disconnect()
        }
    }

    @WorkerThread
    fun removeBook(id: String) {
        val httpUrlConnection = configureHttpURLConnection(DELETE, id)
        try {
            httpUrlConnection.responseCode
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            httpUrlConnection.disconnect()
        }
    }

    private fun configureHttpURLConnection(query: String, id: String? = null): HttpURLConnection {
        var url = "$ENDPOINT$BOOKS_URI"
        if (id != null) url += "/$id"
        val httpUrlConnection = URL(url).openConnection() as HttpURLConnection
        with(httpUrlConnection) {
            when (query) {
                GET -> doInput = true
                POST, PATCH -> {
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                }
                DELETE -> doOutput = true
            }
            connectTimeout = 10000 // 10 seconds
            requestMethod = query
        }
        return httpUrlConnection
    }
}