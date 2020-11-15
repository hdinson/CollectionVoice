package com.zjta.collectionvoice

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dinson.blingbase.kotlin.*
import com.dinson.blingbase.retrofit.RxSchedulers
import com.dinson.blingbase.utils.RxHelper
import com.yzy.voice.VoicePlay
import com.yzy.voice.model.TTSPlayerHelper
import com.zjta.collectionvoice.bean.GetPhoneCode
import com.zjta.collectionvoice.bean.LoginCodeReq
import com.zjta.collectionvoice.bean.LoginPwdReq
import com.zjta.collectionvoice.bean.LoginUser
import com.zjta.collectionvoice.http.HttpHelper
import com.zjta.collectionvoice.http.HttpObserver
import com.zjta.collectionvoice.utils.MMKVUtils
import com.zjta.collectionvoice.utils.loge
import com.zjta.collectionvoice.utils.toast
import com.zjta.collectionvoice.widget.DialogCancelConfirm
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private var mCurrentModeIsPhone = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val withDialog = intent.getBooleanExtra(EXTRA_TAG, false)
        if (withDialog) {
            DialogCancelConfirm(this)
                .setMessage("您的账号从别处登录，请注意您的账号安全")
                .setButtonText("确定")
                .setDialogSingleClickListener {
                    it.dismiss()
                    MMKVUtils.removeUserInfo()
                }.show()
        }

        etPhone.setText(MMKVUtils.getUserName())
        etPhone.lostFocus()
        vChangeMode.click {
            mCurrentModeIsPhone = !mCurrentModeIsPhone
            changeMode(mCurrentModeIsPhone)
        }
        vDoLogin.click { doLogin() }
        tvGetPhoneCode.click { getPhoneCode() }
        test.click {
            TTSPlayerHelper.init(this).play("欢迎光临${index++}")
              }
    }

    private var index = 200

    @SuppressLint("SetTextI18n")
    private fun getPhoneCode() {
        if (etPhone.text.toString().isEmpty()) return
        HttpHelper.appApi.getPhoneCode(GetPhoneCode(etPhone.text.toString()))
            .compose(RxSchedulers.io_main())
            .subscribe(object : HttpObserver<String>() {
                override fun onHandleSuccess(t: String?, message: String) {
                    RxHelper.countdown(60).subscribe({
                        loge { "it  $it s" }
                        tvGetPhoneCode.text = "${it}s"
                        tvGetPhoneCode.isEnabled = false
                    }, {
                        tvGetPhoneCode.isEnabled = true
                        tvGetPhoneCode.text = "获取验证码"
                    }, {
                        tvGetPhoneCode.isEnabled = true
                        tvGetPhoneCode.text = "获取验证码"
                    }).addToManaged()
                }
            })
    }

    private fun doLogin() {
        val phone = etPhone.text.toString()
        //val phoneUnder = etPhoneUnder.text.toString()
        if (phone.isEmpty()) {
            "请输入手机号码".toast()
            return
        }
        if (mCurrentModeIsPhone) {
            if (etPhonePwd.text.toString().isEmpty()) {
                "请输入登录密码".toast()
                return
            }
        } else {
            if (etPhoneCode.text.toString().isEmpty()) {
                "请输入验证码".toast()
                return
            }
        }
        val ob = if (mCurrentModeIsPhone) {
            val req = LoginPwdReq(phone, etPhonePwd.text.toString())
            HttpHelper.appApi.doLoginByPwd(req)
        } else {
            val req = LoginCodeReq(phone, etPhoneCode.text.toString())
            HttpHelper.appApi.doLoginByCode(req)
        }
        ob.compose(RxSchedulers.io_main())
            .subscribe(object : HttpObserver<LoginUser>() {
                override fun onHandleSuccess(t: LoginUser?, message: String) {
                    if (t == null) {
                        "系统错误，请联系管理人员".toast()
                        return
                    }
                    MMKVUtils.setUserName(phone)
                    MMKVUtils.setUserToken(t.token)
                    MMKVUtils.setUserId(t.userId)
                    MainActivity.start(this@LoginActivity)
                }
            })
    }


    private fun changeMode(isPhone: Boolean) {
        if (isPhone) {
            tvMode.text = "密码登录"
            tvModeDesc.text = "请输入您的账号密码"
            llPhoneCode.hide(true)
            etPhonePwd.show()
            vChangeMode.text = "手机快捷登录"
            etPhonePwd.setText("")
            etPhoneCode.setText("")
            etPhone.lostFocus()
            etPhonePwd.lostFocus()
            etPhoneCode.lostFocus()
            vChangeMode.setCompoundDrawablesRes(R.mipmap.login_mode_phone_code)
        } else {
            tvMode.text = "验证码登录"
            tvModeDesc.text = "请输入您的手机号及验证码"
            llPhoneCode.show()
            etPhonePwd.hide(true)
            vChangeMode.text = "密码登录"
            etPhonePwd.setText("")
            etPhoneCode.setText("")
            etPhone.lostFocus()
            etPhonePwd.lostFocus()
            etPhoneCode.lostFocus()
            vChangeMode.setCompoundDrawablesRes(R.mipmap.login_mode_phone)
        }
    }

    override fun onBackPressed() {
        SplashActivity.exitSystem(this)
    }

    companion object {
        private const val EXTRA_TAG = "extra_tag"

        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }

        fun startWithDialog(context: Context) {
            val starter = Intent(context, LoginActivity::class.java)
            starter.putExtra(EXTRA_TAG, true)
            context.startActivity(starter)
        }
    }
}