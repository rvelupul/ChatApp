package com.chatapp.controller

import android.util.Log
import com.chatapp.interfaces.GetMessagesInterface
import com.chatapp.interfaces.GetUsersInterface
import com.chatapp.interfaces.SendMessagesInterface
import com.chatapp.model.MessageModel
import com.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Objects

class ChatController(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {
    private val TAG = ChatController::class.java.simpleName
    fun getUsers(uid: String?, getUsersInterface: GetUsersInterface) {
        db.collection("users")
            .whereNotEqualTo("uid", uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val users = ArrayList<User?>()
                    for (doc in task.result) {
                        val name = doc["name"] as String?
                        val uid = doc["uid"] as String?
                        val email = doc["email"] as String?
                        val clientId = doc["clientId"] as Long?
                        users.add(User(uid,email,name,clientId))
                        getUsersInterface.onUsersFetchSuccess(users)
                    }
                } else {
                    getUsersInterface.onUserFetchError(Objects.requireNonNull(task.exception)?.localizedMessage)
                }
            }
    }

    fun getMessages(chatId: String?, getMessagesInterface: GetMessagesInterface) {
        db.collection("messages")
            .whereEqualTo("chatId", chatId)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e(TAG, ">>>>> " + error.localizedMessage)
                } else {
                    val messageModels = java.util.ArrayList<MessageModel?>()
                    assert(value != null)
                    for (doc in value!!) {
                        val messageModel = MessageModel()
                        messageModel.messageId = doc.getString("messageId")
                        messageModel.chatId = doc.getString("chatId")
                        messageModel.messageType = doc["messageType"] as Long?
                        messageModel.msg = doc.getString("msg")
                        messageModel.timeStamp = doc.getString("timeStamp")
                        messageModel.imageUrl = doc.getString("imageUrl")
                        messageModel.senderId = doc.getString("senderId")
                        messageModels.add(messageModel)
                    }
                    getMessagesInterface.onMessagesReceived(messageModels)
                }
            }
    }

    fun sendMessage(messageModel: MessageModel?, sendMessagesInterface: SendMessagesInterface) {
        db.collection("messages")
            .add(messageModel!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendMessagesInterface.onSuccessMessageSent()
                } else {
                    sendMessagesInterface.onFailureMessageSent(Objects.requireNonNull(task.exception)?.localizedMessage)
                }
            }
    }
}