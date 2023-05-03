package com.chatapp.interfaces

import com.google.firebase.auth.FirebaseUser

interface SignupInterface {
    fun onSignupComplete(user: FirebaseUser?)
    fun onSignupError(msg: String?)
}