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
        // Set position
        glassImage?.setBounds(viewWidth.toInt() / 2 + numberFromMiddleX * width + numberFromMiddleX * 10 - width/2,
            numberFromTopY * height - height/2 + numberFromTopY * 10 + topBrickLayoutOffset,
            viewWidth.toInt() / 2 + numberFromMiddleX * width + numberFromMiddleX * 10 + width/2,
            numberFromTopY * height + height/2 + numberFromTopY * 10 + topBrickLayoutOffset)

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
        hasBeenHit = true
        // TODO: Lägg till mer logik för vad som ska hända när bricka träffas, efter att bilden roterat ska brickan bl.a. försvinna så småningom.
    }

    fun sufaceChanged(inputWidth: Float, inputHeight: Float, imageWidth: Int){
        viewWidth = inputWidth
        viewHeight = inputHeight
        width = imageWidth
        height = imageWidth * 3

    }

}