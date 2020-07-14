package com.example.httpexample.utils

import com.example.httpexample.model.Book
import com.example.httpexample.utils.Constants.ID
import com.example.httpexample.utils.Constants.TITLE
import org.json.JSONArray

fun String.tooBookList(): MutableList<Book> {
    val books = mutableListOf<Book>()
    val json = JSONArray(this)
    for (i in 0 until json.length()) {
        val jsonBook = json.getJSONObject(i)
        val id = jsonBook.getInt(ID)
        val title = jsonBook.getString(TITLE)
        books.add(Book(id, title))
    }
    return books
}