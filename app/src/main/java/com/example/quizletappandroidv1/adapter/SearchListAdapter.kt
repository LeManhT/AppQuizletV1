package com.example.quizletappandroidv1.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.appquizlet.model.SearchSetModel
import com.example.quizletappandroidv1.R

class SearchListAdapter(
    private var onSearchSetClick: ISearchSetClick? = null
) :
    RecyclerView.Adapter<SearchListAdapter.ItemSearchBookHolder>() {

    interface ISearchSetClick {
        fun handleSearchSetClick(studyset: SearchSetModel)
    }

    private var listStudysets: List<SearchSetModel> = mutableListOf()

    inner class ItemSearchBookHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtStudySetSearch: TextView = itemView.findViewById(R.id.txtStudySetSearch)
        val imgStudySetAvatarSearch: ImageView = itemView.findViewById(R.id.imgStudySetAvatarSearch)
        val txtStudySetUsername: TextView = itemView.findViewById(R.id.txtStudySetUsername)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSearchBookHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_set_layout_item, parent, false)
        return ItemSearchBookHolder(view)
    }

    override fun onBindViewHolder(holder: ItemSearchBookHolder, position: Int) {
        val studyset = listStudysets[position]
        holder.txtStudySetSearch.text = studyset.name
        holder.txtStudySetUsername.text = studyset.idOwner
//        if (studyset.image?.isNotEmpty() == true) {
//            Glide.with(holder.itemView.context)
//                .load(studyset.image)
//                .apply(
//                    RequestOptions()
//                        .centerCrop()
//                        .fitCenter()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                )
//                .into(holder.imgStudySetAvatarSearch)
//        } else {
            Glide.with(holder.itemView.context)
                .load(R.raw.owl_default_avatar)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(holder.imgStudySetAvatarSearch)
//        }


        holder.itemView.setOnClickListener {
            onSearchSetClick?.handleSearchSetClick(studyset)
        }

    }

    override fun getItemCount(): Int {
        return listStudysets.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<SearchSetModel>) {
        this.listStudysets = newList
        notifyDataSetChanged()
    }

}