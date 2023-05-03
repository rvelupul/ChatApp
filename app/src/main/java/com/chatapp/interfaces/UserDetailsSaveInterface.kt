package com.chatapp.interfaces

import com.chatapp.model.User

interface UserDetailsSaveInterface {
    fun onUserSavedSuccess(user: User?)
    fun onUserSaveFailed(msg: String?)
}