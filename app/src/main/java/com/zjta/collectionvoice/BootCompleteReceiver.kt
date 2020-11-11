package com.zjta.collectionvoice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zjta.collectionvoice.utils.loge

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Intent.ACTION_SCREEN_OFF)) {
            loge { "开启保活" }
            HooliganActivity.startHooligan()
        } else if (intent?.action.equals(Intent.ACTION_SCREEN_ON)) {
            loge { "关闭保活" }
            HooliganActivity.killHooligan()
        }
    }
}