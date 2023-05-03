package com.chatapp.utils

import android.text.format.DateFormat
import android.widget.TextView
import com.chatapp.model.User
import java.util.Calendar
import java.util.Locale
import java.util.Random

object Utils {
    // It will generate 6 digit random Number.
    // from 0 to 999999
    val randomNumber: Long
        get() {
            // It will generate 6 digit random Number.
            // from 0 to 999999
            val rnd = Random()
            return rnd.nextInt(999999).toLong()
        }

    fun getChatId(user: User, otherUser: User): String {
        val id = user.clientId
        val otherId = otherUser.clientId
        return if (id!! < otherId!!) {
            id.toString() + "_" + otherId
        } else {
            otherId.toString() + "_" + id
        }
    }

    fun setTitle(txtTitle: TextView, title: String?) {
        txtTitle.text = title
    }

    @JvmStatic
    fun getDate(time: Long): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = time
        return DateFormat.format("dd-MM-yy hh:mm", cal).toString()
    }
}