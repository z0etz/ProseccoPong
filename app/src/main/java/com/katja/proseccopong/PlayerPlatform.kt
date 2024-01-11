package com.katja.proseccopong
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect


class PlayerPlatform(
    val context: Context,
    var width: Float,
    var height: Float,
    var speedX: Float = 0f,
    var platformLevel: Float,
    color: Int = Color.BLUE,

    ) {
    private var glassesBroken: Int = 0


    var posX: Float = 0f
    var posY: Float = 0f


    private val paint = Paint().apply { this.color = color }
    fun initialize(viewWidth: Int, viewHeight: Int) {
        posX = (viewWidth / 2 - width / 2).toFloat() // Center horizontally
        posY =
            (viewHeight - height - platformLevel).toFloat() // The bar is raised a little bit from the bottom, by the platformLevel variable
    }



        fun checkBounds(bounds: Rect) {
            if (posX <= bounds.left) {
                posX = bounds.left.toFloat() // Limit to the left edge
            } else if (posX + width >= bounds.right) {
                posX = (bounds.right - width).toFloat() // Limit to the right edge
            }
        }

        fun update() {
            posX += speedX
        }

        fun moveLeft() {
            posX -= speedX // Adjust the position to move left
        }

        fun moveRight() {
            posX += speedX // Adjust the position to move right
        }

        fun draw(canvas: Canvas) {
            canvas.drawRect(posX, posY, posX + width, posY + height, paint)
        }
    }

