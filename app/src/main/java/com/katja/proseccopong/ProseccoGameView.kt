package com.katja.proseccopong

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.media.MediaPlayer
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.google.gson.Gson

class ProseccoGameView(
    context: Context,
    private val activityContext: Context,
    private val sharedPreferences: SharedPreferences
) : SurfaceView(context), SurfaceHolder.Callback, Runnable, GameView {
    private var mholder: SurfaceHolder? = null
    private var running = false
    lateinit var canvas: Canvas
    private var mcontext = context
    private var ball1: Ball
    private var playerPlatform: PlayerPlatform
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
    private var existingScoreIndex = -1 // Lägg till den här raden
    private var platformSound: MediaPlayer? = null
    private var glassSound: MediaPlayer? = null
    private var gameOverSound: MediaPlayer? = null

    // List holding hit bricks that will be removed once they have had time to spin.
    val bricksToRemove = mutableListOf<GlassBrick>()

    var glassesHitCount = 0
    private var gameOver = false
    private var ballOnPlatform = true // Sätt initialt värdet till true för att bollen ska starta på plattformen

    init {
        mholder = holder
        playPlatformSound()
        playGlassSound()

        if (mholder != null) {
            holder?.addCallback(this)

        }
        playerPlatform=PlayerPlatform(mcontext,platformWidth,platformHeight,0f, platformLevel, Color.WHITE)
        ball1 = Ball(this, mcontext, playerPlatform.posX + playerPlatform.width / 2, playerPlatform.posY - 20f, 20f, 10f, 20f, platformTop)

    }
    override fun playPlatformSound(){
        platformSound = MediaPlayer.create(mcontext, R.raw.platform)
    }

    override fun playGlassSound(){
        glassSound = MediaPlayer.create(mcontext, R.raw.glass_sound)
    }


    override fun surfaceCreated(holder: SurfaceHolder) {
        if (GameManager.brickList.isEmpty()) {
            addBricks()
        }
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
            brickWidth = viewHeight.toInt() / 2 / 3 / 6 // Set last Int to the number of bricks in the longest column of the brick layout
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

        handleBallOnPlatformTouchEvent(event, touchX)

        return true
    }

    fun handleBallOnPlatformTouchEvent(event: MotionEvent?, touchX: Float) {
        if (ballOnPlatform && event?.action == MotionEvent.ACTION_DOWN) {
            // Bollen är på plattformen och användaren trycker ner på skärmen, skjut iväg bollen
            ball1.speedX = 10f // Ange den önskade hastigheten för bollen i X-riktningen
            ball1.speedY = -20f // Ange den önskade hastigheten för bollen i Y-riktningen
            ballOnPlatform = false
        }
    }

    fun handleBallAndPlatform() {
        ball1.checkbounders(bounds, mcontext)
        playerPlatform.checkBounds(bounds)

        if (ballOnPlatform) {
            // Bollen är på plattformen, uppdatera dess position med plattformen
            ball1.posX = playerPlatform.posX + playerPlatform.width / 2
            ball1.posY = playerPlatform.posY - ball1.size
        }
    }

    fun updatePlatformPosition() {
        if (touchX != 0.0f) {
            val difference = touchX - (playerPlatform.posX + playerPlatform.width / 2)
            val speedFactor = decreasePlatformSpeed()
            playerPlatform.speedX = difference / speedFactor

            // Adjust the platform's position based on touch input
            if (Math.abs(difference) > 5) {
                // Move the platform if the touch input is significantly different from the platform's position
                playerPlatform.posX += playerPlatform.speedX
            }
        }
    }

    private fun decreasePlatformSpeed(): Float {
        // Justera plattformens rörelse baserat på antalet krossade glas

        return when {
            glassesHitCount < 1 -> 20.0f   // Ökning av hastigheten när inga eller få glas har krossats
            glassesHitCount < 2 -> 15.0f   // Ökning av hastigheten när 2-3 glas har krossats
            glassesHitCount < 3 -> 18.0f   // Ökning av hastigheten när 4-5 glas har krossats
            glassesHitCount < 4 -> 2f   // Ökning av hastigheten när 6-14 glas har krossats
            else -> 1.0f                  // Ökning av hastigheten när 15 eller fler glas har krossats
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
        platformSound?.release()
        glassSound?.release()
    }

    fun update() {
        ball1.update()
        updatePlatformPosition()


        val previousScore = GameManager.points // Save the previous score
        val currentScore = GameManager.points // Get the current score after possible increments
        val currentTime = System.currentTimeMillis()

        val iterator = GameManager.brickList.iterator()
        while (iterator.hasNext()) {
            val brick = iterator.next()

            if (brick.checkCollision(ball1)) {
                brick.handleCollision(ball1)

                // Mark brick with the time it was hit
                brick.hitTime = currentTime
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

        ball1.checkbounders(bounds, mcontext)
        playerPlatform.checkBounds(bounds)

        if (GameManager.brickList.isEmpty()) {
            addBricks()
        }
    }

    fun removeBricks() {
        // Remove marked bricks from the brickList after delay
        bricksToRemove.forEach { brick ->
            GameManager.brickList.remove(brick)
        }
        bricksToRemove.clear()
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
            platformSound?.start()


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
        playerPlatform.draw(canvas)
        GameManager.brickList.forEach { brick ->
            brick.draw(canvas)
        }
        ball1.draw(canvas)
        holder!!.unlockCanvasAndPost(canvas)

    }

    override fun run() {
        while (running) {
            if (!gameOver) {
                update()
                draw()
                handleBallAndPlatform()
            }
            Thread.sleep(6)
        }
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
        val scoreText = "Score: ${GameManager.points}".uppercase()
        val printText = nameText + "     " + scoreText

        val centerX = viewWidth / 2
        val centerY = viewHeight / 10 // Justera y-koordinaten för att höja texten

        canvas.drawText(printText, centerX, centerY, paintPoints)

        paintPoints.clearShadowLayer()
    }


    fun setPlayerName(name: String) {
        playerName = name
    }


    fun saveScore() {
        val editor = sharedPreferences.edit()

        // Lägg till ny poäng i listan oavsett om det finns en duplicat
        val newProseccoScore = Score(playerName, GameManager.points, false)
        ScoreList.scoreList.add(newProseccoScore)

        // Uppdatera den befintliga variabeln
        existingScoreIndex = ScoreList.scoreList.indexOfFirst { it.name == playerName && !it.classic }

        // Konvertera ScoreList till en JSON-sträng och spara den i SharedPreferences
        val scoreListJson = Gson().toJson(ScoreList.scoreList)
        editor.putString("score_list", scoreListJson)
        editor.apply()
    }


    // Method to handle glass breakage event
    override fun handleGlassBreakage() {
        glassesHitCount++
        glassSound?.start()
    }

    fun showGameOverDialog() {
        (context as Activity).runOnUiThread {
            val currentTime = System.currentTimeMillis()
            val currentScore = GameManager.points

            // Formatera score och tid
            val formattedScore = "\nScore: $currentScore"
            val formattedTime = "\n\n${Score(playerName, currentScore, true, currentTime).getFormattedDate()}"

            // Skapa en SpannableStringBuilder för att kombinera text med olika stilar
            val spannableStringBuilder = SpannableStringBuilder()

            // Lägg till formattedScore med ScoreStyle
            val scoreStyleSpan = TextAppearanceSpan(activityContext, R.style.ScoreStyle)
            spannableStringBuilder.append(formattedScore)
            spannableStringBuilder.setSpan(scoreStyleSpan, 0, formattedScore.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Lägg till formattedTime med TimeStyle
            val timeStyleSpan = TextAppearanceSpan(activityContext, R.style.TimeStyle)
            spannableStringBuilder.append(formattedTime)
            spannableStringBuilder.setSpan(timeStyleSpan, formattedScore.length, spannableStringBuilder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Skapa en AlertDialog
            val alertDialog = AlertDialog.Builder(activityContext, R.style.CustomAlertDialog)
                .setTitle("Game Over")
                .setMessage(spannableStringBuilder)
                .setPositiveButton("OK") { dialog, which ->
                    val intent = Intent(activityContext, HighscoreActivity::class.java)
                    activityContext.startActivity(intent)
                }
                .setCancelable(false)
                .create()

            // Justera storlek på dialogfönstret
            alertDialog.setOnShowListener {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(alertDialog.window?.attributes)
                layoutParams.width = 800 // Justera bredden efter behov
                layoutParams.height = 550 // Justera höjden efter behov
                alertDialog.window?.attributes = layoutParams
            }

            alertDialog.show()
        }
    }

    private fun playGameOverSound(){
        val gameOverSound = MediaPlayer.create(mcontext, R.raw.gameover)
        gameOverSound.setOnCompletionListener { mp ->mp.release() }
        gameOverSound.start()
    }

    override fun gameEnd() {
        saveScore()
        println(ScoreList)
        showGameOverDialog()
        GameManager.resetPoints()
        GameManager.clearBricklist()
        playGameOverSound()
        gameOver = true
    }


    fun addBricks() {
        // Create glass brick layout
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 1_1", brickWidth,
                -2, 0, false, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 1_2", brickWidth,
                -1, 0, true, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 1_3", brickWidth,
                0, 0, true, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 1_4", brickWidth,
                1, 0, true, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 1_5", brickWidth,
                2, 0, false, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 2_1", brickWidth,
                -1, 1, false, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 2_2", brickWidth,
                0, 1, true, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 2_3", brickWidth,
                1, 1, false, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 3_1", brickWidth,
                0, 2, false, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 4_1", brickWidth,
                0, 3, false, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 5_1", brickWidth,
                0, 4, false, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 6_1", brickWidth,
                -1, 5, false, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 6_2", brickWidth,
                0, 5, false, viewWidth, viewHeight
            )
        )
        GameManager.brickList.add(
            GlassBrick(
                this, mcontext, "brick 6_3", brickWidth,
                1, 5, false, viewWidth, viewHeight
            )
        )

    }

}
