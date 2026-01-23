package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.switchmaterial.SwitchMaterial
import java.net.URI

class SettingsActivity : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
       enableEdgeToEdge()
        super.onCreate(savedInstanceState
        )
        setContentView(R.layout.activity_settings)

        val rootView = findViewById<View>(R.id.settings_root)
        val themeSwitch = findViewById<SwitchMaterial>(R.id.theme_switch)
        val shareApp = findViewById<View>(R.id.share_app)
        val writeSupport = findViewById<View>(R.id.write_support_id)
        val arrow_forward = findViewById<View>(R.id.arrow_forward_id)

        arrow_forward.setOnClickListener{
        val url = getString(R.string.agreement_url)
            val agreementIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
                startActivity(agreementIntent)
        }

       writeSupport.setOnClickListener {

        val email = getString(R.string.support_email)
           val subject = getString(R.string.support_subject)
           val body = getString(R.string.support_body)

           val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
               data = Uri.parse("mailto:")
               putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
               putExtra(Intent.EXTRA_SUBJECT, subject)
               putExtra(Intent.EXTRA_TEXT, body)

           }
           startActivity(supportIntent)
       }

        shareApp.setOnClickListener {
            val message = getString(R.string.text_share_app)
            val sentIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sentIntent,null)
            startActivity(shareIntent)

        }


        themeSwitch.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        themeSwitch.setOnCheckedChangeListener {_,isChecked ->
            if (isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }}

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

