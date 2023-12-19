package com.katja.proseccopong

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import com.google.gson.Gson

class ProseccoGameView(context: Context, private val activityContext: Context, private val sharedPreferences: SharedPreferences) : SurfaceView(context), SurfaceHolder.Callback, Runnable, GameView {
    private var mholder: SurfaceHolder? = null
    private var running = false
    lateinit var canvas: Canvas
    private var mcontext = context
    private var ball1: Ball
    private var playerPlatform: PlayerPlatform
    private var thread: Thread? = null
    private var platformLevel = 200f
    private var platformHeight = 25f
    private var platformTop = platformHeight + platformLevel
    private var platformWidth = 200f
    lateinit var bounds: Rect
    var viewWidth = 0f
    var viewHeight = 0f
    var paintPoints = Paint()
    val textSizePoints: Float = resources.getDimension(R.dimen.text_size_points)
    var brickWidth: Int = 50
    private var playerName: String = ""
    var touchX = 0f // Declare touchX as a class-level variable
    // List holding active bricks, filled in onSurfaceCreated. Bricks should be removed once they are hit.
    val brickList = ArrayList<GlassBrick>()


    init {
        mholder = holder

        if (mholder != null) {
            holder?.addCallback(this)

        }
        playerPlatform=PlayerPlatform(mcontext,platformWidth,platformHeight,0f,0f, platformLevel, Color.WHITE)
        ball1 = Ball(this, mcontext, 100f, 100f, 20f, 10f, 20f, platformTop)

    }

    override fun surfaceCreated(holder: SurfaceHolder) {

        // Create glass brick layout
        brickList.add(GlassBrick(this, mcontext, "brick 1_1", brickWidth,
            -2, 0, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 1_2", brickWidth,
            -1, 0, true, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 1_3", brickWidth,
            0, 0, true, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 1_4", brickWidth,
            1, 0, true, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 1_5", brickWidth,
            2, 0, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 2_1", brickWidth,
            -1, 1, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 2_2", brickWidth,
            0, 1, true, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 2_3", brickWidth,
            1, 1, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 3_1", brickWidth,
            0, 2, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 4_1", brickWidth,
            0, 3, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 5_1", brickWidth,
            0, 4, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 6_1", brickWidth,
            -1, 5, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 6_2", brickWidth,
            0, 5, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this, mcontext,"brick 6_3", brickWidth,
            1, 5, false, viewWidth, viewHeight))

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        bounds = Rect(0, 0, width, height)
        playerPlatform.initialize(width, height)
        viewWidth = width.toFloat()
        viewHeight = height.toFloat()

        if (viewHeight >= viewWidth) {
            // Set first Int to aprx. how big part of the screen width the layout should take up (1/x)
            // Set last Int to the number of bricks in the widest row of the brick layout
            brickWidth = viewWidth.toInt() / 4 / 5
        }
        else {
            // Set first Int to aprx. how big part of the screen height the layout should take up (1/x)
            // Second Int converts brickHeight to brickWidth and should be kept as 3
            // Set last Int to the number of bricks in the widest row of the brick layout
            brickWidth = viewHeight.toInt() / 4 / 3 / 7 // Set last Int to the number of bricks in the longest column of the brick layout
        }
        brickList.forEach { brick ->
            brick.sufaceChanged(viewWidth, viewHeight, brickWidth)
        }

        start()

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stop()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN || event?.action == MotionEvent.ACTION_MOVE) {
            touchX = event.x
        }
        return true
    }

    fun updatePlatformPosition() {
        val movementSpeed = calculateMovementSpeedBasedOnScore()
        val difference = touchX - (playerPlatform.posX + playerPlatform.width / 2)
        playerPlatform.posX += difference / movementSpeed

        // Check if the platform is close enough to the touch point
        if (Math.abs(touchX - (playerPlatform.posX + playerPlatform.width / 2)) < 5) {
            // If the difference is very small, set the platform's position to the touch point
            playerPlatform.posX = touchX - playerPlatform.width / 2
        }
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
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    fun update() {
        ball1.update()
        updatePlatformPosition()
    }

    // Function to accsess the return statment of onIntersection by other classes
    override fun ballDown(): Boolean {
        return onIntersection(playerPlatform, ball1)
    }

    fun onIntersection(p: PlayerPlatform, b: Ball): Boolean {
        // Calculate the centers of the platform and the ball
        val platformCenterX = p.posX + p.width / 2
        val ballCenterX = b.posX

        // If the ball intersects the platform
        if (b.posX - b.size / 2 <= platformCenterX + p.width / 2 && b.posX + b.size / 2 >= platformCenterX - p.width / 2) {
            // Calculate the difference between the centers
            val differenceX = ballCenterX - platformCenterX
            // Reverse the ball's horizontal direction
            b.speedX = differenceX / 2  // Adjust this factor as needed
            // Reverse the ball's vertical direction
            b.speedY *= -1
            // Increse the ball's vertcal speed
            b.speedY *= 1.05f // Adjust this factor as needed
            // Move the ball up to avoid it going into the platform
            b.posY = b.posY + b.speedY * 2
            // Increment points
            GameManager.addPoints()
            return false // Return statment to mark that the ball is not out
        } else {
            return true // Return statment to mark that the ball is out
        }
    }

    fun draw() {

        canvas = mholder!!.lockCanvas() ?: return
        val backgroundDrawable = resources.getDrawable(R.drawable.gold_and_black_background, null)
        backgroundDrawable.setBounds(0, 0, canvas.width, canvas.height)
        backgroundDrawable.draw(canvas)
        drawPoints(canvas)
        playerPlatform.draw(canvas)
        brickList.forEach { brick ->
            brick.draw(canvas)
        }
        ball1.draw(canvas)
        holder!!.unlockCanvasAndPost(canvas)
    }

    override fun run() {
        while (running) {

            update()
            draw()
            ball1.checkbounders(bounds, mcontext)
            playerPlatform.checkBounds(bounds)
            Thread.sleep(6)
        }
        Thread.sleep(6)
    }

    fun drawPoints(canvas: Canvas) {
        // Rensa Canvas
        val backgroundDrawable = resources.getDrawable(R.drawable.gold_and_black_background, null)
        backgroundDrawable.setBounds(0, 0, canvas.width, canvas.height)
        backgroundDrawable.draw(canvas)

        val textColor = ContextCompat.getColor(context, R.color.white)
        val shadowColor = ContextCompat.getColor(context, R.color.baby_blue)

        paintPoints.color = textColor
        paintPoints.textAlign = Paint.Align.CENTER
        paintPoints.textSize = textSizePoints
        paintPoints.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        // Använd shadowLayer för skugga
        paintPoints.setShadowLayer(20f, 3f, 3f, shadowColor)

        // Rita "Name" och "Score" bredvid varandra på samma rad, högre upp på skärmen
        val nameText = "Name: $playerName".uppercase()
        val scoreText = "Score: ${GameManager.points}".uppercase()
        val printText = nameText + "     " + scoreText

        val centerX = viewWidth / 2
        val centerY = viewHeight / 8 // Justera y-koordinaten för att höja texten

        canvas.drawText(printText, centerX, centerY, paintPoints)

        paintPoints.clearShadowLayer()
    }


    fun setPlayerName(name: String) {
        playerName = name
    }

    fun calculateMovementSpeedBasedOnScore(): Float {
        val currentScore = GameManager.points

        // Justera hastigheten beroende på poängen
        return when {
            currentScore < 5 -> 5f // Långsammare rörelse om poängen är mindre än 50
            currentScore < 10 -> 4f // Måttlig hastighet om poängen är mindre än 100
            currentScore < 15 -> 3f // Snabbare om poängen är mindre än 150
            else -> 2f // Anpassa efter behov för högre poäng
        }
    }

        // TODO: Anpassa funktionen för Prosecco Pong scores + ändra så att tidigare resultat inte skrivs över
        fun saveScore() {

            val editor = sharedPreferences.edit()

            val existingScoreIndex =
                ScoreList.scoreList.indexOfFirst { it.name == playerName && !it.classic }

            if (existingScoreIndex != -1) {
                // Om användaren redan finns i listan, uppdatera poängen
                ScoreList.scoreList[existingScoreIndex].score = GameManager.points
            } else {
                // Om användaren inte finns, lägg till nya poäng som Prosecco-score
                val newProseccoScore = Score(playerName, GameManager.points, false)
                ScoreList.scoreList.add(newProseccoScore)
            }

            // Konvertera ScoreList till en JSON-sträng och spara den i SharedPreferences
            val scoreListJson = Gson().toJson(ScoreList.scoreList)
            editor.putString("score_list", scoreListJson)
            editor.apply()
        }


        override fun gameEnd() {
            saveScore() // Save the score before transitioning to HighscoreActivity
            println(ScoreList) //Sout for debug
            val intent = Intent(activityContext, HighscoreActivity::class.java)
            activityContext.startActivity(intent)
            GameManager.resetPoints() // Reset points variable so that it starts at 0 in the next game
        }

    }
