package com.example.httpexample.connection

import com.example.httpexample.model.Book

interface Connection {
    fun getBooks(): MutableList<Book>
    fun addBook(title: String)
    fun editBook(title: String, id: String)
    fun removeBook(id: String)
}