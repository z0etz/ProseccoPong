package com.katja.proseccopong

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
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
        val leftBound = bounds.left + size
        val rightBound = bounds.right - size
        val topBound = bounds.top + size
        val bottomBound = bounds.bottom - size

        if (posX - size < leftBound || posX + size > rightBound) {
            speedX *= -1
            posX += speedX * 2
        }

        if (posY - size < topBound ) {
            speedY *= -1
            posY += speedY * 2
        }
        if (posY - size > bottomBound) {

            //Save score and sout for debug

            gameView.saveScore()
            println(ScoreList)
//            (context as Activity).runOnUiThread {
//                val toast = "Ball is out"
//                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
//            }
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