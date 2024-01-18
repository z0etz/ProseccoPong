package com.katja.proseccopong

import android.content.Context
import android.media.MediaPlayer

class AudioManager private constructor(private val context: Context, rawResourceId: Int) {

    private var backgroundMusicPlayer: MediaPlayer = MediaPlayer.create(context, rawResourceId)
    private var isSoundEnabled = true

    init {
        backgroundMusicPlayer.isLooping = true

        if (!isSoundEnabled) {
            backgroundMusicPlayer.pause()
        } else {
            backgroundMusicPlayer.start()
        }
    }

    // Metod för att sätta ljudaktiveringen (på/av)
    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled

        if (!enabled) {
            if (!backgroundMusicPlayer.isPlaying){
                backgroundMusicPlayer.start()
            }
        } else {
            backgroundMusicPlayer.pause()
        }
    }

    companion object {
        private var instance: AudioManager? = null

        // Initialisera AudioManager och skapa en instans om den inte redan finns
        fun initialize(context: Context, rawResourceId: Int) {
            if (instance == null) {
                instance = AudioManager(context, rawResourceId)
            }
        }

        // Släpp resurser när de inte längre behövs
        fun release() {
            instance?.backgroundMusicPlayer?.release()
            instance = null
        }

        // Hämta den befintliga instansen av AudioManager
        fun getInstance(): AudioManager? {
            return instance
        }
    }
}
