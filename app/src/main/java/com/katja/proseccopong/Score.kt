package com.katja.proseccopong

// Bool True for classic score and False for Prosecco score
class Score(val name: String, var score: Int, val classic: Boolean) {
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
}
