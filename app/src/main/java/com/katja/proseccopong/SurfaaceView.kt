package com.katja.proseccopong

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import com.katja.proseccopong.databinding.ActivitySurfaaceViewBinding

class SurfaaceView : AppCompatActivity(),SurfaceHolder.Callback {
    lateinit var binding :ActivitySurfaaceViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySurfaaceViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var gameView = GameView(this)
        binding.surfaceView.holder.addCallback(gameView)
                setContentView(gameView)



    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        TODO("Not yet implemented")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        TODO("Not yet implemented")
    }
}