package com.example.httpexample.connection

import com.example.httpexample.model.Book
import com.example.httpexample.utils.Constants.ENDPOINT
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitConnection : Connection {

    private val jsonPlaceHolderApi: JsonPlaceHolderApi by lazy { createJsonPlaceHolderApi() }

    override fun getBooks(): MutableList<Book> {
        val call = jsonPlaceHolderApi.getBooks()
        return call.execute().body() ?: mutableListOf()
    }

    override fun addBook(title: String) {
        val book = Book(title = title)
        jsonPlaceHolderApi.addBook(book).execute()
    }

    override fun editBook(title: String, id: String) {
        val book = Book(title = title)
        jsonPlaceHolderApi.editBook(id, book).execute()
    }

    override fun removeBook(id: String) {
        jsonPlaceHolderApi.deleteBook(id).execute()
    }

    private fun createJsonPlaceHolderApi(): JsonPlaceHolderApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(JsonPlaceHolderApi::class.java)
    }
}