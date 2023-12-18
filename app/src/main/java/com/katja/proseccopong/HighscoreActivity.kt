package com.katja.proseccopong

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katja.proseccopong.databinding.ActivityHighscoreBinding

class HighscoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHighscoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val deleteScore = application as MyApplication
        super.onCreate(savedInstanceState)
        binding = ActivityHighscoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainmenubutton: Button = binding.buttonMainMenu

        mainmenubutton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Sortera listan baserat på poäng i fall det inte är sorterat ännu
        ScoreList.scoreList.sortByDescending { it.score }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewClassic)
        val adapter = HighScoreAdapter(ScoreList.scoreList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val buttonDelete = binding.btnDelete // Change this to your ImageButton ID
        buttonDelete.setOnClickListener {
            deleteScore.clearAllData()
            recreate() // Återskapar den nuvarande aktiviteten
        }
    }
}
