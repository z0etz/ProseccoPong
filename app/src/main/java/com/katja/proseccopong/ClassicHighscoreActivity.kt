package com.katja.proseccopong

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katja.proseccopong.databinding.ActivityClassicHighscoreBinding

class ClassicHighscoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassicHighscoreBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassicHighscoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val deleteScore = application as MyApplication
        val mainmenubutton: Button = binding.buttonMainMenu

        mainmenubutton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Sortera listan baserat på poäng i fall det inte är sorterat ännu
        ScoreList.scoreList.sortByDescending { it.score }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = ClassicHighScoreAdapter(ScoreList.scoreList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val buttonDelete = binding.imgBtnDelete // Change this to your ImageButton ID
        buttonDelete.setOnClickListener {
            deleteScore.clearAllData()
            recreate() // Återskapar den nuvarande aktiviteten
        }
    }

}
