package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.main).applySystemBarsPadding()

        // Явные Intent - открываем конкретные экраны своего приложения
        findViewById<MaterialButton>(R.id.search_button).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.media_library_button).setOnClickListener {
            startActivity(Intent(this, MediaLibraryActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.settings_button).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
