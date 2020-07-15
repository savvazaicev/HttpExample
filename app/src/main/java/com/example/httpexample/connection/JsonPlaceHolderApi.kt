package com.example.httpexample.connection

import com.example.httpexample.model.Book
import retrofit2.Call
import retrofit2.http.*


interface JsonPlaceHolderApi {
    @GET("/books")
    fun getBooks(): Call<MutableList<Book>>

    @POST("/books")
    fun addBook(@Body book: Book): Call<Book>

    @PATCH("/books/{id}")
    fun editBook(@Path("id") id: String, @Body book: Book): Call<Book>

    @DELETE("/books/{id}")
    fun deleteBook(@Path("id") id: String): Call<Void>
}