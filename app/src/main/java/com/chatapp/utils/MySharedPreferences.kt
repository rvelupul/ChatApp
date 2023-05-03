package com.chatapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.chatapp.model.User
import com.google.gson.Gson

class MySharedPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("ChatApp", Context.MODE_PRIVATE)
    }

    fun saveUser(user: User?) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER, Gson().toJson(user))
        editor.apply()
    }

    val user: User
        get() = Gson().fromJson(sharedPreferences.getString(KEY_USER, ""), User::class.java)

    companion object {
        private const val KEY_USER = "key_user"
    }
}