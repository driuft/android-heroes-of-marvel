package com.driuft.heroesofmarvel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HeroAdapter(private val heroList: List<Hero>) : RecyclerView.Adapter<HeroAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val heroPortrait: ImageView
        val heroName: TextView
        val heroDescription: TextView

        init {
            heroPortrait = view.findViewById(R.id.hero_portrait)
            heroName = view.findViewById(R.id.hero_name)
            heroDescription = view.findViewById(R.id.hero_description)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hero_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(heroList[position].imageUrl)
            .centerCrop()
            .into(holder.heroPortrait)

        holder.heroName.text = heroList[position].name
        holder.heroDescription.text = if (heroList[position].description != "") {
            heroList[position].description
        } else { "No description provided." }

        holder.heroPortrait.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Hero ID: ${heroList[position].id}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = heroList.size
}