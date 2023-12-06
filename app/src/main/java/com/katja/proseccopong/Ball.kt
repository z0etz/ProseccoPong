package com.katja.proseccopong

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.widget.Toast
import androidx.core.content.ContextCompat

class Ball(private val gameView: ClassicGameView,
    val context: Context,
    var posX: Float,
    var posY: Float,
    var size: Float,
    var speedX: Float,
    var speedY: Float,
    var platformTop: Float,
    var color: Int = R.color.pink
) {

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.pink)
    }
    var ballOutToastShown = false

    fun checkbounders(bounds: Rect, context: Context) {

        if (posX - size < bounds.left || posX - size > bounds.right) {
            speedX *= -1
            posX = posX + speedX * 2

        }
        if (posY - size < bounds.top) {
            speedY *= -1
            posY = posY + speedY * 2

        }
        if (posY + size / 2 > bounds.bottom - platformTop) {
            // When the ball reaches the platform level, check it it collides or goes out
            if(gameView.ballDown()) {
                if (!ballOutToastShown) { // Check if the toast has been shown
                    (context as Activity).runOnUiThread {
                        val toast = "Ball is out"
                        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
                    }
                    gameView.gameEnd()
                    ballOutToastShown = true // Set the flag to true once the toast is shown
                }
            }
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