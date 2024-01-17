package com.katja.proseccopong

import android.content.Context
import android.media.MediaPlayer

// Singleton pattern to handle the background music.
class AudioManager private constructor(private val context: Context, rawResourceId: Int) {

    private var backgroundMusicPlayer: MediaPlayer = MediaPlayer.create(context, rawResourceId)

    init {
        backgroundMusicPlayer.isLooping = true
        backgroundMusicPlayer.start()
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
    }
}
