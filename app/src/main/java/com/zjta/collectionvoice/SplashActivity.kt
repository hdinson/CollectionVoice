package com.zjta.collectionvoice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.dinson.blingbase.retrofit.RxSchedulers
import com.zjta.collectionvoice.bean.StopSocket
import com.zjta.collectionvoice.utils.MMKVUtils
import io.reactivex.Observable
import org.greenrobot.eventbus.EventBus
import kotlin.system.exitProcess


class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        jumpToNextActivity()
    }


    private fun jumpToNextActivity() {
        val userId = MMKVUtils.getUserId()
        if (userId.isEmpty()) LoginActivity.start(this)
        else MainActivity.start(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag = intent.getStringExtra(EXTRA_TAG)
        if (tag != null && !TextUtils.isEmpty(tag)) {
            when (tag) {
                EXIT -> finish()//退出程序
                OTHER_USER -> LoginActivity.startWithDialog(this)
                LOGOUT -> LoginActivity.start(this)
            }
        } else {
            jumpToNextActivity()
        }
    }

    companion object {

        private const val EXTRA_TAG = "extra_tag"

        private const val EXIT = "exit"
        private const val LOGOUT = "logout"
        private const val OTHER_USER = "other_user"

        fun exitSystem(context: Context) {
            val starter = Intent(context, SplashActivity::class.java)
            starter.putExtra(EXTRA_TAG, EXIT)
            context.startActivity(starter)
        }

        /**
         * 正常退出登录
         */
        fun logout(context: Context) {
            EventBus.getDefault().post(StopSocket())
            MMKVUtils.removeUserInfo()
            val starter = Intent(context, SplashActivity::class.java)
            starter.putExtra(EXTRA_TAG, LOGOUT)
            context.startActivity(starter)
        }

        /**
         * 异地登录
         */
        fun otherLogout(context: Context) {
            EventBus.getDefault().post(StopSocket())
            MMKVUtils.removeUserInfo()
            val starter = Intent(context, SplashActivity::class.java)
            starter.putExtra(EXTRA_TAG, OTHER_USER)
            context.startActivity(starter)
        }
    }
}