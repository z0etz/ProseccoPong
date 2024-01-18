package com.katja.proseccopong
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect


class PlayerPlatform(val context: Context, var width: Float, var height: Float, var speedX: Float = 0f, var platformLevel: Float, color: Int = Color.BLUE, ) {

    private var glassesBroken: Int = 0
    private val paint = Paint().apply { this.color = color }

    var posX: Float = 0f
    var posY: Float = 0f

    fun initialize(viewWidth: Int, viewHeight: Int) {
        posX = (viewWidth / 2 - width / 2).toFloat() // Centrera horisontellt
        posY = (viewHeight - height - platformLevel).toFloat() // Stången är lite höjd över botten, med hjälp av variabeln plattformNivå
    }

        fun checkBounds(bounds: Rect) {
            if (posX <= bounds.left) {
                posX = bounds.left.toFloat() // Begränsa till vänster kant
            } else if (posX + width >= bounds.right) {
                posX = (bounds.right - width).toFloat() // Begränsa till höger kant
            }
        }

        fun update() {
            posX += speedX
        }

        fun moveLeft() {
            posX -= speedX
        }

        fun moveRight() {
            posX += speedX
        }

        fun draw(canvas: Canvas) {
            canvas.drawRect(posX, posY, posX + width, posY + height, paint)
        }
    }

