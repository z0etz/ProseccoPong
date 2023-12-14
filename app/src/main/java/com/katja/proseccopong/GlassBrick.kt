package com.katja.proseccopong

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat

class GlassBrick(private val gameView: GameView,
                 val context: Context,
                 var offsetFromMiddleX: Float,
                 var offsetFromTopY: Float, var rose: Boolean, var viewWidth: Float, var viewHeight: Float) {

    var hasBeenHit = false
    var glassImage = ContextCompat.getDrawable(context, R.drawable.glas_prosecco)
    val width = 50
    val height = width * 3
    var rotationAngle = 0f

    init {
        if (!rose) {
            val glassImage = ContextCompat.getDrawable(context, R.drawable.glas_prosecco)
        } else {
            val glassImage = ContextCompat.getDrawable(context, R.drawable.glas_rose)
        }
    }


    // Draw image centered at posX and posY
    fun draw(canvas: Canvas) {
        // Choose drawable
        if(!hasBeenHit) {
            if (!rose) {
                val glassImage = ContextCompat.getDrawable(context, R.drawable.glas_prosecco)
            } else {
                val glassImage = ContextCompat.getDrawable(context, R.drawable.glas_rose)
            }
        }
        else {
            val glassImage = ContextCompat.getDrawable(context, R.drawable.glas_tomt)
        }
        // Set position
        glassImage?.setBounds(viewWidth.toInt() / 2 + offsetFromMiddleX.toInt() - width/2,
            offsetFromTopY.toInt() - height/2,
            viewWidth.toInt() / 2 + offsetFromMiddleX.toInt() + width/2,
             offsetFromTopY.toInt() + height/2)

        if(rotationAngle == 0f) {
            glassImage?.draw(canvas)
        }
        else {
            glassImage?.let {
                canvas.save()
                canvas.rotate(rotationAngle, offsetFromMiddleX, offsetFromTopY)
                it.draw(canvas)
                canvas.restore()
            }
        }
    }

    fun glasHit() {
        hasBeenHit = true
        // TODO: Lägg till mer logik för vad som ska hända när bricka träffas, efter att bilden roterat ska brickan bl.a. försvinna så småningom.
    }

    fun sufaceChanged(width: Float, height: Float){
        viewWidth = width
        viewHeight = height
    }

}