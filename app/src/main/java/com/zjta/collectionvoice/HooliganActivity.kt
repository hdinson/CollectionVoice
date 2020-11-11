package com.zjta.collectionvoice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.dinson.blingbase.RxBling

class HooliganActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        val window: Window = window
        window.setGravity(Gravity.LEFT or Gravity.TOP)
        val params: WindowManager.LayoutParams = window.attributes
        params.x = 0
        params.y = 0
        params.height = 1
        params.width = 1
        window.attributes = params
    }


    companion object {

        private var instance: HooliganActivity? = null

        /**
         * 开启保活页面
         */
        fun startHooligan() {
            val intent = Intent(RxBling.getApplicationContext(), HooliganActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            RxBling.getApplicationContext().startActivity(intent)
        }

        /**
         * 关闭保活页面
         */
        fun killHooligan() {
            if (instance != null) {
                instance!!.finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }


}