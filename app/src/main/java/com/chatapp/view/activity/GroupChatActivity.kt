package com.chatapp.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatapp.adapters.ChatListAdapter
import com.chatapp.controller.ChatController
import com.chatapp.databinding.ActivityGroupChatBinding
import com.chatapp.interfaces.SendMessagesInterface
import com.chatapp.model.MessageModel
import com.chatapp.model.User
import com.chatapp.utils.MySharedPreferences
import com.chatapp.utils.Utils.getChatId
import com.chatapp.utils.Utils.setTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Collections

class GroupChatActivity : AppCompatActivity() {
    private val TAG = GroupChatActivity::class.java.simpleName
    private var binding: ActivityGroupChatBinding? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var otherUser: User? = null
    private var user: User? = null
    private var chatId: String? = null
    private var preferences: MySharedPreferences? = null
    private var chatController: ChatController? = null
    private var chatListAdapter: ChatListAdapter? = null
    private val messageModels = ArrayList<MessageModel?>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initialise()
    }

    private fun initialise() {
        preferences = MySharedPreferences(this)
        user = preferences!!.user
        otherUser = intent.getParcelableExtra("user")
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        chatController = ChatController(auth!!, db!!)
        chatId = getChatId(user!!, otherUser!!)
        chatListAdapter = ChatListAdapter(user!!.uid!!, messageModels)
        binding!!.rvMessagesList.layoutManager = LinearLayoutManager(this)
        binding!!.rvMessagesList.adapter = chatListAdapter
        setTitle(binding!!.toolbar.txtTitle, otherUser!!.name)
        binding?.toolbar?.ivBack?.setOnClickListener { onBackPressed() }
        Log.e(">>>>>", "Chat Id ::$chatId")

        binding!!.layoutChatInput.ivSend.setOnClickListener {
            val messageModel = MessageModel()
            messageModel.senderId = user?.uid
            messageModel.msg = binding?.layoutChatInput?.edtChatText?.text.toString()
            messageModel.messageId = System.nanoTime().toString()
            messageModel.messageType = 0L
            messageModel.timeStamp = System.currentTimeMillis().toString()
            messageModel.imageUrl = ""
            messageModel.chatId =
                "mainChat" // Replace "mainChat" with the ID of your group chat document.
            binding?.layoutChatInput?.edtChatText?.setText("")
            chatController!!.sendMessage(messageModel, object : SendMessagesInterface {
                override fun onSuccessMessageSent() {
                    Log.e(TAG, ">>>>> onSuccessMessageSent")
                }

                override fun onFailureMessageSent(msg: String?) {
                    Log.e(TAG, ">>>>> onFailureMessageSent$msg")
                    Toast.makeText(this@GroupChatActivity, msg, Toast.LENGTH_SHORT).show()
                }
            })
        }


        binding!!.rvMessagesList.adapter = chatListAdapter

        db?.collection("messages")
            ?.orderBy("timeStamp")
            ?.addSnapshotListener(EventListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    return@EventListener
                }
                messageModels.clear()
                for (doc in value!!.documents) {
                    val message = doc.toObject(MessageModel::class.java)
                    db?.collection("users")!!
                        .whereEqualTo("uid", message!!.senderId)
                        .get()
                        .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                            for (documentSnapshot in queryDocumentSnapshots) {
                                Log.e(
                                    "user.getEmail() ",
                                    user?.email + " email " + documentSnapshot.getString("email")
                                )
                                if (user?.email != documentSnapshot.getString("email")) {
                                    val senderName = documentSnapshot.getString("name")
                                    message.senderId = senderName
                                }
                            }
                            messageModels.add(message)
                            chatListAdapter?.notifyDataSetChanged()
                            binding?.rvMessagesList?.scrollToPosition(messageModels.size - 1)
                        }
                        .addOnFailureListener { e: Exception? -> }
                    Log.e("messages f ", message.msg!!)
                }
                messageModels.reverse()
                Log.e("chatController ", messageModels.size.toString() + "")
                // add the message to your adapter or list
            })
    }
}