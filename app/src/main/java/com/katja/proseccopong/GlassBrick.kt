package com.katja.proseccopong

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat

class GlassBrick(private val gameView: GameView,
                 val context: Context,
                 var posX: Float,
                 var posY: Float, var rose: Boolean) {

    var hasBeenHit = false
    var glassImage = ContextCompat.getDrawable(context, R.drawable.glas_prosecco)
    val width = 100
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
        glassImage?.setBounds(posX.toInt() - width/2, posY.toInt() - height/2, posX.toInt() + width/2, posY.toInt() + height/2)

        if(rotationAngle == 0f) {
            glassImage?.draw(canvas)
        }
        else {
            glassImage?.let {
                canvas.save()
                canvas.rotate(rotationAngle, posX, posY)
                it.draw(canvas)
                canvas.restore()
            }
        }
    }

    fun glasHit() {
        hasBeenHit = true
        // TODO: Lägg till mer logik för vad som ska hända när bricka träffas, efter att bilden roterat ska brickan bl.a. försvinna så småningom.
    }


}