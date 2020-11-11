package com.zjta.collectionvoice.utils

import android.annotation.SuppressLint
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.dinson.blingbase.RxBling


/**
 * Toast工具
 */
@SuppressLint("ShowToast")
object ToastUtils {
    private val mToast by lazy {
        val toast = Toast.makeText(RxBling.getApplicationContext(), "", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast
    }

    fun showToast(message: String) {
        mToast.setText(message)
        mToast.show()
    }
}

fun String.toast() {
    ToastUtils.showToast(this)
}