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

    lateinit var backgroundMusicPlayer: MediaPlayer // Declare mediaplayer for background music
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.theme_song) // Inizialize mediaplayer for background music
        backgroundMusicPlayer.isLooping = true
        backgroundMusicPlayer.start()


        val bClassicPong: Button = binding.buttonClassicGame
        val bProseccoPong: Button = binding.buttonProseccoGame
        val bHighscore: Button = binding.buttonHighscore


        bClassicPong.setOnClickListener {

            backgroundMusicPlayer.setVolume(0.1f, 0.1f) // Lower the volume in game mode

            val toast = "Classic Pong"
            Toast.makeText(this,toast, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, EnterNameActivity::class.java)
            intent.putExtra("Game Choice", toast);
            startActivity(intent)
        }


        bProseccoPong.setOnClickListener {

            backgroundMusicPlayer.setVolume(0.1f, 0.1f)
            val toast = "Prosecco Pong"
            Toast.makeText(this,toast, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, EnterNameActivity::class.java)
            intent.putExtra("Game Choice", toast)
            startActivity(intent)
        }

        bHighscore.setOnClickListener {

            backgroundMusicPlayer.setVolume(1.0f, 1.0f)

            val intent = Intent(this, HighscoreActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusicPlayer.release()
    }
}
