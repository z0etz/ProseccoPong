package com.katja.proseccopong

object GameManager {
    var points = 0

    val brickList = ArrayList<GlassBrick>()

    fun addPoints() {
        points++
    }

    fun resetPoints() {
        points = 0
    }

    fun incrementPoints(pointsToAdd: Int) {
        points += pointsToAdd
    }

    fun clearBricklist() {
        brickList.clear()
    }
}