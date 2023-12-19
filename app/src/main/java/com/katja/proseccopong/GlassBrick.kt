package com.katja.proseccopong

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class GlassBrick(
    private val gameView: GameView,
    val context: Context, val name: String, var width: Int,
    val numberFromMiddleX: Int,
    val numberFromTopY: Int, var rose: Boolean, var viewWidth: Float, var viewHeight: Float
) {

    var hasBeenHit = false
    var glassImage = ContextCompat.getDrawable(context, R.drawable.glas_prosecco)
    var height = width * 3
    var rotationAngle = 0f
    val topBrickLayoutOffset = 400

    private var leftBound: Int = 0
    private var topBound: Int = 0
    private var rightBound: Int = 0
    private var bottomBound: Int = 0

    private fun calculateBounds() {
        leftBound =
            viewWidth.toInt() / 2 + numberFromMiddleX * width + numberFromMiddleX * 10 - width / 2
        topBound = numberFromTopY * height - height / 2 + numberFromTopY * 10 + topBrickLayoutOffset
        rightBound =
            viewWidth.toInt() / 2 + numberFromMiddleX * width + numberFromMiddleX * 10 + width / 2
        bottomBound =
            numberFromTopY * height + height / 2 + numberFromTopY * 10 + topBrickLayoutOffset
    }

    fun draw(canvas: Canvas) {
        // Choose drawable
        if (!hasBeenHit) {
            if (!rose) {
                glassImage = ContextCompat.getDrawable(context, R.drawable.glas_prosecco)
            } else {
                glassImage = ContextCompat.getDrawable(context, R.drawable.glas_rose)
            }
        } else {
            glassImage = ContextCompat.getDrawable(context, R.drawable.glas_tomt)
        }

        calculateBounds()

        // Set position
        glassImage?.setBounds(leftBound, topBound, rightBound, bottomBound)

        if (rotationAngle == 0f) {
            glassImage?.draw(canvas)
        }
        // Rotation used once a brick is hit
        else {
            val x = numberFromMiddleX.toFloat() * width + numberFromMiddleX * 10
            val y = numberFromTopY.toFloat() * height + numberFromTopY * 10 + topBrickLayoutOffset
            glassImage?.let {
                canvas.save()
                canvas.rotate(rotationAngle, x, y)
                it.draw(canvas)
                canvas.restore()
            }
        }
    }

    fun handleCollision(ball: Ball) {
        if (!hasBeenHit) {
            val hitPointX = ball.posX - (leftBound + rightBound) / 2
            val hitPointY = ball.posY - (topBound + bottomBound) / 2

            // Calculate the normalized vector pointing from the center of the brick to the hit point
            val hitVectorLength = sqrt((hitPointX * hitPointX + hitPointY * hitPointY).toDouble()).toFloat()
            val normalizedHitVectorX = hitPointX / hitVectorLength
            val normalizedHitVectorY = hitPointY / hitVectorLength

            // Reflect the ball's velocity vector across the normalized hit vector
            val dotProduct = ball.speedX * normalizedHitVectorX + ball.speedY * normalizedHitVectorY
            val reflectionX = 2 * dotProduct * normalizedHitVectorX - ball.speedX
            val reflectionY = 2 * dotProduct * normalizedHitVectorY - ball.speedY

            // Update the ball's velocity with the reflection
            ball.speedX = reflectionX
            ball.speedY = reflectionY

            hasBeenHit = true
            glasHit()
        }
    }


    fun checkCollision(ball: Ball): Boolean {
        if (!hasBeenHit) {
            val brickRect = RectF(
                leftBound.toFloat(),
                topBound.toFloat(),
                rightBound.toFloat(),
                bottomBound.toFloat()
            )
            val ballRect = RectF(
                ball.posX - ball.size / 2,
                ball.posY - ball.size / 2,
                ball.posX + ball.size / 2,
                ball.posY + ball.size / 2
            )

            if (brickRect.intersect(ballRect)) {
                handleCollision(ball)
                return true
            }
        }
        return false


    }

    fun glasHit() {
        hasBeenHit = true
        // TODO: Lägg till mer logik för vad som ska hända när bricka träffas, efter att bilden roterat ska brickan bl.a. försvinna så småningom.
    }

    fun sufaceChanged(inputWidth: Float, inputHeight: Float, imageWidth: Int) {
        viewWidth = inputWidth
        viewHeight = inputHeight
        width = imageWidth
        height = imageWidth * 3

    }

}