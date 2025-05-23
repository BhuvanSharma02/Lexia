package com.example.lexia

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JourneyAdapter(
    private val journeys: List<Journey>
) : RecyclerView.Adapter<JourneyAdapter.JourneyViewHolder>() {

    inner class JourneyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.journeyTitle)
        val range: TextView = view.findViewById(R.id.videoRange)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_journey, parent, false)
        return JourneyViewHolder(view)
    }

    override fun onBindViewHolder(holder: JourneyViewHolder, position: Int) {
        val journey = journeys[position]
        holder.title.text = journey.title
        holder.range.text = journey.range
        holder.itemView.setBackgroundResource(journey.backgroundResId)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, JourneyDetailActivity::class.java).apply {
                putExtra("START", journey.start)
                putExtra("END", journey.end)
                putExtra("BACKGROUND_RES_ID", journey.backgroundResId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = journeys.size
}
