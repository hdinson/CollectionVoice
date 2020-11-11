package com.zjta.collectionvoice.bean

class ServiceMessage(
    val data: String = "",
    val type: Int = 0//0  是登录返回的响应  1 是付款到账消息类型
)