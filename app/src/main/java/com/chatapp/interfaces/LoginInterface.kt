package com.chatapp.interfaces

import com.chatapp.model.User

interface LoginInterface {
    fun onLoginSuccess(user: User?)
    fun onLoginFailure(msg: String?)
}