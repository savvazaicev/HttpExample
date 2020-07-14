package com.example.httpexample.connection

import com.example.httpexample.model.Book

interface Connection {
    fun getBooks(): MutableList<Book>
    fun addBook(book: String)
    fun editBook(book: String, id: String)
    fun removeBook(id: String)
}