package com.zjta.collectionvoice.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class LoginUser(
    val agentId: Int = 0,
    val userId: String = "",
    val token: String = ""
) : Parcelable