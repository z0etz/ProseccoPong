package com.katja.proseccopong

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ClassicHighscoreActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classic_highscore)

        val button = findViewById<Button>(R.id.HS_main_menu_btn)
        button.setOnClickListener{
            val Intent = Intent (this,MainActivity :: class.java)
            startActivity(Intent)
        }
        

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)


        val adapter = ClassicHighScoreAdapter(highScores = listOf())

// Set the adapter to the RecyclerView
        recyclerView.adapter = adapter


        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}