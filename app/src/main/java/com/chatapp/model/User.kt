package com.chatapp.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User(
    var uid: String? = null,
    var email: String? = null,
    var name: String? = null,
    var clientId: Long? = null
) : Parcelable
