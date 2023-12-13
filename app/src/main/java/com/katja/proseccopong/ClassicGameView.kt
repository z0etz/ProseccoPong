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


class ClassicGameView(context: Context, private val activityContext: Context, private val sharedPreferences: SharedPreferences) : SurfaceView(context), SurfaceHolder.Callback, Runnable, GameView {
    private var mholder: SurfaceHolder? = null
    private var running = false
    lateinit var canvas:Canvas
    private var mcontext=context
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
    private var playerName: String = ""


    init {
        mholder = holder

        if(mholder!=null) {
            holder?.addCallback(this)

        }
        playerPlatform=PlayerPlatform(mcontext,platformWidth,platformHeight,0f,0f, platformLevel, Color.WHITE)
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
    override fun onTouchEvent(event: MotionEvent?):Boolean{
        val touchX = event?.x ?: 0f

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
            ball1.checkbounders(bounds,mcontext)
            playerPlatform.checkBounds(bounds)

        }
        Thread.sleep(6)
    }


    fun drawPoints(canvas: Canvas) {
        val textColor = ContextCompat.getColor(context, R.color.white)
        val shadowColor = ContextCompat.getColor(context, R.color.baby_blue)

        paintPoints.color = textColor
        paintPoints.textAlign = Paint.Align.CENTER
        paintPoints.textSize = textSizePoints
        paintPoints.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Gör texten fet

        // Använd shadowLayer för skugga
        paintPoints.setShadowLayer(20f, 3f, 3f, shadowColor)

        // Rita "Name" och "Score" bredvid varandra på samma rad, högre upp på skärmen
        val nameText = "Name: $playerName".uppercase() // Gör texten till stora bokstäver
        val scoreText = "Score: $GameManager.points".uppercase() // Gör texten till stora bokstäver

        val centerX = viewWidth / 2
        val centerY = viewHeight / 8 // Justera y-koordinaten för att höja texten
        val distanceBetweenText = 10f // Justera avståndet mellan "Name" och "Score"

        // Rita "Name"
        val nameX = centerX - paintPoints.measureText(nameText) / 2
        val nameY = centerY - textSizePoints / 2
        canvas.drawText(nameText, nameX, nameY, paintPoints)

        // Rita "Score" bredvid "Name"
        val scoreX = centerX + paintPoints.measureText(nameText) / 2 + distanceBetweenText
        val scoreY = centerY - textSizePoints / 2
        canvas.drawText(scoreText, scoreX, scoreY, paintPoints)

        // Rensa shadowLayer efter användning
        paintPoints.clearShadowLayer()
    }


    fun setPlayerName(name: String) {
        playerName = name
    }

    // TODO: Ändra så att tidigare resultat inte skrivs över
    fun saveScore() {
        val editor = sharedPreferences.edit()

        // Hitta befintliga poängposter för samma spelare
        val existingScores = ScoreList.scoreList.filter { it.name == playerName && it.classic }

        // Kontrollera om den nya poängen redan finns i listan
        val isDuplicate = existingScores.any { it.score == points }


        // Lägg till ny poäng i listan om det inte är en duplicat
        if (!isDuplicate) {
            // Lägg till ny poäng i listan
            val newClassicScore = Score(playerName, points, true)

        if (existingScoreIndex != -1) {
            // Om användaren redan finns i listan, uppdatera poängen
            ScoreList.scoreList[existingScoreIndex].score = GameManager.points
        } else {
            // Om användaren inte finns, lägg till nya poäng
            val newClassicScore = Score(playerName, GameManager.points, true)

            ScoreList.scoreList.add(newClassicScore)
        }

        // Konvertera ScoreList till en JSON-sträng och spara den i SharedPreferences
        val scoreListJson = Gson().toJson(ScoreList.scoreList)
        editor.putString("score_list", scoreListJson)
        editor.apply()
    }


    override fun gameEnd(){
        saveScore() // Save the score before transitioning to HighscoreActivity
        println(ScoreList) //Sout for debug
        val intent = Intent(activityContext, HighscoreActivity::class.java)
        activityContext.startActivity(intent)
        GameManager.resetPoints() // Reset points variable so that it starts at 0 in the next game
    }
}


