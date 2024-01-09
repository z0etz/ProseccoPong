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
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.gson.Gson

class ProseccoGameView(private val gameManager: GameManager,context: Context, private val activityContext: Context, private val sharedPreferences: SharedPreferences) : SurfaceView(context), SurfaceHolder.Callback, Runnable, GameView {
    private var mholder: SurfaceHolder? = null
    private var running = false
    lateinit var canvas: Canvas
    private var mcontext = context

    private var thread: Thread? = null
    private var platformLevel = 100f
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
    val bricksToRemove = mutableListOf<GlassBrick>()

    var glassesHitCount = 0
    init {
        mholder = holder

        if (mholder != null) {
            holder?.addCallback(this)

        }


        playerPlatform=PlayerPlatform(mcontext,platformWidth,platformHeight,0f,0f, platformLevel, Color.WHITE)
        ball1 = Ball(this, mcontext, 1f, 500f, 20f, 10f, 20f, platformTop)


    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (GameManager.brickList.isEmpty()) {
            addBricks()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        bounds = Rect(0, 0, width, height)
       gameManager. playerPlatform.initialize(width, height)
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
        GameManager.brickList.forEach { brick ->
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
        val difference = touchX - (gameManager.playerPlatform.posX + gameManager.playerPlatform.width / 2)
        val speedFactor = decreasePlatformSpeed()
        gameManager. playerPlatform.speedX = difference / speedFactor

        // Adjust the platform's position based on touch input
        if (Math.abs(difference) > 5) {
            // Move the platform if the touch input is significantly different from the platform's position
            gameManager. playerPlatform.posX += gameManager.playerPlatform.speedX
        }
    }

    private fun decreasePlatformSpeed(): Float {
        // Adjust platform movement based on the number of glasses broken

        return when {
            glassesHitCount < 2 -> 1f
            glassesHitCount < 4 -> 1.5f
            glassesHitCount < 6 -> 2.3f
            glassesHitCount < 15 -> 4f
            else -> 5f
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
        gameManager.ball1.update()
        updatePlatformPosition()

        val previousScore = gameManager.points // Save the previous score
        val currentScore = gameManager.points // Get the current score after possible increments
        val currentTime = System.currentTimeMillis()

        val iterator = GameManager.brickList.iterator()
        while (iterator.hasNext()) {
            val brick = iterator.next()

            if (brick.checkCollision(gameManager.ball1)) {
                brick.handleCollision(gameManager.ball1)

                // Mark brick with the time it was hit
                brick.hitTime = currentTime
            }

                when (currentScore) {
                    10 -> increaseBallSpeed(2.01f) // Increase speed at score 10
                    20 -> increaseBallSpeed(2.03f) // Increase speed at score 4
                    30 -> increaseBallSpeed(2.05f) // Increase speed at score 10
            }

        }

        // Remove bricks that were hit more than 800 milliseconds ago
        val iteratorToRemove = GameManager.brickList.iterator()
        while (iteratorToRemove.hasNext()) {
            val brick = iteratorToRemove.next()

            brick.hitTime?.let { hitTime ->
                if (currentTime - hitTime > 200) {
                    bricksToRemove.add(brick)
                }
            }
        }

        // Perform the removal of bricks after the delay
        if (bricksToRemove.isNotEmpty()) {
            removeBricks()
        }

        gameManager. ball1.checkbounders(bounds, mcontext)
        gameManager.playerPlatform.checkBounds(bounds)

        if (GameManager.brickList.isEmpty()) {
            addBricks()
        }
    }
    private fun increaseBallSpeed(factor: Float) {
        gameManager. ball1.speedX *= factor
        gameManager. ball1.speedY *= factor
    }
    fun removeBricks() {
        // Remove marked bricks from the brickList after delay
        bricksToRemove.forEach { brick ->
            GameManager.brickList.remove(brick)
        }
        bricksToRemove.clear()
    }


    // Function to accsess the return statment of onIntersection by other classes

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
            gameManager.addPoints()

            return false // Return statment to mark that the ball is not out
        } else {
            GameManager.brickList.clear()
            return true // Return statment to mark that the ball is out
        }
    }

    fun draw() {

        canvas = mholder!!.lockCanvas() ?: return
        val backgroundDrawable = resources.getDrawable(R.drawable.black_background, null)
        backgroundDrawable.setBounds(0, 0, canvas.width, canvas.height)
        backgroundDrawable.draw(canvas)
        drawPoints(canvas)

        gameManager.playerPlatform.draw(canvas)
        brickList.forEach { brick ->

        playerPlatform.draw(canvas)
        GameManager.brickList.forEach { brick ->

            brick.draw(canvas)
        }
        gameManager. ball1.draw(canvas)
        holder!!.unlockCanvasAndPost(canvas)
    }

    override fun run() {
        while (running) {

            update()
            draw()
            gameManager. ball1.checkbounders(bounds, mcontext)
            gameManager. playerPlatform.checkBounds(bounds)
            Thread.sleep(6)
        }
        Thread.sleep(6)
    }

    fun drawPoints(canvas: Canvas) {
        // Rensa Canvas
        val backgroundDrawable = resources.getDrawable(R.drawable.black_background, null)
        backgroundDrawable.setBounds(0, 0, canvas.width, canvas.height)
        backgroundDrawable.draw(canvas)

        val textColor = ContextCompat.getColor(context, R.color.white)
        val shadowColor = ContextCompat.getColor(context, R.color.gold)

        paintPoints.color = textColor
        paintPoints.textAlign = Paint.Align.CENTER
        paintPoints.textSize = textSizePoints
        paintPoints.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        // Använd shadowLayer för skugga
        paintPoints.setShadowLayer(20f, 3f, 3f, shadowColor)

        // Rita "Name" och "Score" bredvid varandra på samma rad, högre upp på skärmen
        val nameText = "Name: $playerName".uppercase()
        val scoreText = "Score: ${gameManager.points}".uppercase()
        val printText = nameText + "     " + scoreText

        val centerX = viewWidth / 2
        val centerY = viewHeight / 10 // Justera y-koordinaten för att höja texten

        canvas.drawText(printText, centerX, centerY, paintPoints)

        paintPoints.clearShadowLayer()
    }


    fun setPlayerName(name: String) {
        playerName = name
    }



        // TODO: Anpassa funktionen för Prosecco Pong scores + ändra så att tidigare resultat inte skrivs över
        fun saveScore() {

            val editor = sharedPreferences.edit()

            val existingScoreIndex =
                ScoreList.scoreList.indexOfFirst { it.name == playerName && !it.classic }

            if (existingScoreIndex != -1) {
                // Om användaren redan finns i listan, uppdatera poängen
                ScoreList.scoreList[existingScoreIndex].score = gameManager.points
            } else {
                // Om användaren inte finns, lägg till nya poäng som Prosecco-score
                val newProseccoScore = Score(playerName, gameManager.points, false)
                ScoreList.scoreList.add(newProseccoScore)
            }

            // Konvertera ScoreList till en JSON-sträng och spara den i SharedPreferences
            val scoreListJson = Gson().toJson(ScoreList.scoreList)
            editor.putString("score_list", scoreListJson)
            editor.apply()
        }

    // Method to handle glass breakage event
    override fun handleGlassBreakage() {
        glassesHitCount++
    }


    override fun incrementPoints() {

        gameManager.incrementPoints(1) // Använd det här om standardpoängen är 1
    }

    fun addBricks() {
        // Create glass brick layout
        brickList.add(GlassBrick(this,gameManager, mcontext, "brick 1_1", brickWidth,
            -2, 0, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 1_2", brickWidth,
            -1, 0, true, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 1_3", brickWidth,
            0, 0, true, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 1_4", brickWidth,
            1, 0, true, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 1_5", brickWidth,
            2, 0, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 2_1", brickWidth,
            -1, 1, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 2_2", brickWidth,
            0, 1, true, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 2_3", brickWidth,
            1, 1, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 3_1", brickWidth,
            0, 2, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 4_1", brickWidth,
            0, 3, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 5_1", brickWidth,
            0, 4, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 6_1", brickWidth,
            -1, 5, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 6_2", brickWidth,
            0, 5, false, viewWidth, viewHeight))
        brickList.add(GlassBrick(this,gameManager, mcontext,"brick 6_3", brickWidth,

    fun addBricks() {
        // Create glass brick layout
        GameManager.brickList.add(GlassBrick(this, mcontext, "brick 1_1", brickWidth,
            -2, 0, false, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 1_2", brickWidth,
            -1, 0, true, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 1_3", brickWidth,
            0, 0, true, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 1_4", brickWidth,
            1, 0, true, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 1_5", brickWidth,
            2, 0, false, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 2_1", brickWidth,
            -1, 1, false, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 2_2", brickWidth,
            0, 1, true, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 2_3", brickWidth,
            1, 1, false, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 3_1", brickWidth,
            0, 2, false, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 4_1", brickWidth,
            0, 3, false, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 5_1", brickWidth,
            0, 4, false, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 6_1", brickWidth,
            -1, 5, false, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 6_2", brickWidth,
            0, 5, false, viewWidth, viewHeight))
        GameManager.brickList.add(GlassBrick(this, mcontext,"brick 6_3", brickWidth,

            1, 5, false, viewWidth, viewHeight))

    }

}
