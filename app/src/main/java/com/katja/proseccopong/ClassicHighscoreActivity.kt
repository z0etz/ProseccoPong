package com.katja.proseccopong

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ClassicHighscoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classic_highscore)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

// Create an instance of your adapter
        val adapter = HighScoreAdapter(highScores = listOf())

// Set the adapter to the RecyclerView
        recyclerView.adapter = adapter

// Set LayoutManager (you can customize this based on your needs)
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}