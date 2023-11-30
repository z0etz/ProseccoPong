package com.katja.proseccopong

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.provider.Settings.Global.getString
import android.widget.Toast

class Ball(val context: Context, var posX: Float,
            var posY: Float,
            var size: Float,
            var speedX: Float,
            var speedY: Float,
            color: Int = Color.BLUE
) {

    private val paint = Paint().apply { this.color = color }


    fun checkbounders(bounds: Rect, context: Context){

        if(posX-size<bounds.left||posX-size>bounds.right){
            speedX*=-1
            speedX+=speedX*1.2f
        }
        if(posY-size<bounds.top){
            speedY*=-1
            speedY+=speedY*1.2f
        }
if(posY-size>bounds.bottom){
    (context as Activity).runOnUiThread {
        val toast = "Ball is out"
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }
}
    }

    fun update() {
        posY += speedY
        posX += speedX
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(posX, posY, size, paint)
    }
}