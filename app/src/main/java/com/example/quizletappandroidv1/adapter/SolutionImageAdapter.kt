package com.example.quizletappandroidv1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletappandroidv1.R

class SolutionImageAdapter(
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<SolutionImageAdapter.ImageViewHolder>() {
    private var imageResources: List<Int> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageResource = imageResources[position]
        holder.bind(imageResource)
    }

    override fun getItemCount(): Int = imageResources.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageThumbnail: ImageView = itemView.findViewById(R.id.imageThumbnail)

        fun bind(imageResource: Int) {
            imageThumbnail.setImageResource(imageResource)
            imageThumbnail.setOnClickListener {
                onImageClick(imageResource)
            }
        }
    }

    fun updateData(newImageResources: List<Int>) {
        imageResources = newImageResources
        notifyDataSetChanged()
    }
}
