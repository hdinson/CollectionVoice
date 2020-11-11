package com.zjta.collectionvoice.http

import androidx.annotation.MenuRes
import com.dinson.blingbase.retrofit.log.LogLevel
import com.dinson.blingbase.retrofit.log.LoggingInterceptor
import com.zjta.collectionvoice.api.AppApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit统一封装设置,返回retrofit对象
 */
object HttpHelper {

    private const val DEFAULT_TIMEOUT = 10

    private val mOkHttpClient = OkHttpClient.Builder()
        .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .addInterceptor(LoggingInterceptor.Builder()
            .setLogLevel(LogLevel.INFO)
            .build())
        .build()
    private val mRetrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create()) //对http请求结果进行统一的预处理
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //对rxJava提供支持
        .baseUrl("https://nadernet.com/v2/api-docs/")
        .client(mOkHttpClient)
        .build()

    val appApi: AppApi by lazy { mRetrofit.create(AppApi::class.java) }

}
