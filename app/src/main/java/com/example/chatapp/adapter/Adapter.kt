package com.example.chatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.ChatActivity
import com.example.chatapp.R
import com.example.chatapp.model.ChatData

class Adapter(
    private val context: Context,
    private var users: HashMap<String, ChatData>) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater.inflate(R.layout.user_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users.values.elementAt(position)
        holder.nameView.text = user.name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java).apply {
                //putExtra("userId", user.)
                putExtra("userName", user.name)
                // Add any other relevant data here
            }
            context.startActivity(intent)
        }
        Glide.with(context)
            .load(R.drawable.user)
            .placeholder(R.drawable.user)
            .into(holder.imageView)
    }

    override fun getItemCount() = users.size

    fun setData(users: HashMap<String, ChatData>) {
        this.users = users
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.user_list_full_name)
        val imageView: AppCompatImageView = itemView.findViewById(R.id.user_list_image)
    }
}