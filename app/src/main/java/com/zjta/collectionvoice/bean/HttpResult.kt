package com.zjta.collectionvoice.bean

class HttpResult<T>(
    val data: T?,
    val message: String = "",
    val status: Int = 0,
    val timestamp: String = ""
)