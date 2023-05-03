package com.chatapp.interfaces

import com.chatapp.model.User

interface GetUsersInterface {
    fun onUsersFetchSuccess(users: List<User?>?)
    fun onUserFetchError(msg: String?)
}