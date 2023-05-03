package com.chatapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chatapp.controller.LoginController
import com.chatapp.databinding.ActivityLoginBinding
import com.chatapp.interfaces.LoginInterface
import com.chatapp.model.User
import com.chatapp.utils.MySharedPreferences
import com.chatapp.utils.Utils.setTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private val TAG = LoginActivity::class.java.simpleName
    private var binding: ActivityLoginBinding? = null
    private var loginController: LoginController? = null
    private var preferences: MySharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initialise()
    }

    fun initialise() {
        setTitle(binding!!.toolbar.txtTitle, "Login")
        preferences = MySharedPreferences(this)
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        loginController = LoginController(auth, db)
        binding!!.txtSignUpNow.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    SignupActivity::class.java
                )
            )
        }
        binding!!.btnLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (isValid) {
                    showProgressbar()
                    loginController!!.login(
                        binding!!.edtPhoneEmail.text.toString(),
                        binding!!.edtPassword.text.toString(),
                        object : LoginInterface {
                            override fun onLoginSuccess(user: User?) {
                                Log.e(TAG, ">>>>> onLoginSuccess " + user!!.uid + ">> " + user.name)
                                preferences!!.saveUser(user)
                                hideProgressbar()
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        DashboardActivity::class.java
                                    )
                                )
                            }

                            override fun onLoginFailure(msg: String?) {
                                Log.e(TAG, ">>>>> onLoginFailure$msg")
                                hideProgressbar()
                                Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            }
        })
        binding!!.toolbar.ivBack.setOnClickListener { onBackPressed() }
    }

    val isValid: Boolean
        get() {
            if (binding!!.edtPhoneEmail.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter email Id.", Toast.LENGTH_SHORT).show()
                return false
            } else if (binding!!.edtPassword.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter password.", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }

    fun showProgressbar() {
        binding!!.progressBar.visibility = View.VISIBLE
    }

    fun hideProgressbar() {
        binding!!.progressBar.visibility = View.GONE
    }
}