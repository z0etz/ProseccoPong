package com.katja.proseccopong

object GameManager {
    var points = 0

    fun addPoints() {
        points++
    }

    fun resetPoints() {
        points = 0
    }

    fun incrementPoints(pointsToAdd: Int) {
        points += pointsToAdd
    }
}
