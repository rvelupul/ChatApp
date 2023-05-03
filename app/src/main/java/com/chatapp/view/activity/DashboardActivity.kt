package com.chatapp.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.chatapp.adapters.UsersListAdapter
import com.chatapp.controller.ChatController
import com.chatapp.databinding.ActivityDashboardBinding
import com.chatapp.interfaces.GetUsersInterface
import com.chatapp.interfaces.UserItemClickedInterface
import com.chatapp.model.User
import com.chatapp.utils.MySharedPreferences
import com.chatapp.utils.Utils.setTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {
    private var binding: ActivityDashboardBinding? = null
    private var chatController: ChatController? = null
    private var auth: FirebaseAuth? = null
    private val userList = ArrayList<User?>()
    private var adapter: UsersListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding?.btnLogout?.setOnClickListener {
            val preferences = MySharedPreferences(this@DashboardActivity)
            preferences.saveUser(null)
            startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
            finish()
        }
        binding!!.btnChatRoom.setOnClickListener {
            val builder = AlertDialog.Builder(this@DashboardActivity)
            builder.setTitle("Enter Chat Room ID")
            val input = EditText(this@DashboardActivity)
            // input.setText("246439")
            input.hint = "Please enter chat room id"
            builder.setView(input)
            val preferences = MySharedPreferences(this@DashboardActivity)
            val user = preferences.user
            builder.setPositiveButton(
                "OK"
            ) { dialog, which ->
                val chatRoomId = input.text.toString().trim { it <= ' ' }
                if (chatRoomId == "246439") {
                    val intent = Intent(this@DashboardActivity, GroupChatActivity::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Wrong chat room ID!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.cancel() }
            builder.show()
        }
        initialise()
    }

    private fun initialise() {
        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        chatController = ChatController(auth!!, db)
        users
        setTitle(binding!!.toolbar.txtTitle, "Chat List")
        binding!!.toolbar.ivBack.setOnClickListener { onBackPressed() }
        binding!!.swipeRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                users
            }
        })
        adapter = UsersListAdapter(userList, object : UserItemClickedInterface {
            override fun onUserItemClicked(user: User?) {
            }
        })
        binding!!.rvUsers.layoutManager = LinearLayoutManager(this)
        binding!!.rvUsers.adapter = adapter
    }

    private val users: Unit
        private get() {
            showProgressbar()
            val user = auth!!.currentUser
            if (user != null) chatController!!.getUsers(user.uid, object : GetUsersInterface {
                @SuppressLint("NotifyDataSetChanged")
                override fun onUsersFetchSuccess(users: List<User?>?) {
                    Log.e(">>>>>", "Users ::" + users.toString())
                    hideProgressbar()
                    binding!!.swipeRefreshLayout.isRefreshing = false
                    userList.clear()
                    userList.addAll(users!!)
                    adapter!!.notifyDataSetChanged()
                }

                override fun onUserFetchError(msg: String?) {
                    Log.e(">>>>>", "onUserFetchError ::")
                    hideProgressbar()
                    Toast.makeText(this@DashboardActivity, msg, Toast.LENGTH_SHORT).show()
                }
            })
            //   hideProgressbar()
        }

    fun showProgressbar() {
        binding!!.progressBar.visibility = View.VISIBLE
    }

    fun hideProgressbar() {
        binding!!.progressBar.visibility = View.GONE
    }
}