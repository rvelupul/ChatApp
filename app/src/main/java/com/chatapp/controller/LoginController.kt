package com.chatapp.controller

import com.chatapp.interfaces.LoginInterface
import com.chatapp.interfaces.SignupInterface
import com.chatapp.interfaces.UserDetailsSaveInterface
import com.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Objects

class LoginController(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {
    fun register(email: String?, password: String?, signupInterface: SignupInterface) {
        auth.createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                signupInterface.onSignupComplete(task.result.user)
            } else {
                signupInterface.onSignupError(task.exception!!.localizedMessage)
            }
        }
    }

    fun saveUserInfo(user: User?, userDetailsSaveInterface: UserDetailsSaveInterface) {
        db.collection("users")
            .add(user!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userDetailsSaveInterface.onUserSavedSuccess(user)
                } else {
                    userDetailsSaveInterface.onUserSaveFailed(Objects.requireNonNull(task.exception)?.localizedMessage)
                }
            }
    }

    fun login(email: String?, password: String?, loginInterface: LoginInterface) {
        auth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.collection("users")
                        .whereEqualTo("uid", task.result.user!!.uid)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val documentSnapshot = task.result.documents[0]
                                val name = documentSnapshot["name"] as String?
                                val uid = documentSnapshot["uid"] as String?
                                val email = documentSnapshot["email"] as String?
                                val clientId = documentSnapshot["clientId"] as Long?
                                loginInterface.onLoginSuccess(User(uid,email,name,clientId))
                            } else {
                                loginInterface.onLoginFailure(Objects.requireNonNull(task.exception)?.localizedMessage)
                            }
                        }
                } else {
                    loginInterface.onLoginFailure(Objects.requireNonNull(task.exception)?.localizedMessage)
                }
            }
    }
}