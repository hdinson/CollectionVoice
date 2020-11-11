package com.zjta.collectionvoice

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dinson.blingbase.kotlin.click
import com.dinson.blingbase.kotlin.lostFocus
import com.dinson.blingbase.retrofit.RxSchedulers
import com.dinson.blingbase.utils.RxHelper
import com.dinson.blingbase.utils.SystemBarModeUtils
import com.zjta.collectionvoice.bean.GetPhoneCode
import com.zjta.collectionvoice.bean.UpdatePwdReq
import com.zjta.collectionvoice.http.HttpHelper
import com.zjta.collectionvoice.http.HttpObserver
import com.zjta.collectionvoice.utils.MMKVUtils
import com.zjta.collectionvoice.utils.loge
import com.zjta.collectionvoice.utils.toast
import kotlinx.android.synthetic.main.activity_reset_pwd.*

class ResetPwdActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pwd)
        SystemBarModeUtils.darkMode(this, true)

        tvPhone.text = MMKVUtils.getUserName()
        etPhoneCode.lostFocus()

        vConfirm.click {
            if (etPhoneCode.text.toString().isEmpty()) {
                "请输入验证码".toast()
                return@click
            }
            if (etPhonePwd.text.toString().isEmpty()) {
                "请输入旧密码".toast()
                return@click
            }
            if (etPhoneNew.toString().isEmpty()) {
                "请输入新密码".toast()
                return@click
            }
            if (etPhoneNew.text.toString() != etPhoneNew2.text.toString()) {
                "两次密码输入不一致".toast()
                return@click
            }
            val phone = MMKVUtils.getUserName()
            val bean = UpdatePwdReq(phone, etPhoneNew.text.toString(), etPhonePwd.text.toString())
            HttpHelper.appApi.updatePwd(bean)
                .compose(RxSchedulers.io_main())
                .subscribe(object : HttpObserver<String>() {
                    override fun onHandleSuccess(t: String?, message: String) {
                        "密码修改成功".toast()
                        onBackPressed()
                    }
                })
        }

        tvGetPhoneCode.click { getPhoneCode() }
    }


    @SuppressLint("SetTextI18n")
    private fun getPhoneCode() {
        if (tvPhone.text.toString().isEmpty()) return
        HttpHelper.appApi.getPhoneCode(GetPhoneCode(tvPhone.text.toString()))
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


    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ResetPwdActivity::class.java))
        }
    }
}