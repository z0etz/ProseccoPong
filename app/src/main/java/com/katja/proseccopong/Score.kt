package com.katja.proseccopong

// Bool True for classic score and False for Prosecco score
import java.util.*
import java.text.SimpleDateFormat
import java.util.Locale


class Score(val name: String, var score: Int, val classic: Boolean = false, val timestamp: Long = System.currentTimeMillis()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Score) return false

        return name == other.name && classic == other.classic
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + classic.hashCode()
        return result
    }

    // getFormattedDate-metoden returnerar tidstämpeln som en formaterad sträng.
    // Använder det angivna datumformatet ("yyyy-MM-dd HH:mm:ss").
    fun getFormattedDate(): String {
        // Skapa en instans av Calendar-klassen som håller tid och datum.
        val calendar = Calendar.getInstance()

        // Sätt tidsstämpeln på kalendern till den som finns i objektet (timestamp är tidpunkten då poängen skapades).
        calendar.timeInMillis = timestamp

        // Skapa en SimpleDateFormat-instans för att formatera datumet som en sträng.
        // Det angivna formatet är "yyyy-MM-dd HH:mm:ss".
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Returnera tidstämpeln som en sträng med det specificerade datumformatet.
        return dateFormat.format(calendar.time)
    }
}

