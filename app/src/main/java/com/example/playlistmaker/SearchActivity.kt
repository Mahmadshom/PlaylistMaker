package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {
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
        val searchEditText = findViewById<EditText>(R.id.search_edit_textId)
var currentText = ""
        searchButtonBackId.setNavigationOnClickListener{
            finish()
        }
        searchEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                currentText = s.toString()
            val clearIcon = if (s.isNullOrEmpty()) 0 else R.drawable.clear_icon
        searchEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_icon_mini, 0, clearIcon, 0)

            }

            override fun afterTextChanged(s: Editable?) {}

        })


        searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
            val drawableEnd = searchEditText.compoundDrawables[2]
                if(drawableEnd != null && event.rawX >= (searchEditText.right - searchEditText.paddingRight- drawableEnd.bounds.width())){
                    searchEditText.text.clear()
                    return@setOnTouchListener true
                }
            }
            false
            }
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
        if (!savedText.isNullOrEmpty()) {
            searchEditText.setSelection(savedText.length)
    }



    }}
