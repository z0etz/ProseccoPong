package com.katja.proseccopong

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class EnterNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_name)

        val editTextName: EditText = findViewById(R.id.editTextName)
        val buttonPlay: Button = findViewById(R.id.buttonPlay)

        buttonPlay.setOnClickListener {
            val playerName = editTextName.text.toString()
            val intent = Intent(this, ClassicGameViewActivity::class.java)
            intent.putExtra("PLAYER_NAME", playerName)
            startActivity(intent)
        }
    }
}
