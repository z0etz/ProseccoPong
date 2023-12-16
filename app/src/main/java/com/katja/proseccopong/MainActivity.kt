package com.katja.proseccopong

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.katja.proseccopong.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bClassicPong: Button = binding.buttonClassicGame
        val bProseccoPong: Button = binding.buttonProseccoGame
        val bHighscore: Button = binding.buttonHighscore


        bClassicPong.setOnClickListener {
            val toast = "Classic Pong"
            Toast.makeText(this,toast, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, EnterNameActivity::class.java)
            intent.putExtra("Game Choice", toast);
            startActivity(intent)
        }


        bProseccoPong.setOnClickListener {
            val toast = "Prosecco Pong"
            Toast.makeText(this,toast, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, EnterNameActivity::class.java)
            intent.putExtra("Game Choice", toast)
            startActivity(intent)
        }

        bHighscore.setOnClickListener {
            val intent = Intent(this, HighscoreActivity::class.java)
            startActivity(intent)
        }


    }
}
