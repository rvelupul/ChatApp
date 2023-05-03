package com.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chatapp.R
import com.chatapp.adapters.UsersListAdapter.UserViewHolder
import com.chatapp.interfaces.UserItemClickedInterface
import com.chatapp.model.User

class UsersListAdapter(
    private val users: ArrayList<User?>,
    private val userItemClickedInterface: UserItemClickedInterface
) : RecyclerView.Adapter<UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.tvName.text = users[position]?.name
        holder.tvEmail.text = users[position]?.email
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tvName: TextView
         val tvEmail: TextView

        init {
            tvName = itemView.findViewById(R.id.tvName)
            tvEmail = itemView.findViewById(R.id.tvEmail)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            userItemClickedInterface.onUserItemClicked(users[adapterPosition])
        }
    }
}