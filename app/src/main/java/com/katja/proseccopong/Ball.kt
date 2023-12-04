package com.katja.proseccopong

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.widget.Toast
import androidx.core.content.ContextCompat

class Ball(private val gameView: ClassicGameView,
    val context: Context, var posX: Float,
    var posY: Float,
    var size: Float,
    var speedX: Float,
    var speedY: Float,
    var color: Int = R.color.pink
) {

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.pink)
    }


    fun checkbounders(bounds: Rect, context: Context) {

        if (posX - size < bounds.left || posX - size > bounds.right) {
            speedX *= -1
//            posX = posX + speedX * 2
            // TODO: Flytta funktionsanropet nedan till när bollen studdsar mot spelbrickan istället
            // för väggarna (placerat här tillfälligt för testning).
            ClassicGameView.addPoints()

        }
        if (posY - size < bounds.top) {
            speedY *= -1
            posY = posY + 2
            speedY *= 1.1f
            speedX *= 1.1f

        }
        if (posY - size > bounds.bottom) {

            //Save score and sout for debug
            gameView.saveScore()
            println(ScoreList)
            (context as Activity).runOnUiThread {
                val toast = "Ball is out"
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
            }
            gameView.gameEnd()
        }
    }

    fun update() {
        posY += speedY
        posX += speedX
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(posX, posY, size, paint)
    }
}