package com.zjta.collectionvoice

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import com.dinson.blingbase.RxBling
import com.dinson.blingbase.rxcache.RxCache
import com.dinson.blingbase.rxcache.diskconverter.GsonDiskConverter
import java.io.File

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RxBling.init(this)
        RxCache.initializeDefault(
            RxCache.Builder()
                .appVersion(1) //当版本号改变,缓存路径下存储的所有数据都会被清除掉
                .diskDir(File(cacheDir.path + File.separator.toString() + "data-cache"))
                .diskConverter(GsonDiskConverter()) //支持Serializable、Json(GsonDiskConverter)
                .memoryMax(2 * 1024 * 1024)
                .diskMax(20 * 1024 * 1024.toLong())
                .showLog(BuildConfig.DEBUG)
                .build()
        )

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(BootCompleteReceiver(), filter)
    }
}