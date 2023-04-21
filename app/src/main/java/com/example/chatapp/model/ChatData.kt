package com.example.chatapp.model

class ChatData() {
    var chatID: String? = null
    var name: String? = null
    var email: String? = null
    var image: String? = null

    constructor(chatID: String, name: String, email: String, imageUrl: String) : this() {
        this.chatID = chatID
        this.name = name
        this.email = email
        this.image = imageUrl
    }
}