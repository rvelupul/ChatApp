package com.chatapp.interfaces

import com.chatapp.model.User

interface UserItemClickedInterface {
    fun onUserItemClicked(user: User?)
}