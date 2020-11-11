package com.zjta.collectionvoice.http

import android.app.AlertDialog
import android.content.Context
import android.net.ParseException
import android.system.ErrnoException
import com.google.gson.JsonParseException
import com.zjta.collectionvoice.LoginActivity
import com.zjta.collectionvoice.SplashActivity
import com.zjta.collectionvoice.bean.HttpResult
import com.zjta.collectionvoice.event.NoLogin
import com.zjta.collectionvoice.utils.loge
import com.zjta.collectionvoice.utils.toast
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.OnErrorNotImplementedException
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

abstract class HttpObserver<T> : Observer<HttpResult<T>> {

    var mPd: AlertDialog? = null

    constructor() {}

    constructor(pd: AlertDialog) {
        this.mPd = pd
    }

    constructor(context: Context) {
        //this.mPd = AppProgressDialog(context)
    }

    constructor(context: Context, dialogCancelable: Boolean) {
        //this.mPd = AppProgressDialog(context, dialogCancelable)
    }

    final override fun onSubscribe(d: Disposable) {
        if (mPd != null && !mPd!!.isShowing) {
            mPd!!.show()
            mPd!!.setOnDismissListener {
                d.dispose()
            }
        }
    }

    final override fun onNext(value: HttpResult<T>) {
        when (value.status) {
            SUCCESS -> onHandleSuccess(value.data, value.message) //成功
            NO_LOGIN -> EventBus.getDefault().post(NoLogin())
            else -> onHandleError(value.status, value.message)
        }
    }

    final override fun onError(e: Throwable) {
        dismissDialog()
        onHandleError(HTTP_ERROR, e.getShowMsg())
        loge { "Request error: $e\n${e.getShowMsg()}" }
        onHandleFinal()
    }

    final override fun onComplete() {
        dismissDialog()
        onHandleFinal()
    }

    private fun dismissDialog() {
        if (mPd != null && mPd!!.isShowing) {
            mPd!!.dismiss()
        }
    }

    abstract fun onHandleSuccess(t: T?, message: String)

    open fun onHandleError(code: Int, message: String) {
        message.toast()
    }


    private fun Throwable.getShowMsg() = when (this) {
        is HttpException -> "${
        when (code()) {
            401 -> "文件未授权或证书错误"
            403 -> "服务器拒绝请求"
            404 -> "服务器找不到请求的文件"
            408 -> "请求超时,服务器未响应"
            500 -> "服务器内部错误,服务器遇到错误，无法完成请求。"
            502 -> "服务器从上游服务器收到无效响应。"
            503 -> "服务器目前无法使用"
            504 -> "服务器从上游服务器获取数据超时"
            else -> "服务器错误"
        }
        }(${code()})"
        is ParseException -> "json格式错误"
        is JSONException -> "json解析错误"
        is JsonParseException -> "json参数错误"
        is SSLHandshakeException -> "证书验证失败"
        is SocketTimeoutException -> "网络连接超时，请稍后重试"  //请求超时。经常发生，不提示用户
        is UnknownHostException -> "网络连接失败，请检查网络是否正常"
        is ErrnoException -> "网络不可访问"
        is OnErrorNotImplementedException -> cause?.message ?: message ?: "未知错误"
        else -> message ?: "未知错误"
    }

    /**
     * 重写{$onHandleError} 一定要调用super.onHandleError(code, message),否则该方法不执行
     */
    open fun onHandleFinal() {}

    companion object {
        const val SUCCESS = 1
        const val NO_LOGIN = 2
        const val HTTP_ERROR = 3000
    }
}
