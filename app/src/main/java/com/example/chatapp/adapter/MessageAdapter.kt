package com.example.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.model.MessageData

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
    }


    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == "currentUserId") {
            SENT_MESSAGE
        } else {
            RECEIVED_MESSAGE
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun updateList(newMessages: List<MessageData>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun addMessage(newMessage: MessageData) {
        messages.add(newMessage)
        notifyItemInserted(messages.size - 1)
    }
}