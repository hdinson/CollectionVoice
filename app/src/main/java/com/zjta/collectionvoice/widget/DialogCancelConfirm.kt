package com.zjta.collectionvoice.widget

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.zjta.collectionvoice.R

/**
 * 提示框
 */
class DialogCancelConfirm(context: Context) : Dialog(context, R.style.AppDialog),
    View.OnClickListener {


    init {
        setContentView(R.layout.dialog_cancel_confirm)
        findViewById<View>(R.id.dialogLeftBtn).setOnClickListener(this)
        findViewById<View>(R.id.dialogRightBtn).setOnClickListener(this)
    }

    private var operationListener: ((dialog: Dialog, isLeft: Boolean) -> Unit)? = null
    private var singleClickListener: ((dialog: Dialog) -> Unit)? = null

    fun setOperationListener(listener: (dialog: Dialog, isLeft: Boolean) -> Unit): DialogCancelConfirm {
        this.operationListener = listener
        return this
    }

    fun setDialogSingleClickListener(listener: (dialog: Dialog) -> Unit): DialogCancelConfirm {
        this.singleClickListener = listener
        return this
    }

    fun setMessage(message: CharSequence): DialogCancelConfirm {
        findViewById<TextView>(R.id.dialogText).text = message
        return this
    }

    fun setButtonText(left: CharSequence, right: CharSequence): DialogCancelConfirm {
        findViewById<View>(R.id.dialogRightBtn).visibility = View.VISIBLE
        findViewById<TextView>(R.id.dialogLeftBtn).text = left
        findViewById<TextView>(R.id.dialogRightBtn).text = right
        return this
    }

    fun setDialogTitle(title: String): DialogCancelConfirm {
        findViewById<TextView>(R.id.dialogTitle).apply {
            text = title
            visibility = View.VISIBLE
        }
        return this
    }

    fun setButtonText(text: CharSequence): DialogCancelConfirm {
        findViewById<View>(R.id.dialogLeftBtn).visibility = View.GONE
        findViewById<TextView>(R.id.dialogRightBtn).apply {
            this.text = text
            background = ContextCompat.getDrawable(
                this.context,
                R.drawable.shape_dialog_cancel_confirm_single_btn
            )
        }
        return this
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dialogLeftBtn -> {
                operationListener?.invoke(this, true)
            }
            R.id.dialogRightBtn -> {
                when {
                    singleClickListener != null -> {
                        singleClickListener?.invoke(this)
                    }
                    operationListener != null -> {
                        operationListener?.invoke(this, false)
                    }
                    else -> cancel()
                }
            }
        }
    }

}