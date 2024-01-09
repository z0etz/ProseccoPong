package com.katja.proseccopong

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.ContextCompat

import com.google.gson.Gson

class GameManager( val classicGameView: ClassicGameView,
                  private val context: Context,
                  private val activityContext: String,
                  private val sharedPreferences: SharedPreferences, private val resources: Resources
):Runnable{
    lateinit var canvas: Canvas
    var paintPoints = Paint()
    val textSizePoints: Float = resources.getDimension(R.dimen.text_size_points)
    var viewWidth = 0f
    var viewHeight = 0f

    var playerName: String=""

    private var mcontext=context
  var ball1: Ball
var playerPlatform: PlayerPlatform

    private var platformLevel = 200f
    private var platformHeight = 25f
    private var platformTop = platformHeight + platformLevel
    private var platformWidth = 200f



    var existingScoreIndex = -1
    var points = 0
    var running = false

    var thread: Thread? = null
    init {

        playerPlatform=PlayerPlatform(mcontext,platformWidth,platformHeight,0f,0f, platformLevel, Color.WHITE)
        ball1 = Ball( this,mcontext,100f, 100f, 20f, 10f, 20f, platformTop)

    }
    fun start() {
      running = true


       thread = Thread(this)
thread?.start()
    }

    fun update() {
      ball1.update()

    }
    override fun run() {
        while (running) {

            update()
          draw_ClassicGame()
            ball1.checkbounders(classicGameView. bounds,mcontext)
          playerPlatform.checkBounds(classicGameView.bounds)

        }
        Thread.sleep(6)
    }
    fun draw_ClassicGame() {

      canvas= classicGameView. mholder!!.lockCanvas() ?: return
        val backgroundDrawable = resources.getDrawable(R.drawable.black_background, null)
        backgroundDrawable.setBounds(0, 0, canvas.width,canvas.height)
        backgroundDrawable.draw(canvas)
       drawPoints(canvas)
     playerPlatform.draw(canvas)
        ball1.draw(canvas)
        classicGameView. holder!!.unlockCanvasAndPost(canvas)
    }

    fun stop() {
        running = false
        try {
            thread?.join()
        } catch (e:InterruptedException){
            e.printStackTrace()
        }

    }
    fun checkActivityType() {
       when(activityContext){

           "ClassicGame"->{  draw_ClassicGame()}
           }
       }



    fun gameEnd(){
        saveScore() // Save the score before transitioning to HighscoreActivity
        println(ScoreList) //Sout for debug
        val intent = Intent(context, HighscoreActivity::class.java)
        context.startActivity(intent)
      resetPoints() // Reset points variable so that it starts at 0 in the next game
    }
    fun saveScore() {
        val editor = sharedPreferences.edit()

        // Lägg till ny poäng i listan oavsett om det finns en duplicat
        val newClassicScore = Score(playerName, points, true)
        ScoreList.scoreList.add(newClassicScore)

        // Uppdatera den befintliga variabeln
        existingScoreIndex = ScoreList.scoreList.indexOfFirst { it.name == playerName && it.classic }

        // Konvertera ScoreList till en JSON-sträng och spara den i SharedPreferences
        val scoreListJson = Gson().toJson(ScoreList.scoreList)
        editor.putString("score_list", scoreListJson)
        editor.apply()
    }
    fun addPoints() {
        points++
    }

    fun resetPoints() {
        points = 0
    }

    fun incrementPoints(pointsToAdd: Int) {
        points += pointsToAdd
    }
  fun ballDown(): Boolean {
        return onIntersection(playerPlatform,ball1)
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
           addPoints()
            return false // Return statment to mark that the ball is not out
        }
        else {
            return true // Return statment to mark that the ball is out
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
        val scoreText = "Score: ${points}".uppercase()
        val printText = nameText + "     " + scoreText

        val centerX = viewWidth / 2
        val centerY = viewHeight / 10 // Justera y-koordinaten för att höja texten

        canvas.drawText(printText, centerX, centerY, paintPoints)

        paintPoints.clearShadowLayer()
    }



}

