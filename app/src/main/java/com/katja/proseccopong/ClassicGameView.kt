package com.katja.proseccopong

import android.app.Activity
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
import android.app.AlertDialog
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.WindowManager

class ClassicGameView(context: Context, private val activityContext: Context, private val sharedPreferences: SharedPreferences) : SurfaceView(context), SurfaceHolder.Callback, Runnable, GameView {
    private var mholder: SurfaceHolder? = null
    private var running = false
    lateinit var canvas:Canvas
    private var mcontext=context
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
    private var playerName: String = ""
    var existingScoreIndex = -1
    private var ballOnPlatform = true // Sätt initialt värdet till true för att bollen ska starta på plattformen
    private var gameOver = false

    init {
        mholder = holder

        if(mholder!=null) {
            holder?.addCallback(this)

        }
        playerPlatform=PlayerPlatform(mcontext,platformWidth,platformHeight,0f, platformLevel, Color.WHITE)
        ball1 = Ball(this, mcontext,100f, 100f, 20f, 10f, 20f, platformTop)

    }

    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        bounds=Rect(0,0, width,height)
        playerPlatform.initialize(width, height)
        viewWidth = width.toFloat()
        viewHeight = height.toFloat()
        start()

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stop()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x ?: 0f

        if (ballOnPlatform && event?.action == MotionEvent.ACTION_DOWN) {
            // Bollen är på plattformen och användaren trycker ner på skärmen, skjut iväg bollen
            ball1.speedX = 10f // Ange den önskade hastigheten för bollen i X-riktningen
            ball1.speedY = -20f // Ange den önskade hastigheten för bollen i Y-riktningen
            ballOnPlatform = false
        }

        // Gradvis rörelsehastighet för plattformen
        val movementSpeed = 5f

        // Beräkna skillnaden mellan den nuvarande plattformens position och tryckpunkten
        val difference = touchX - playerPlatform.posX

        // Uppdatera plattformens position gradvis mot tryckpunkten
        playerPlatform.posX += difference / movementSpeed

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

    // Function to accsess the return statment of onIntersection by other classes
    override fun ballDown(): Boolean {
        return onIntersection(playerPlatform, ball1)
    }

    fun onIntersection(p:PlayerPlatform,b:Ball): Boolean{
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
        }
        else {
            return true // Return statment to mark that the ball is out
        }
    }

    fun draw() {

        canvas= mholder!!.lockCanvas() ?: return
        val backgroundDrawable = resources.getDrawable(R.drawable.black_background, null)
        backgroundDrawable.setBounds(0, 0, canvas.width, canvas.height)
        backgroundDrawable.draw(canvas)
        drawPoints(canvas)
        playerPlatform.draw(canvas)
        ball1.draw(canvas)
        holder!!.unlockCanvasAndPost(canvas)
    }

    override fun run() {
        while (running) {
            if (!gameOver) {
                update()
                draw()
                ball1.checkbounders(bounds, mcontext)
                playerPlatform.checkBounds(bounds)

                if (ballOnPlatform) {
                    // Bollen är på plattformen, uppdatera dess position med plattformen
                    ball1.posX = playerPlatform.posX + playerPlatform.width / 2
                    ball1.posY = playerPlatform.posY - ball1.size
                }
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
        val newClassicScore = Score(playerName, GameManager.points, true)
        ScoreList.scoreList.add(newClassicScore)

        // Uppdatera den befintliga variabeln
        existingScoreIndex = ScoreList.scoreList.indexOfFirst { it.name == playerName && it.classic }

        // Konvertera ScoreList till en JSON-sträng och spara den i SharedPreferences
        val scoreListJson = Gson().toJson(ScoreList.scoreList)
        editor.putString("score_list", scoreListJson)
        editor.apply()
    }

    private fun showGameOverDialog() {
        (context as Activity).runOnUiThread {
            val currentTime = System.currentTimeMillis()
            val currentScore = GameManager.points

            // Formatera score och tid
            val formattedScore = "\nScore: $currentScore"
            val formattedTime = "\n\n${Score(playerName, currentScore, true, currentTime).getFormattedDate()}"

            // Skapa en AlertDialog
            val alertDialog = AlertDialog.Builder(activityContext, R.style.CustomAlertDialog)
                .setTitle("Game Over")
                .setMessage(buildSpannableMessage(formattedScore, formattedTime))
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

    private fun buildSpannableMessage(formattedScore: String, formattedTime: String): SpannableStringBuilder {
        // Skapa en SpannableStringBuilder för att kombinera text med olika stilar
        val spannableStringBuilder = SpannableStringBuilder()

        // Lägg till formattedScore med ScoreStyle
        val scoreStyleSpan = TextAppearanceSpan(activityContext, R.style.ScoreStyle)
        val startIndexOfScore = spannableStringBuilder.length
        spannableStringBuilder.append(formattedScore)
        spannableStringBuilder.setSpan(scoreStyleSpan, startIndexOfScore, spannableStringBuilder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Lägg till formattedTime med TimeStyle
        val timeStyleSpan = TextAppearanceSpan(activityContext, R.style.TimeStyle)
        val startIndexOfTime = spannableStringBuilder.length
        spannableStringBuilder.append(formattedTime)
        spannableStringBuilder.setSpan(timeStyleSpan, startIndexOfTime, spannableStringBuilder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannableStringBuilder
    }






    override fun gameEnd() {
        saveScore()
        gameOver = true
        println(ScoreList)
        showGameOverDialog()
        GameManager.resetPoints()
    }

}



