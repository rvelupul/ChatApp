package com.chatapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chatapp.controller.LoginController
import com.chatapp.databinding.ActivitySignupBinding
import com.chatapp.interfaces.SignupInterface
import com.chatapp.interfaces.UserDetailsSaveInterface
import com.chatapp.model.User
import com.chatapp.utils.MySharedPreferences
import com.chatapp.utils.Utils.randomNumber
import com.chatapp.utils.Utils.setTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private val TAG = SignupActivity::class.java.simpleName
    private var binding: ActivitySignupBinding? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var loginController: LoginController? = null
    private var preferences: MySharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initialise()
    }

    private fun initialise() {
        setTitle(binding!!.toolbar.txtTitle, "Sign Up")
        preferences = MySharedPreferences(this)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        loginController = LoginController(auth!!, db!!)
        binding!!.btnSignup.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (isValid) {
                    showProgressbar()
                    loginController!!.register(
                        binding!!.edtEmail.text.toString(),
                        binding!!.edtPassword.text.toString(),
                        object : SignupInterface {
                            override fun onSignupComplete(user: FirebaseUser?) {
                                Log.e(
                                    TAG,
                                    ">>>>> onSignupComplete ::" + user!!.uid + ">>" + user.email
                                )
                                loginController!!.saveUserInfo(
                                    User(user.uid,user.email,binding!!.edtName.text.toString(),randomNumber),
                                    object : UserDetailsSaveInterface {
                                        override fun onUserSavedSuccess(user: User?) {
                                            Log.e(TAG, ">>>>> onUserSavedSuccess ")
                                            hideProgressbar()
                                            preferences!!.saveUser(user)
                                            startActivity(
                                                Intent(
                                                    this@SignupActivity,
                                                    DashboardActivity::class.java
                                                )
                                            )
                                        }

                                        override fun onUserSaveFailed(msg: String?) {
                                            Log.e(TAG, ">>>>> onUserSaveFailed ::$msg")
                                            hideProgressbar()
                                            Toast.makeText(
                                                this@SignupActivity,
                                                msg,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }

                            override fun onSignupError(msg: String?) {
                                Log.e(TAG, ">>>>> onSignupError ::$msg")
                                hideProgressbar()
                                Toast.makeText(this@SignupActivity, msg, Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            }
        })
    }

    private val isValid: Boolean
        private get() {
            if (binding!!.edtName.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter name.", Toast.LENGTH_SHORT).show()
                return false
            } else if (binding!!.edtEmail.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter email Id.", Toast.LENGTH_SHORT).show()
                return false
            } else if (binding!!.edtPassword.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter password.", Toast.LENGTH_SHORT).show()
                return false
            } else if (binding!!.edtConfirmPassword.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter confirm password.", Toast.LENGTH_SHORT).show()
                return false
            } else if (!binding!!.edtPassword.text.toString()
                    .equals(binding!!.edtConfirmPassword.text.toString())
            ) {
                Toast.makeText(
                    this,
                    "Password and confirm password not matched.",
                    Toast.LENGTH_SHORT
                ).show()
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