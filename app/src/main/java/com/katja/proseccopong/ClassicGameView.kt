package com.katja.proseccopong

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat



class ClassicGameView(context: Context, private val sharedPreferences: SharedPreferences,private val resources: Resources) : SurfaceView(context), SurfaceHolder.Callback,  GameView {
    var mholder: SurfaceHolder? = null


     var mcontext=context

    private var platformLevel = 200f

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


    var existingScoreIndex = -1
lateinit var gameManager: GameManager
lateinit var proseccoGameView: ProseccoGameView
    init {
        mholder = holder

        if(mholder!=null) {
            holder?.addCallback(this)

        }

        gameManager= GameManager(this,context,"ClassicGame",sharedPreferences,resources)
        proseccoGameView= ProseccoGameView(gameManager, mcontext,mcontext,sharedPreferences)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        bounds=Rect(0,0, width,height)
     gameManager.   playerPlatform.initialize(width, height)
      gameManager.  viewWidth = width.toFloat()
      gameManager . viewHeight = height.toFloat()
       gameManager. start()

    }
    fun setPlayerName(name: String) {
       gameManager. playerName = name
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
       gameManager. stop()
    }
    override fun onTouchEvent(event: MotionEvent?):Boolean{
        val touchX = event?.x ?: 0f

        // Gradvis rörelsehastighet för plattformen
        val movementSpeed = 5f

        // Beräkna skillnaden mellan den nuvarande plattformens position och tryckpunkten
        val difference = touchX -gameManager. playerPlatform.posX

        // Uppdatera plattformens position gradvis mot tryckpunkten
        gameManager. playerPlatform.posX += difference / movementSpeed

        return true
    }






    // Function to accsess the return statment of onIntersection by other classes













    // TODO: Ändra så att tidigare resultat inte skrivs över






    override fun incrementPoints() {
        TODO("Not yet implemented")
=======
    override fun gameEnd(){
        saveScore() // Save the score before transitioning to HighscoreActivity
        println(ScoreList) //Sout for debug
        val intent = Intent(activityContext, HighscoreActivity::class.java)
        activityContext.startActivity(intent)
        GameManager.resetPoints() // Reset points variable so that it starts at 0 in the next game

    }
}



