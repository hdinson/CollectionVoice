package com.zjta.collectionvoice.bean

import com.zjta.collectionvoice.utils.MMKVUtils

class GetOrderReq(
    val page: Int = 0,
    val queryKey: String? = null,
    val time: String? = null,
    val userId: String = MMKVUtils.getUserId(),
    val token: String = MMKVUtils.getUserToken(),
    val size: Int = 20
)