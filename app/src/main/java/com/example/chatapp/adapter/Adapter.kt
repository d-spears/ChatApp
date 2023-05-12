package com.example.chatapp.adapter

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.activity.ChatActivity
import com.example.chatapp.R
import com.example.chatapp.model.User

class Adapter(private val context: Context) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    private val users = HashMap<String, User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.user_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users.values.elementAt(position)
        holder.nameView.text = user.name
        Glide.with(context)
            .load(user.image)
            .circleCrop()
            .placeholder(R.drawable.user)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java).apply {
                putExtra("userName", user.name)
                putExtra("receiverId", user.userID)
                putExtra("image", user.image)
                putExtra("firebaseToken", user.firebaseToken)
            }
            context.startActivity(intent)

        }
    }

    override fun getItemCount() = users.size

    fun setData(users: HashMap<String, User>, loggedInUserId: String) {
        val filteredUsers = users.filterKeys { it != loggedInUserId }
        this.users.clear()
        this.users.putAll(filteredUsers)
        notifyDataSetChanged()
    }

    fun getUser(userId: String): User? {
        return users[userId]
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.user_list_full_name)
        val imageView: ImageView = itemView.findViewById(R.id.user_list_image)
    }
}