package com.katja.proseccopong

import android.content.Intent
import android.media.MediaPlayer
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

        AudioManager.initialize(this, R.raw.theme_song) // AudioManager Singleton

        val bClassicPong: Button = binding.buttonClassicGame
        val bProseccoPong: Button = binding.buttonProseccoGame
        val bHighscore: Button = binding.buttonHighscore
        val bInfoPage: Button = binding.buttonInfoPage


        bClassicPong.setOnClickListener {
            val toast = "Classic Pong"
            // Om man vill ha en toast som anger spelläge, så kan den avkommenteras nedan.
//          Toast.makeText(this,toast, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, EnterNameActivity::class.java)
            intent.putExtra("Game Choice", toast)
            startActivity(intent)
        }

        bProseccoPong.setOnClickListener {
            val toast = "Prosecco Pong"
            // Om man vill ha en toast som anger spelläge, så kan den avkommenteras nedan.
//            Toast.makeText(this,toast, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, EnterNameActivity::class.java)
            intent.putExtra("Game Choice", toast)
            startActivity(intent)
        }

        bHighscore.setOnClickListener {
            val intent = Intent(this, HighscoreActivity::class.java)
            startActivity(intent)
        }

        bInfoPage.setOnClickListener {
            val intent = Intent(this, InfoPageActivity::class.java)
            startActivity(intent)
        }


    }



    override fun onDestroy() {
        super.onDestroy()
        AudioManager.release()
    }
}
