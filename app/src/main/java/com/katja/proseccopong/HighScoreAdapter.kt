package com.katja.proseccopong

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HighScoreAdapter(private val highScores: List<Score>) : RecyclerView.Adapter<HighScoreAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankTextView: TextView = view.findViewById(R.id.rankTextView)
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_classic, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return highScores.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val score = highScores[position]

        // Assuming rank is the position in the list (1-based)
        holder.rankTextView.text = (position + 1).toString()
        holder.scoreTextView.text = score.score.toString()
        holder.nameTextView.text = score.name
        holder.dateTextView.text = score.getFormattedDate()
    }


}

