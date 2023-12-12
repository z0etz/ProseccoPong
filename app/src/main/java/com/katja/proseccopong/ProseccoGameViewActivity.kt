package com.katja.proseccopong

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import com.katja.proseccopong.databinding.ActivityProseccoGameViewBinding

class ProseccoGameViewActivity : AppCompatActivity(), SurfaceHolder.Callback {

    lateinit var proseccoBinding: ActivityProseccoGameViewBinding
    lateinit var proseccoGameView: ProseccoGameView

    // Skapa SharedPreferences-objekt
    private val sharedPreferences by lazy {
        getSharedPreferences("game_scores", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        proseccoBinding = ActivityProseccoGameViewBinding.inflate(layoutInflater)
        setContentView(proseccoBinding.root)

        val playerName = intent.getStringExtra("PLAYER_NAME")
        proseccoGameView = ProseccoGameView(this, this, sharedPreferences)
        proseccoGameView.setPlayerName(playerName ?: "")

        proseccoBinding.surfaceProseccoGameView.holder.addCallback(proseccoGameView)
        setContentView(proseccoGameView)
    }

    override fun onDestroy() {
        super.onDestroy()
        proseccoGameView.saveScore() // Spara poängen när aktiviteten förstörs
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
}