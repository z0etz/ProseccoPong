package com.katja.proseccopong

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlin.math.sqrt

class Ball(private val gameView: GameView, val context: Context, var posX: Float, var posY: Float, var size: Float, var speedX: Float, var speedY: Float, var platformTop: Float, var color: Int = R.color.gold) {

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.gold) // Sätt färgen på bollen
    }

    var ballOutToastShown = false

    // Metod för att kontrollera bollens kollision med spelområdets gränser
    fun checkbounders(bounds: Rect, context: Context) {
        val leftBound = bounds.left + size
        val rightBound = bounds.right - size
        val topBound = bounds.top + size
        val bottomBound = bounds.bottom - size

        // Hantera bollens kollision med vänster och höger kant av spelområdet
        if (posX - size < leftBound || posX + size > rightBound) {
            speedX *= -1
            posX += speedX * 2
        }

        // Hantera bollens kollision med överkanten av spelområdet
        if (posY - size < topBound) {
            speedY *= -1
            posY += speedY * 2
        }

        // Hantera bollens kollision med nederkanten (plattformens nivå) av spelområdet
        if (posY + size / 2 > bounds.bottom - platformTop) {
            if (gameView.ballDown()) {
                if (!ballOutToastShown) {
                    (context as Activity).runOnUiThread {
                        val toast = "Bollen är ute"
                        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
                    }
                    gameView.gameEnd()
                    ballOutToastShown = true
                }
            }
        }
    }

    // Egenskap för att hämta eller sätta bollens hastighet
    open var speed: Float
        get() = sqrt(speedX * speedX + speedY * speedY)
        set(value) {
            if (speed != 0f) {
                val factor = value / speed
                speedX *= factor
                speedY *= factor
            }
        }

    // Metod för att uppdatera bollens position baserat på dess hastighet
    fun update() {
        posY += speedY
        posX += speedX
    }

    // Metod för att rita bollen på given Canvas
    fun draw(canvas: Canvas) {
        canvas.drawCircle(posX, posY, size, paint) // Rita cirkelformad boll på Canvas
    }
}
