package com.chatapp.interfaces

interface SendMessagesInterface {
    fun onSuccessMessageSent()
    fun onFailureMessageSent(msg: String?)
}