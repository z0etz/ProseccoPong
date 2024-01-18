package com.katja.proseccopong

import android.annotation.SuppressLint
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

        val recyclerViewClassic: RecyclerView = findViewById(R.id.recyclerViewClassic)
        val adapterClassic = HighScoreAdapter(ScoreList.scoreList.filter { it.classic })
        recyclerViewClassic.adapter = adapterClassic
        recyclerViewClassic.layoutManager = LinearLayoutManager(this)

        val recyclerViewProsecco: RecyclerView = findViewById(R.id.recyclerViewProcesso)
        val adapterProsecco = HighScoreAdapter(ScoreList.scoreList.filter { !it.classic })
        recyclerViewProsecco.adapter = adapterProsecco
        recyclerViewProsecco.layoutManager = LinearLayoutManager(this)


        val buttonDelete = binding.btnDelete
        buttonDelete.setOnClickListener {
            deleteScore.clearAllData()
            recreate() // Återskapar den nuvarande aktiviteten
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
