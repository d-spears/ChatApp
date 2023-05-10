package com.example.chatapp.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.model.MessageData
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val messages: MutableList<MessageData>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val SENT_MESSAGE = 0
    private val RECEIVED_MESSAGE = 1

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
        val userImage: ImageView = itemView.findViewById(R.id.imageView!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = when (viewType) {
            SENT_MESSAGE -> layoutInflater.inflate(R.layout.message_sent, parent, false)
            RECEIVED_MESSAGE -> layoutInflater.inflate(R.layout.message_received, parent, false)
            else -> layoutInflater.inflate(R.layout.message_received, parent, false)
        }
        return MessageViewHolder(view)
    }


    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.messageText

        if (message.senderId == "currentUserId") {
            // Message was sent by the current user
            holder.userImage.visibility = View.GONE
            holder.messageText.gravity = Gravity.END
        } else {
            // Message was received by the current user
            holder.userImage.visibility = View.VISIBLE
            holder.messageText.gravity = Gravity.START
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            SENT_MESSAGE
        } else {
            RECEIVED_MESSAGE
        }
    }


    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: MessageData) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}