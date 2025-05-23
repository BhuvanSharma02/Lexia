package com.example.lexia

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class ButtonAdapter(
    private val buttonCount: Int,
    private val videoUrls: List<String>,
    private val sharedPreferences: SharedPreferences // Pass SharedPreferences to the adapter
) : RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder>() {

    // List to track button states (true = clicked, false = not clicked)
    private val buttonStates = MutableList(buttonCount) { position ->
        sharedPreferences.getBoolean("button_$position", false) // Load saved state
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.button.text = "${position + 1}"

        // Set button color based on saved state
        if (buttonStates[position]) {
            setButtonBackground(holder.button, "#90EE90") // Green background for clicked state
        } else {
            setButtonBackground(holder.button, "#FFFFFF") // White background for default state
        }

        // Handle button click
        holder.button.setOnClickListener {
            // Update the button state
            buttonStates[position] = true
            notifyItemChanged(position) // Refresh the button

            // Save the updated state in SharedPreferences
            saveButtonState(position, true)

            // Start the VideoPlayerActivity
            val intent = Intent(holder.itemView.context, VideoPlayerActivity::class.java)
            intent.putExtra("VIDEO_URL", videoUrls[position % videoUrls.size]) // Pass the video URL

            // Set orientation to landscape
            (holder.itemView.context as AppCompatActivity).requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            // Start the activity
            holder.itemView.context.startActivity(intent)
        }

        holder.button.setOnLongClickListener {
            // Revert the button state
            buttonStates[position] = false
            notifyItemChanged(position) // Refresh the button

            // Save the updated state in SharedPreferences
            saveButtonState(position, false)

            true // Return true to indicate the long press is consumed
        }
    }

    override fun getItemCount(): Int {
        return buttonCount
    }

    // Save the button state in SharedPreferences
    private fun saveButtonState(position: Int, isClicked: Boolean) {
        sharedPreferences.edit().putBoolean("button_$position", isClicked).apply()
    }

    private fun setButtonBackground(button: Button, color: String) {
        val background = button.background as GradientDrawable
        background.setColor(android.graphics.Color.parseColor(color)) // Set the new color
    }

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.button_item)
    }
}