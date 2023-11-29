package com.katja.proseccopong

data class  HighScoreItem(val rank: Int, val score: Int, val playerName: String)

fun  main(){
    val highScores = listOf(
        HighScoreItem(rank = 1, score = 1000, playerName = "Player1"),
        HighScoreItem(rank = 2, score = 980, playerName = "Player2"))

    for (score in highScores){
        println("Rank: ${score.rank}, Score: ${score.score}, Player Name: ${score.playerName}")
    }
}




