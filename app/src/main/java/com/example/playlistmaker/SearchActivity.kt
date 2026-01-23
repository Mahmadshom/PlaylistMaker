package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {
    private lateinit var clearButton: ImageButton
    @SuppressLint("ClickableViewAccessibility", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seach)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_activity_id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchButtonBackId = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.search_button_back)
        var searchEditText = findViewById<EditText>(R.id.search_edit_textId)
        clearButton = findViewById(R.id.button_clear_id)



        clearButton.setOnClickListener {
            searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }



        searchButtonBackId.setNavigationOnClickListener {
            finish()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)

    }
        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            val searchEditText = findViewById<EditText>(R.id.search_edit_textId)
            outState.putString("saved_text", searchEditText.text.toString())
        }

        override fun onRestoreInstanceState(savedInstanceState: Bundle) {
            super.onRestoreInstanceState(savedInstanceState)
            val savedText = savedInstanceState.getString("saved_text")
            val searchEditText = findViewById<EditText>(R.id.search_edit_textId)
            searchEditText.setText(savedText)
            clearButton.visibility = if (savedText.isNullOrEmpty()) View.GONE else View.VISIBLE
        }


    }

