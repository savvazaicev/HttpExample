package com.example.httpexample.connection

import com.example.httpexample.model.Book
import com.example.httpexample.utils.Constants.BOOKS_URI
import com.example.httpexample.utils.Constants.ENDPOINT
import com.example.httpexample.utils.Constants.TITLE
import com.example.httpexample.utils.tooBookList
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request


object OkHttp : Connection {

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    override fun getBooks(): MutableList<Book> {
        val request = Request.Builder()
            .url("$ENDPOINT$BOOKS_URI")
            .build()

        client.newCall(request).execute()
            .use { response -> return response.body!!.string().tooBookList() }
    }

    override fun addBook(book: String) {
        val formBody = FormBody.Builder()
            .add(TITLE, book)
            .build()

        val request = Request.Builder()
            .url("$ENDPOINT$BOOKS_URI")
            .post(formBody)
            .build()

        client.newCall(request).execute()
    }

    override fun editBook(book: String, id: String) {
        val formBody = FormBody.Builder()
            .add(TITLE, book)
            .build()

        val request = Request.Builder()
            .url("$ENDPOINT$BOOKS_URI/$id")
            .patch(formBody)
            .build()

        client.newCall(request).execute()
    }

    override fun removeBook(id: String) {
        val request = Request.Builder()
            .url("$ENDPOINT$BOOKS_URI/$id")
            .delete()
            .build()

        client.newCall(request).execute()
    }
}