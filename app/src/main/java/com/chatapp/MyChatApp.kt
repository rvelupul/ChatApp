package com.chatapp

import android.app.Application
import com.chatapp.utils.MySharedPreferences

class MyChatApp : Application() {
    var preferences: MySharedPreferences? = null
    override fun onCreate() {
        super.onCreate()
        preferences = MySharedPreferences(this)
    }
}