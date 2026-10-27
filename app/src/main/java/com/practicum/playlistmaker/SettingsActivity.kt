package com.practicum.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViewById<View>(R.id.root).applySystemBarsPadding()

        findViewById<ImageButton>(R.id.back_button).setOnClickListener { finish() }

        // "Поделиться приложением" - неявный Intent ACTION_SEND + системный диалог выбора
        findViewById<TextView>(R.id.shareButton).setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            }
            startActivity(Intent.createChooser(sendIntent, null))
        }

        // "Написать в поддержку" - ACTION_SENDTO со схемой mailto: открывает только почтовые клиенты
        findViewById<TextView>(R.id.supportButton).setOnClickListener {
            val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))
            }
            safeStart(mailIntent)
        }

        // "Пользовательское соглашение" - ACTION_VIEW со ссылкой открывает браузер
        findViewById<TextView>(R.id.agreementButton).setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_url)))
            safeStart(browserIntent)
        }
    }

    // Если на устройстве нет подходящего приложения - не падаем, а показываем сообщение
    private fun safeStart(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.no_app_found, Toast.LENGTH_SHORT).show()
        }
    }
}
