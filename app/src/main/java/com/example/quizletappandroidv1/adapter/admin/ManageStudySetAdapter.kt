package com.example.quizletappandroidv1.adapter.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.models.StudySetModel

class ManageStudySetAdapter(
    private val onAdminStudySetClick: IAdminStudySetClick,
) : RecyclerView.Adapter<ManageStudySetAdapter.StudySetViewHolder>() {

    private val studySetList: List<StudySetModel> = mutableListOf()

    interface IAdminStudySetClick {
        fun handleEditClick(studySet: StudySetModel)
        fun handleDeleteClick(studySet: StudySetModel)
    }

    class StudySetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studySetName: TextView = itemView.findViewById(R.id.txtStudySetName)
        val studySetDescription: TextView = itemView.findViewById(R.id.txtStudySetDescription)
        val btnEditStudySet: Button = itemView.findViewById(R.id.btnEditStudySet)
        val btnDeleteStudySet: Button = itemView.findViewById(R.id.btnDeleteStudySet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudySetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_studyset, parent, false)
        return StudySetViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudySetViewHolder, position: Int) {
        val studySet = studySetList[position]
        holder.studySetName.text = studySet.name
        holder.studySetDescription.text = studySet.description

        holder.btnEditStudySet.setOnClickListener {
            onAdminStudySetClick.handleEditClick(studySet)
        }

        holder.btnDeleteStudySet.setOnClickListener {
            onAdminStudySetClick.handleDeleteClick(studySet)
        }
    }

    override fun getItemCount(): Int {
        return studySetList.size
    }
}
