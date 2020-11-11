package com.zjta.collectionvoice.api

import com.zjta.collectionvoice.bean.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AppApi {

    /**
     * 密码登录
     */
    @POST("/user/login")
    fun doLoginByPwd(@Body req: LoginPwdReq): Observable<HttpResult<LoginUser>>

    /**
     * 密码登录
     */
    @POST("/user/login")
    fun doLoginByCode(@Body req: LoginCodeReq): Observable<HttpResult<LoginUser>>

    /**
     * 退出登录
     */
    @GET("/user/exitlogin")
    fun doLoginOut(@Query("userId") userId: String): Observable<HttpResult<String>>

    /**
     * 修改密码
     */
    @POST("/user/password")
    fun updatePwd(@Body req: UpdatePwdReq): Observable<HttpResult<String>>

    /**
     * 获取门店订单
     */
    @POST("/user/store/order")
    fun getOrder(@Body req: GetOrderReq): Observable<HttpResult<OrderList>>


    /**
     * 发送验证码
     */
    @POST("/user/sms/code")
    fun getPhoneCode(@Body req: GetPhoneCode): Observable<HttpResult<String>>

    /**
     * 订单号，消费者搜索
     */
    @POST("/user/store/order")
    fun searchByKey(@Body req: GetOrderReq): Observable<HttpResult<OrderList>>
}