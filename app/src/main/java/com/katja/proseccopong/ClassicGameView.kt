package com.katja.proseccopong

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat

class ClassicGameView(context: Context): SurfaceView(context), SurfaceHolder.Callback,Runnable {
    private var mholder: SurfaceHolder? = holder
    private var running = false
    lateinit var canvas:Canvas
    private var mcontext=context
    private var ball1: Ball
    private var playerPlatform: PlayerPlatform
    private var thread: Thread? = null
    lateinit var bounds: Rect
    var viewWidth = 0f
    var viewHeight = 0f
    var paintPoints = Paint()
    val textSizePoints: Float = resources.getDimension(R.dimen.text_size_points)

    init {
       if(mholder!=null) {
           holder?.addCallback(this)

       }

        ball1 = Ball(mcontext,100f, 100f, 20f, 5f, 5f)
        playerPlatform=PlayerPlatform(mcontext,100f,25f,5f,0f,Color.WHITE)
    }
    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        bounds=Rect(0,0,width,height)
        playerPlatform.initialize(width, height)
        viewWidth = width.toFloat()
        viewHeight = height.toFloat()
        start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stop()
    }
    override fun onTouchEvent(event: MotionEvent?):Boolean{

        playerPlatform.posX=event!!.x

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
        drawPoints(canvas)
        playerPlatform.draw(canvas)
        ball1.draw(canvas)
        holder!!.unlockCanvasAndPost(canvas)
    }

    override fun run() {
        while (running) {

                update()
                draw()
            ball1.checkbounders(bounds,mcontext)
            playerPlatform.checkBounds(bounds)

            }
        Thread.sleep(6)
        }

    fun drawPoints(canvas: Canvas) {
        paintPoints.color = Color.BLACK
        paintPoints.textAlign = Paint.Align.CENTER
        paintPoints.textSize = textSizePoints
        paintPoints.color = ContextCompat.getColor(context, R.color.pink)
        canvas.drawText(points.toString(),viewWidth / 2, viewHeight / 10, paintPoints)
    }

    companion object {
        var points = 0
       fun addPoints() {
            // TODO: anropa denna funktion varje gång bollen studdsar mot spelbrickan.
            points ++
        }
    }
}
