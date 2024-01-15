package com.katja.proseccopong

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.content.ContextCompat
import kotlin.math.sqrt

class GlassBrick(
    private val gameView: GameView,
    val context: Context, val name: String, var width: Int,
    val numberFromMiddleX: Int,
    val numberFromTopY: Int, var rose: Boolean, var viewWidth: Float, var viewHeight: Float
) {

    var hitTime: Long? = null
    var hasBeenHit = false
    var empty = false
    var glassImage = ContextCompat.getDrawable(context, R.drawable.glas_prosecco)
    var height = width * 3
    var rotationAngle = 0f
    var topBrickLayoutOffset = 400

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
        if (!empty) {
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

        val centerX = (leftBound + rightBound) / 2f
        val centerY = (topBound + bottomBound) / 2f

        if (rotationAngle == 0f) {
            glassImage?.draw(canvas)
        } else {
            glassImage?.let {
                canvas.save()
                canvas.rotate(rotationAngle, centerX, centerY)
                it.draw(canvas)
                canvas.restore()
            }
        }
    }

    fun handleCollision(ball: Ball) {
        if (!hasBeenHit) {

            rotationAngle = 1F
            glasHit()
            hasBeenHit = true

            // Uppdatera poäng baserat på vilken typ av glasbricka som träffas
            if (rose) {
                GameManager.incrementPoints(2) // Om det är en roséglasbricka, ge 2 poäng
            } else {
                GameManager.incrementPoints(1) // Annars, ge 1 poäng för proseccoglasbricka
            }

            gameView.handleGlassBreakage()
            hasBeenHit = true // Markera brickan som träffad
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

                // Check if the collision is more horizontal or vertical
                val overlapX = minOf(brickRect.right, ballRect.right) - maxOf(brickRect.left, ballRect.left)
                val overlapY = minOf(brickRect.bottom, ballRect.bottom) - maxOf(brickRect.top, ballRect.top)

                val isHorizontalCollision = overlapX > overlapY

                // Use isHorizontalCollision to determine whether it's a horizontal or vertical collision
                if (isHorizontalCollision) {
                    // Handle horizontal collision
                    ball.speedY *= -1
                    val differenceX = ball.posX - (leftBound + rightBound) / 2
                    ball.speedX = differenceX / 1.5f // Adjust this factor as needed
                } else {
                    // Handle vertical collision
                    ball.speedX *= -1
                    val differenceY = ball.posY - (topBound + bottomBound) / 2
                    ball.speedY = differenceY / 1.5f  // Adjust this factor as needed
                }

                return true
            }
        }
        return false

    }

    fun glasHit() {
        Thread {
            var rotationCounter = 0
            while (rotationCounter < 180) {
                rotationCounter += 2
                rotationAngle = rotationCounter.toFloat()
                if (rotationCounter >= 90) {
                    empty = true
                }
                Thread.sleep(1)
            }
        }.start()
    }

    fun sufaceChanged(inputWidth: Float, inputHeight: Float, imageWidth: Int) {
        viewWidth = inputWidth
        viewHeight = inputHeight
        width = imageWidth
        height = imageWidth * 3

        if(viewWidth > viewHeight) {
            topBrickLayoutOffset = 200
        }
        else {
            topBrickLayoutOffset = 400
        }
    }

}