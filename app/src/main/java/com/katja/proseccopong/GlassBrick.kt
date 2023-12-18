package com.katja.proseccopong

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat

class GlassBrick(private val gameView: GameView,
                 val context: Context, val name: String, var width: Int,
                 val numberFromMiddleX: Int,
                 val numberFromTopY: Int, var rose: Boolean, var viewWidth: Float, var viewHeight: Float) {

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
        leftBound = viewWidth.toInt() / 2 + numberFromMiddleX * width + numberFromMiddleX * 10 - width/2
        topBound = numberFromTopY * height - height/2 + numberFromTopY * 10 + topBrickLayoutOffset
        rightBound = viewWidth.toInt() / 2 + numberFromMiddleX * width + numberFromMiddleX * 10 + width/2
        bottomBound = numberFromTopY * height + height/2 + numberFromTopY * 10 + topBrickLayoutOffset
    }

    fun draw(canvas: Canvas) {
        // Choose drawable
        if(!hasBeenHit) {
            if (!rose) {
                glassImage = ContextCompat.getDrawable(context, R.drawable.glas_prosecco)
            } else {
                glassImage = ContextCompat.getDrawable(context, R.drawable.glas_rose)
            }
        }
        else {
            glassImage = ContextCompat.getDrawable(context, R.drawable.glas_tomt)
        }

        calculateBounds()

        // Set position
        glassImage?.setBounds(leftBound, topBound, rightBound, bottomBound)

        if(rotationAngle == 0f) {
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

    fun glasHit() {
        val rotationThread = Thread {
            while (rotationAngle < 180) {
                rotationAngle ++
                if (rotationAngle >= 90) {
                    hasBeenHit = true
                }
                Thread.sleep(3)
            }
        }
        // TODO: Lägg till i kollissionskontrollen inne i ProseccoGameView att den här funktionen ska
    //  anropas och en tråd sedan vänta i 800 millisekunder, innan den tar bort brickan från listan.
    }

    fun sufaceChanged(inputWidth: Float, inputHeight: Float, imageWidth: Int){
        viewWidth = inputWidth
        viewHeight = inputHeight
        width = imageWidth
        height = imageWidth * 3

    }

}