package com.katja.proseccopong

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context): SurfaceView(context), SurfaceHolder.Callback,Runnable {
    private var mholder: SurfaceHolder? = holder
    private var running = false
    lateinit var canvas:Canvas
    private lateinit var ball1: Ball
    private var thread: Thread? = null
    lateinit var bounds: Rect

    init {
       if(mholder!=null) {
           holder?.addCallback(this)

       }



        ball1 = Ball(100f, 100f, 20f, 5f, 5f)
    }
    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            bounds=Rect(0,0,width,height)
        start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stop()
    }
    override fun onTouchEvent(event: MotionEvent?):Boolean{

        ball1.posX=event!!.x

        return true
    }

    fun start() {
        running = true
        thread = Thread(this)
        thread?.start()
    }

    fun stop() {
        running = false
        try {
            thread?.join()
        } catch (e:InterruptedException){
            e.printStackTrace()
        }

    }

    fun update() {
        ball1.update()
    }

    fun draw() {

        canvas= holder!!.lockCanvas()

        canvas.drawColor(Color.BLACK)
        ball1.draw(canvas)
        holder!!.unlockCanvasAndPost(canvas)

    }

    override fun run() {
        while (running) {

                update()
                draw()
            ball1.checkbounders(bounds)

            }
        Thread.sleep(6)
        }
    }
