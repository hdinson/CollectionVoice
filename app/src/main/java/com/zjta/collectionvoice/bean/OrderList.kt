package com.zjta.collectionvoice.bean

class OrderList(
    val content: List<Content> = listOf(),
    val total: Int = 0
)

class Content(
    val orderId: String = "",//支付订单号

    val payTime: String = "",//支付时间
    val name: String = "",//消费者名称
    val money: Double = 0.0,//付款金额
    val actualMoney: Double = 0.0,//商家到账金额
    val state: Int = 0 //订单支付状态 0待支付 1支付成功


    /*val addTime: String = "",
    val address: String = "",
    val areaAgentFee: Any = Any(),
    val areaAgentId: Any = Any(),
    val areaMoney: Any = Any(),
    val comments: Int = 0,
    val commonAgentFee: Int = 0,
    val commonMoney: Int = 0,
    val comonAgentId: Any = Any(),
    val id: Int = 0,
    val ip: String = "",
    val isSplit: String = "",
    val merchantId: Any = Any(),


       val orderNo: String = "",


    val platformActualFee: Double = 0.0,
    val platformFee: Double = 0.0,
    val platformId: Any = Any(),
    val platformMoney: Int = 0,
    val prize: Int = 0,
    val proviceAgentFee: Any = Any(),
    val proviceAgentId: Any = Any(),
    val proviceMoney: Any = Any(),
    val shangNo: String = "",
    val splitState: Any = Any(),

    val storeId: Int = 0,
    val type: Int = 0,
    val userId: Int = 0*/
)