package com.katja.proseccopong

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.katja.proseccopong.databinding.ActivityInfoPageBinding

class InfoPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInfoPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainmenubutton: Button = binding.buttonMainMenu

        mainmenubutton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}