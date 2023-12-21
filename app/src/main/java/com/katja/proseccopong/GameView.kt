package com.katja.proseccopong

interface GameView {

    fun ballDown() : Boolean

    fun gameEnd()
    fun incrementPoints()

    // Method to handle glass breakage event
    fun handleGlassBreakage() {
    }
}