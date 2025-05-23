package com.example.lexia

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lexia.databinding.ActivityLevelSelectionBinding
import com.example.lexia.databinding.ItemLevelBinding


class LevelSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLevelSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLevelSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val difficulty = intent.getParcelableExtra<Difficulty>("difficulty")!!

        // Correct way to access views through binding
        binding.difficultyTitle.text = difficulty.name
        binding.difficultyTitle.setTextColor(ContextCompat.getColor(this, difficulty.color))

        binding.levelsRecycler.layoutManager = GridLayoutManager(this, 3)
        binding.levelsRecycler.adapter = LevelAdapter(difficulty.levels) { level ->
            startActivity(Intent(this, WordQuizActivity::class.java).apply {
                putExtra("level", level)
                putExtra("difficultyColor", difficulty.color)
            })
        }
    }
}

class LevelAdapter(
    private val items: List<Level>,
    private val onClick: (Level) -> Unit
) : RecyclerView.Adapter<LevelAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLevelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemLevelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.levelNumber.text = items[position].number.toString()
        holder.binding.root.setOnClickListener { onClick(items[position]) }
    }

    override fun getItemCount() = items.size
}