package com.katja.proseccopong

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EnterNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val  GameChoice: String? = intent.getStringExtra("Game Choice")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_name)
        val GameMode:TextView = findViewById(R.id.GameModeTextView)
        GameMode.text=GameChoice

        val editTextName: EditText = findViewById(R.id.editTextName)

        val buttonPlay: Button = findViewById(R.id.buttonPlay)

        buttonPlay.setOnClickListener {
            val playerName = editTextName.text.toString()
            when (GameChoice) {
                "Prosecco Pong" -> {
                    val intent = Intent(this, ProseccoGameViewActivity::class.java)
                    intent.putExtra("PLAYER_NAME", playerName)
                    startActivity(intent)
                }

                "Classic Pong" -> {
                    val intent = Intent(this, ClassicGameViewActivity::class.java)
                    intent.putExtra("PLAYER_NAME", playerName)
                    startActivity(intent)
                }
            }


        }
    }
}
