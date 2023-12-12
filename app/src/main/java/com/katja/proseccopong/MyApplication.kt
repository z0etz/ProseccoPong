package com.katja.proseccopong

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialisera ScoreList från SharedPreferences när appen startar
        val sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val scoreListJson = sharedPreferences.getString("score_list", "")
        if (scoreListJson?.isNotEmpty() == true) {
            ScoreList.scoreList.addAll(Gson().fromJson(scoreListJson, object : TypeToken<List<Score>>() {}.type))
        }
    }
    // Function to clear all data, including "score_list", from SharedPreferences
    fun clearAllData() {
        val sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // This will remove all data from SharedPreferences for "game_scores"
        editor.apply()

        // Additionally, clear the ScoreList
        ScoreList.scoreList.clear()
    }
}