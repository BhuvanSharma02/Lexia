package com.example.lexia

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lexia.databinding.ActivityDifficultyBinding
import com.example.lexia.databinding.ItemDifficultyBinding
import kotlinx.parcelize.Parcelize

@Parcelize
data class Difficulty(
    val name: String,
    val color: Int,
    val levels: List<Level>
) : Parcelable

@Parcelize
data class Level(
    val number: Int,
    val words: List<String>
) : Parcelable

class DifficultyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDifficultyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDifficultyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val difficulties = listOf(
            Difficulty("Easy", android.R.color.holo_green_light,
                List(5) { Level(it + 1, listOf("Cat", "Dog", "Sun")) }),
            Difficulty("Medium", android.R.color.holo_orange_light,
                List(5) { Level(it + 1, listOf("Elephant", "Computer")) }),
            Difficulty("Hard", android.R.color.holo_red_light,
                List(5) { Level(it + 1, listOf("Extravaganza")) })
        )

        // Fix: Add LayoutManager here
        binding.difficultyRecycler.layoutManager = LinearLayoutManager(this)
        binding.difficultyRecycler.adapter = DifficultyAdapter(difficulties) { difficulty ->
            startActivity(Intent(this, LevelSelectionActivity::class.java).apply {
                putExtra("difficulty", difficulty)
            })
        }
    }
}

class DifficultyAdapter(
    private val items: List<Difficulty>,
    private val onClick: (Difficulty) -> Unit
) : RecyclerView.Adapter<DifficultyAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDifficultyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemDifficultyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.difficultyName.text = items[position].name
        holder.binding.root.setOnClickListener { onClick(items[position]) }
    }

    override fun getItemCount() = items.size
}