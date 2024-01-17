package com.katja.proseccopong

import android.content.Context
import android.media.MediaPlayer

// Singleton pattern to handle the background music.
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

        fun initialize(context: Context, rawResourceId: Int) {
            if (instance == null) {
                instance = AudioManager(context, rawResourceId)
            }
        }

        fun release() {
            instance?.backgroundMusicPlayer?.release()
            instance = null
        }

        fun getInstance(): AudioManager? {
            return instance
        }
    }
}
