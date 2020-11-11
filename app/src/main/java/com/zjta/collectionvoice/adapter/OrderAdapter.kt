package com.zjta.collectionvoice.adapter

import android.annotation.SuppressLint
import android.view.View
import com.dinson.blingbase.kotlin.click
import com.dinson.blingbase.utils.ClipboardUtils
import com.dinson.blingbase.widget.recycleview.CommonAdapter
import com.dinson.blingbase.widget.recycleview.CommonViewHolder
import com.zjta.collectionvoice.R
import com.zjta.collectionvoice.bean.Content
import com.zjta.collectionvoice.utils.toast
import kotlinx.android.synthetic.main.item_order.view.*

class OrderAdapter(dataList: MutableList<Content>) : CommonAdapter<Content>(dataList) {
    override fun getLayoutId(viewType: Int) = R.layout.item_order

    @SuppressLint("SetTextI18n")
    override fun convert(holder: CommonViewHolder, bean: Content, position: Int) {
        holder.itemView.tvOrderName.text = bean.name
        holder.itemView.tvTime.text = bean.payTime
        holder.itemView.ivOrderState.setImageResource(if (bean.state == 1) R.mipmap.already_pay else R.mipmap.no_pay)
        holder.itemView.tvMoney.visibility = if (bean.state == 0) View.GONE else View.VISIBLE
        holder.itemView.tvMoney.text = "+${bean.actualMoney}"
        holder.itemView.tvMoneyOrder.text = "付款金额: ${bean.money}"
        holder.itemView.tvOrderNo.text = bean.orderId
        holder.itemView.tvOrderNo.click {
            ClipboardUtils.copyToClipBoard(
                holder.itemView.context,
                holder.itemView.tvOrderNo.text.toString()
            )
            "已复制到粘贴板".toast()
        }
    }
}