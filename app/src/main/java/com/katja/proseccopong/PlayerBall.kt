package com.katja.proseccopong


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect


class PlayerBall(val context: Context,
    var width: Float,
    var height: Float,
    var speedX: Float = 0f,
    var speedY: Float,
    color: Int = Color.BLUE,

) {

    var posX: Float =0f
    var posY: Float = 0f

    private val paint = Paint().apply { this.color = color }
    fun initialize(viewWidth: Int, viewHeight: Int) {
        posX = (viewWidth / 2 - width / 2).toFloat() // Center horizontally
        posY = (viewHeight - height).toFloat() // Positioned at the bottom
    }
    fun checkBounds(bounds: Rect) {
        if (posX <= bounds.left) {
            posX = bounds.left.toFloat() // Limit to the left edge
        } else if (posX + width >= bounds.right) {
            posX = (bounds.right - width).toFloat() // Limit to the right edge
        }
    }

    fun update() {
        posY += speedY
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