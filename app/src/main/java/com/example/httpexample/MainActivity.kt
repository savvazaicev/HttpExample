package com.example.httpexample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.httpexample.connection.HttpUrlConnection.addBook
import com.example.httpexample.connection.HttpUrlConnection.editBook
import com.example.httpexample.connection.HttpUrlConnection.getBooks
import com.example.httpexample.connection.HttpUrlConnection.removeBook
import com.example.httpexample.model.Book
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launch { showBooks(getBooks()) }

        button.setOnClickListener {
            val name = bookName.text.toString()
            bookName.text = null

            launch {
                addBook(name)
                showBooks(getBooks())
            }
        }
        edit.setOnClickListener {
            val name = bookName.text.toString()
            val id = bookId.text.toString()
            bookName.text = null
            bookId.text = null

            launch {
                editBook(name, id)
                showBooks(getBooks())
            }
        }
        remove.setOnClickListener {
            val id = bookId.text.toString()
            bookId.text = null

            launch {
                removeBook(id)
                showBooks(getBooks())
            }
        }
    }

    private fun showBooks(books: MutableList<Book>) {
        Handler(Looper.getMainLooper()).post {
            booksTextView.text =
                books.map { "${it.id}.${it.title}" }.reduce { acc, s -> "$acc\n$s" }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }
}