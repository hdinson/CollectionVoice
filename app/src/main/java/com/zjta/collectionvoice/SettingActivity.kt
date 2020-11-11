package com.zjta.collectionvoice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dinson.blingbase.kotlin.click
import com.dinson.blingbase.retrofit.RxSchedulers
import com.dinson.blingbase.utils.SystemBarModeUtils
import com.zjta.collectionvoice.http.HttpHelper
import com.zjta.collectionvoice.http.HttpObserver
import com.zjta.collectionvoice.utils.MMKVUtils
import com.zjta.collectionvoice.widget.DialogCancelConfirm
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        SystemBarModeUtils.darkMode(this, true)


        vResetPwd.click { ResetPwdActivity.start(this) }
        vDoLogout.click {
            DialogCancelConfirm(this)
                .setMessage("您确定要退出么?")
                .setButtonText("取消", "确定")
                .setOperationListener { dialog, isLeft ->
                    dialog.dismiss()
                    if (isLeft.not()) {
                        doLogOut()
                    }
                }.show()
        }
    }

    private fun doLogOut() {
        HttpHelper.appApi.doLoginOut(MMKVUtils.getUserId())
            .compose(RxSchedulers.io_main())
            .subscribe(object : HttpObserver<String>() {
                override fun onHandleSuccess(t: String?, message: String) {
                }

                override fun onHandleFinal() {
                    SplashActivity.logout(this@SettingActivity)
                }
            })
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingActivity::class.java))
        }
    }
}