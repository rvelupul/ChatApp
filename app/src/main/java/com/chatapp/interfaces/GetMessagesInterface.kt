package com.chatapp.interfaces

import com.chatapp.model.MessageModel

interface GetMessagesInterface {
    fun onMessagesReceived(messages: List<MessageModel?>?)
}