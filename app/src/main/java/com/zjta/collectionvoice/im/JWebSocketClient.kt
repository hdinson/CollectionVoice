package com.zjta.collectionvoice.im

import android.content.Context
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.yzy.voice.VoicePlay
import com.yzy.voice.util.StringUtils
import com.zjta.collectionvoice.LoginActivity
import com.zjta.collectionvoice.SplashActivity
import com.zjta.collectionvoice.bean.ServiceMessage
import com.zjta.collectionvoice.bean.StopSocket
import com.zjta.collectionvoice.event.RefreshOrder
import com.zjta.collectionvoice.utils.loge
import com.zjta.collectionvoice.utils.logi
import com.zjta.collectionvoice.utils.toast
import org.greenrobot.eventbus.EventBus
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

open class JWebSocketClient(val context: Context, serverUri: URI) :
    WebSocketClient(serverUri, Draft_6455()) {

    override fun onOpen(handshakedata: ServerHandshake) {
        logi { "websocket连接成功" }
    }

    override fun onMessage(message: String) {
        logi { "收到的消息：$message" }
        try {
            val bean = Gson().fromJson(message, ServiceMessage::class.java)
            if (bean.type == 1) {
                //1 是付款到账消息类型
                val money = StringUtils.getMoney(bean.data)
                if (money.isEmpty()){
                    "未识别到金额: ${bean.data}".toast()
                    return
                }
                VoicePlay.with(context).play(money, false)
                EventBus.getDefault().post(RefreshOrder())
            } else if (bean.type == 0) {
                SplashActivity.otherLogout(context)
            }
        } catch (e: Exception) {

        }
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        logi { "onClose() code:$code, reason:$reason, remote:$remote" }
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
        loge { "onError()" }
    }
}