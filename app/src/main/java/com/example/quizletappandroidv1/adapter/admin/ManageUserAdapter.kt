package com.example.quizletappandroidv1.adapter.admin

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.entity.UserResponse

class ManageUserAdapter(
    private val onUserAdminClick: IUserAdminClick
) : RecyclerView.Adapter<ManageUserAdapter.UserViewHolder>() {
    private var userList: List<UserResponse> = mutableListOf()
    interface IUserAdminClick {
        fun handleEditClick(user: UserResponse)
        fun handleDeleteClick(user: UserResponse)
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.txtUserName)
        val userEmail: TextView = itemView.findViewById(R.id.txtUserEmail)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_admin, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.userName.text = user.userName
        holder.userEmail.text = user.email

        holder.btnEdit.setOnClickListener {
            onUserAdminClick.handleEditClick(user)
        }

        holder.btnDelete.setOnClickListener {
            onUserAdminClick.handleDeleteClick(user)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<UserResponse>) {
        this.userList = newList
        notifyDataSetChanged()
    }

}