package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener{
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttonSearchId = findViewById<Button>(R.id.search_button)
        val mediaLibraryButtonId = findViewById<Button>(R.id.media_library_button)
        val settingsButtonId = findViewById<Button>(R.id.settings_button)

        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val buttonSearchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(buttonSearchIntent)
            } }
        buttonSearchId.setOnClickListener(buttonClickListener)


        mediaLibraryButtonId.setOnClickListener {
            val mediaLibraryButtonIntent = Intent(this@MainActivity, MediaLibraryActivity::class.java)
            startActivity(mediaLibraryButtonIntent)
        }

        settingsButtonId.setOnClickListener(this@MainActivity)
    }

    override fun onClick(p0: View?) {when (p0?.id) {
        R.id.settings_button -> {
            val settingsButtonIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsButtonIntent )
        }
    }

            }

        }




