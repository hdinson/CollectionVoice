package com.zjta.collectionvoice.utils

import com.tencent.mmkv.MMKV

object MMKVUtils {

    private val sAppConfig by lazy { MMKV.mmkvWithID("app_config") }
    private val sUser by lazy { MMKV.mmkvWithID("user") }

    private const val KEY_USER_ID = "userId"
    private const val KEY_USER_PHONE = "phone"
    private const val KEY_USER_TOKEN = "token"


    fun setUserId(userId: String) {
        sUser.encode(KEY_USER_ID, userId)
    }

    fun getUserId(): String {
        return sUser.decodeString(KEY_USER_ID, "")
    }


    fun setUserName(phone: String) {
        sAppConfig.encode(KEY_USER_PHONE, phone)
    }

    fun getUserName(): String {
        return sAppConfig.decodeString(KEY_USER_PHONE, "")
    }


    fun setUserToken(token: String) {
        sAppConfig.encode(KEY_USER_TOKEN, token)
    }

    fun getUserToken(): String {
        return sAppConfig.decodeString(KEY_USER_TOKEN, "")
    }

    fun removeUserInfo() {
        sUser.clearAll()
    }
}