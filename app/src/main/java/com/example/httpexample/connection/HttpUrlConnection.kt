package com.example.httpexample.connection

import androidx.annotation.WorkerThread
import com.example.httpexample.model.Book
import com.example.httpexample.utils.Constants.BOOKS_URI
import com.example.httpexample.utils.Constants.DELETE
import com.example.httpexample.utils.Constants.ENDPOINT
import com.example.httpexample.utils.Constants.GET
import com.example.httpexample.utils.Constants.PATCH
import com.example.httpexample.utils.Constants.POST
import com.example.httpexample.utils.Constants.TITLE
import com.example.httpexample.utils.tooBookList
import org.json.JSONObject
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


object HttpUrlConnection : Connection {

    @WorkerThread
    override fun getBooks(): MutableList<Book> {
        val httpUrlConnection: HttpURLConnection = configureHttpURLConnection(GET)
        try {
            if (httpUrlConnection.responseCode != HttpURLConnection.HTTP_OK) return mutableListOf()

            val streamReader = InputStreamReader(httpUrlConnection.inputStream)
            var text = ""
            streamReader.use { text = it.readText() }

            return text.tooBookList()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            httpUrlConnection.disconnect()
        }
        return mutableListOf()
    }

    @WorkerThread
    override fun addBook(book: String) {
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
    override fun editBook(book: String, id: String) {
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
    override fun removeBook(id: String) {
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