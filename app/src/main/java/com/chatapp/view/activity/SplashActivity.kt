package com.chatapp.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chatapp.R
import com.chatapp.utils.MySharedPreferences
import java.lang.Exception

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var preferences: MySharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        preferences = MySharedPreferences(this)
        try {
            val user = preferences?.user
            if (user != null) {
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }catch (e:Exception){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}