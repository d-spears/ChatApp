package com.example.chatapp.model

data class ChatData(
    var name: String? = null,
    var email: String? = null,
    var image: String? = null,
    var uid: String? = null
) /*{
    constructor(chatID: String, name: String, email: String, imageUrl: String, uid: String) : this() {
        this.chatID = chatID
        this.name = name
        this.email = email
        this.image = imageUrl
        this.uid = uid
    }
}*/