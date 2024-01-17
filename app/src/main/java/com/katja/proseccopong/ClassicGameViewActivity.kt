package com.katja.proseccopong

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.katja.proseccopong.databinding.ActivityClassicGameViewBinding

class ClassicGameViewActivity : AppCompatActivity(), SurfaceHolder.Callback {

    lateinit var binding: ActivityClassicGameViewBinding
    lateinit var classicGameView: ClassicGameView

    // Skapa SharedPreferences-objekt
    private val sharedPreferences by lazy {
        getSharedPreferences("game_scores", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassicGameViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playerName = intent.getStringExtra("PLAYER_NAME")
        classicGameView = ClassicGameView(this, this, sharedPreferences)
        classicGameView.setPlayerName(playerName ?: "")

        binding.surfaceClassicGameView.holder.addCallback(classicGameView)
        setContentView(classicGameView)
    }

    override fun onDestroy() {
        super.onDestroy()
        classicGameView.saveScore() // Spara poängen när aktiviteten förstörs
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Implementera vid behov
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Implementera vid behov
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Implementera vid behov
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        classicGameView.gameEnd()
    }

}