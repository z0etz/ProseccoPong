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

class ProseccoGameView(context: Context, private val activityContext: Context, private val sharedPreferences: SharedPreferences) : SurfaceView(context), SurfaceHolder.Callback, Runnable, GameView {
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
private var existingScoreIndex = -1
private var platformSound: MediaPlayer? = null
private var glassSound: MediaPlayer? = null
private var gameOverSound: MediaPlayer? = null
private var pointsAtGameEnd = 0
private var playerName: String = ""
private var gameOver = false
private var ballOnPlatform = true

var viewWidth = 0f
var viewHeight = 0f
var paintPoints = Paint()
val textSizePoints: Float = resources.getDimension(R.dimen.text_size_points)
var brickWidth: Int = 50
var touchX = 0f
val bricksToRemove = mutableListOf<GlassBrick>()
var glassesHitCount = 0

init {
    mholder = holder
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
    glassSound?.release()
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
        // Sätt första heltal till ungefär hur stor del av skärmens bredd layouten ska ta upp (1/x)
        // Sätt sista heltal till antalet brickor i den bredaste raden av bricklayouten
        brickWidth = viewWidth.toInt() / 4 / 5
    }
    else {
        // Sätt första heltal till ungefär hur stor del av skärmens höjd layouten ska ta upp (1/x)
        // Andra heltalen omvandlar brickHeight till brickWidth och bör hållas som 3
        // Sätt sista heltal till antalet brickor i den bredaste raden av bricklayouten
        brickWidth = viewHeight.toInt() / 2 / 3 / 6 // Sätt sista heltal till antalet brickor i den längsta kolumnen av bricklayouten
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
        val randomSpeedX = (Math.random() * 20 - 10) // Slumpmässig hastighet mellan -10 och 10
        var randomSpeedY = -30.0 // Initiera variabel
        if(randomSpeedX > 0) {
            randomSpeedY = -30 + randomSpeedX // Hastighet mellan -30 0ch -20
        }
        else {
            randomSpeedY = -30 - randomSpeedX // Hastighet mellan -30 0ch -20
        }
        ball1.speedX = randomSpeedX.toFloat()
        ball1.speedY = randomSpeedY.toFloat()
        ballOnPlatform = false

        playPlatformSound()

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

 fun decreasePlatformSpeed(): Float {
    // Justera plattformens rörelse baserat på antalet krossade glas

    return when {
        glassesHitCount < 1 -> 18.0f   // Ökning av hastigheten när inga eller få glas har krossats
        glassesHitCount < 2 -> 14.0f   // Ökning av hastigheten när 2 glas har krossats
        glassesHitCount < 3 -> 8.0f   // Ökning av hastigheten när 3 glas har krossats
        glassesHitCount < 4 -> 2f   // Ökning av hastigheten när 4 glas har krossats
        else -> 1.0f                  // Ökning av hastigheten när 5 eller fler glas har krossats
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

// Ta bort brickor som träffades för mer än 800 millisekunder sedan
    val iteratorToRemove = GameManager.brickList.iterator()
    while (iteratorToRemove.hasNext()) {
        val brick = iteratorToRemove.next()

        brick.hitTime?.let { hitTime ->
            if (currentTime - hitTime > 200) {
                bricksToRemove.add(brick)
            }
        }
    }

// Utför borttagningen av brickor efter fördröjningen
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


// Funktion för att få åtkomst till return-satsen från onIntersection av andra klasser
    override fun ballDown(): Boolean {
    return onIntersection(playerPlatform, ball1)
}

    fun onIntersection(p: PlayerPlatform, b: Ball): Boolean {

        val platformCenterX = p.posX + p.width / 2
        val ballCenterX = b.posX

        if (b.posX - b.size / 2 <= platformCenterX + p.width / 2 && b.posX + b.size / 2 >= platformCenterX - p.width / 2) {

            val differenceX = ballCenterX - platformCenterX

            b.speedX = differenceX / 2  // Justera detta faktor efter behov
            // Ändra riktningen för bollen vertikalt
            b.speedY *= -1
            // Öka bollens vertikala hastighet
            b.speedY *= 1.05f // Justera detta faktor efter behov
            // Flytta bollen uppåt för att undvika att den går in i plattformen
            b.posY = b.posY + b.speedY * 2
            platformSound?.start()

            return false // Return-sats för att markera att bollen inte är ute
        } else {
            GameManager.brickList.clear() // Ta bort alla brickor om bollen är ute
            return true // Return-sats för att markera att bollen är ute
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
    val newProseccoScore = Score(playerName, pointsAtGameEnd, false)
    ScoreList.scoreList.add(newProseccoScore)

    // Uppdatera den befintliga variabeln
    existingScoreIndex = ScoreList.scoreList.indexOfFirst { it.name == playerName && !it.classic }

    // Konvertera ScoreList till en JSON-sträng och spara den i SharedPreferences
    val scoreListJson = Gson().toJson(ScoreList.scoreList)
    editor.putString("score_list", scoreListJson)
    editor.apply()
}


// Metod för att hantera händelsen av glasbrott
    override fun handleGlassBreakage() {
    glassesHitCount++
//    glassSound?.release()
//    glassSound?.start()
}

fun showGameOverDialog() {
    (context as Activity).runOnUiThread {
        val currentTime = System.currentTimeMillis()
        val currentScore = pointsAtGameEnd

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

 fun playGameOverSound(){
    val gameOverSound = MediaPlayer.create(mcontext, R.raw.gameover)
    gameOverSound.setOnCompletionListener { mp ->mp.release() }
    gameOverSound.start()
}

override fun gameEnd() {
    pointsAtGameEnd = GameManager.points
    saveScore()
    println(ScoreList)
    showGameOverDialog()
    GameManager.clearBricklist()
    playGameOverSound()
    GameManager.resetPoints()
    gameOver = true
}


    // Metod för att hantera händelsen av glassplittring
    fun addBricks() {
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
