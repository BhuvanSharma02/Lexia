package com.example.lexia

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity

class Categories : AppCompatActivity() {

    private val categories = listOf("Fruits", "Vegetables", "Animals", "Shapes", "Vehicle")

    private val icons = listOf(
        R.drawable.ic_fruit,
        R.drawable.ic_vegetable,
        R.drawable.animals,
        R.drawable.shapes,
        R.drawable.vehicle
    )

    // Add distinct colors for each category (you can use getColor(R.color.someColor) too)
    private val colors = listOf(
        "#F86C6C", // Yellow for Fruits
        "#8BC34A",  // Green for Vegetables
        "#fab07f",
        "#a5fca2",
        "#55b5e6"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        val gridView = findViewById<GridView>(R.id.categoriesGrid)

        gridView.adapter = object : BaseAdapter() {
            override fun getCount(): Int = categories.size

            override fun getItem(position: Int): Any = categories[position]

            override fun getItemId(position: Int): Long = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = layoutInflater.inflate(R.layout.item_category, parent, false)
                val icon = view.findViewById<ImageView>(R.id.categoryIcon)
                val name = view.findViewById<TextView>(R.id.categoryName)
                val card = view.findViewById<View>(R.id.categoryCard)

                icon.setImageResource(icons[position])
                name.text = categories[position]
                val background = ContextCompat.getDrawable(this@Categories, R.drawable.category_background)?.mutate()
                background?.setTint(Color.parseColor(colors[position]))
                card.background = background


                return view
            }
        }

        gridView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, GameButtonGridActivity::class.java)
            intent.putExtra("CATEGORY", categories[position])
            startActivity(intent)
        }
    }
}
