package com.katja.proseccopong

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat


class ClassicGameView(context: Context, private val activityContext: Context): SurfaceView(context), SurfaceHolder.Callback,Runnable {
    private var mholder: SurfaceHolder? = null
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
    private var playerName: String = ""


    init {
        mholder = holder

        if(mholder!=null) {
            holder?.addCallback(this)

        }

        ball1 = Ball(this, mcontext,100f, 100f, 20f, 10f, 20f)
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
    fun onIntersection(p:PlayerPlatform,b:Ball){
        // Calculate the centers of the platform and the ball
        val platformCenterX = p.posX + p.width / 2
        val ballCenterX = b.posX

        // Calculate the difference between the centers
        val differenceX = ballCenterX - platformCenterX

        // If the ball intersects the platform
        if (b.posY + b.size >= p.posY && b.posY <= p.posY + p.height) {
            // Reverse the ball's horizontal direction
            b.speedX = differenceX / 5  // Adjust this factor as needed
            // Reverse the ball's vertical direction (optional)
            b.speedY *= -1
            b.speedY *= 1.05f
            // Increment points
            addPoints()
        }
    }
    fun onCollision(p: PlayerPlatform,b:Ball) {
        val ballBottom = b.posY + b.size
        val platformTop = p.posY

        // If the bottom of the ball meets the top of the platform
        if (ballBottom >= platformTop && b.speedY > 0) {
            // Reverse the ball's vertical direction
            b.speedY *= -1
        }



    }
    fun draw() {

        canvas= mholder!!.lockCanvas() ?: return
        val backgroundDrawable = resources.getDrawable(R.drawable.pexels_kai_pilger_1341279, null)
        backgroundDrawable.setBounds(0, 0, canvas.width, canvas.height)
        backgroundDrawable.draw(canvas)
        drawPoints(canvas)
        playerPlatform.draw(canvas)
        ball1.draw(canvas)
        holder!!.unlockCanvasAndPost(canvas)
    }

    override fun run() {
        while (running) {

            update()
            draw()
            onIntersection(playerPlatform,ball1)
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
        canvas.drawText(points.toString(),viewWidth / 2, (viewHeight / 10) + textSizePoints + 20, paintPoints)
        canvas.drawText(playerName,viewWidth / 2, viewHeight / 10, paintPoints)
    }

    fun setPlayerName(name: String) {
        playerName = name
    }

    fun saveScore() {
        val existingScoreIndex = ScoreList.scoreList.indexOfFirst { it.name == playerName && it.classic }

        if (existingScoreIndex != -1) {
            // If the user already exists in the list, update the score
            ScoreList.scoreList[existingScoreIndex].score = points
        } else {
            // If the user doesn't exist, add a new score
            val newClassicScore = Score(playerName, points, true)
            ScoreList.scoreList.add(newClassicScore)
        }
    }


    fun gameEnd(){
        saveScore() // Save the score before transitioning to HighscoreActivity
        val intent = Intent(activityContext, ClassicHighscoreActivity::class.java)
        activityContext.startActivity(intent)
    }

    companion object {
        var points = 0
        fun addPoints() {
            points ++
        }
    }
}



