package com.katja.proseccopong

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import com.katja.proseccopong.databinding.ActivitySurfaceViewBinding

class ClassicGameViewActivity : AppCompatActivity(),SurfaceHolder.Callback {
    lateinit var binding :ActivitySurfaceViewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySurfaceViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val classicGameView = ClassicGameView(this)
        binding.surfaceView.holder.addCallback(classicGameView)
                setContentView(classicGameView)



    }

    override fun surfaceCreated(holder: SurfaceHolder) {
       // TODO("Not yet implemented")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        //  TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        //  TODO("Not yet implemented")
    }
}