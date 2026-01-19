package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class SettingsActivity : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
       enableEdgeToEdge()
        super.onCreate(savedInstanceState
        )
        setContentView(R.layout.activity_settings)

        val rootView = findViewById<View>(R.id.settings_root)


        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        val settingsButtonBackId = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.settings_button_back)

    settingsButtonBackId.setNavigationOnClickListener{
        finish()
    }
    }

}