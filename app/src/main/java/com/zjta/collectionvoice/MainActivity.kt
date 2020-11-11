package com.zjta.collectionvoice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinson.blingbase.annotate.BindEventBus
import com.dinson.blingbase.kotlin.click
import com.dinson.blingbase.retrofit.RxSchedulers
import com.dinson.blingbase.utils.SystemBarModeUtils
import com.zjta.collectionvoice.adapter.OrderAdapter
import com.zjta.collectionvoice.bean.Content
import com.zjta.collectionvoice.bean.GetOrderReq
import com.zjta.collectionvoice.bean.OrderList
import com.zjta.collectionvoice.bean.StopSocket
import com.zjta.collectionvoice.event.RefreshOrder
import com.zjta.collectionvoice.http.HttpHelper
import com.zjta.collectionvoice.http.HttpObserver
import com.zjta.collectionvoice.im.JWebSocketClientService
import com.zjta.collectionvoice.utils.MMKVUtils
import com.zjta.collectionvoice.utils.loge
import com.zjta.collectionvoice.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

@BindEventBus
class MainActivity : BaseActivity() {

    private val mOrderList = ArrayList<Content>()
    private val mAdapter by lazy { OrderAdapter(mOrderList) }

    private var mBinder: JWebSocketClientService? = null
    private val mServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(
                componentName: ComponentName, iBinder: IBinder
            ) {
                Log.e("MainActivity", "服务与活动成功绑定")
                val binder = iBinder as JWebSocketClientService.JWebSocketClientBinder
                mBinder = binder.service
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                Log.e("MainActivity", "服务与活动成功断开")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        startJWebSClientService()//启动服务
        bindService() //绑定服务
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: StopSocket) {
        loge { "onMessageEvent : stop socket" }
        mBinder?.closeConnect()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshOrder) {
        srlOrder.autoRefresh()
    }

    private fun initUI() {
        SystemBarModeUtils.setPaddingTop(this, clTopBar)

        ervOrder.getInnerRecycleView().apply {
            overScrollMode = ScrollView.OVER_SCROLL_NEVER
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        ervOrder.setEmptyViewRes(R.layout.layout_base_list_empty_view)
        srlOrder.setOnRefreshListener { getOrder(true) }
        srlOrder.setOnLoadMoreListener { getOrder(false) }
        srlOrder.autoRefresh()
        vSetting.click { SettingActivity.start(this) }
        tvUserName.text = MMKVUtils.getUserName()
        tvSearch.click { SearchActivity.start(this) }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinder?.closeConnect()
    }

    /**
     * 启动服务（websocket客户端服务）
     */
    private fun startJWebSClientService() {
        val intent = Intent(this, JWebSocketClientService::class.java)
        startService(intent)
    }

    /**
     * 绑定服务
     */
    private fun bindService() {
        val bindIntent = Intent(this, JWebSocketClientService::class.java)
        bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE)
    }

    private var mCurrentPage = 1

    private fun getOrder(isRefresh: Boolean) {
        if (isRefresh) mCurrentPage = 1
        HttpHelper.appApi.getOrder(GetOrderReq(mCurrentPage))
            .compose(RxSchedulers.io_main())
            .subscribe(object : HttpObserver<OrderList>() {
                override fun onHandleSuccess(t: OrderList?, message: String) {
                    if (t?.content == null || t.content.size < 20) {
                        srlOrder.finishRefreshWithNoMoreData()
                    } else {
                        if (isRefresh) {
                            srlOrder.finishRefresh()
                        } else {
                            srlOrder.finishLoadMore()
                        }
                    }
                    if (t?.content == null) return
                    mCurrentPage++
                    if (isRefresh) mOrderList.clear()
                    mOrderList.addAll(t.content)
                    mAdapter.notifyDataSetChanged()
                }

                override fun onHandleError(code: Int, message: String) {
                    super.onHandleError(code, message)
                    srlOrder.finishRefresh(false)
                }
            })
    }

    private var exitTime: Long = 0 // 退出时间
    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            "再按一次退出".toast()
            exitTime = System.currentTimeMillis()
        } else {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}