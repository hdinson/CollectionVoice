package com.zjta.collectionvoice

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinson.blingbase.kotlin.click
import com.dinson.blingbase.kotlin.lostFocus
import com.dinson.blingbase.retrofit.RxSchedulers
import com.dinson.blingbase.utils.SystemBarModeUtils
import com.zjta.collectionvoice.adapter.OrderAdapter
import com.zjta.collectionvoice.bean.Content
import com.zjta.collectionvoice.bean.GetOrderReq
import com.zjta.collectionvoice.bean.OrderList
import com.zjta.collectionvoice.http.HttpHelper
import com.zjta.collectionvoice.http.HttpObserver
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

class SearchActivity : BaseActivity() {

    private val mOrderList = ArrayList<Content>()
    private val mAdapter by lazy { OrderAdapter(mOrderList) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        SystemBarModeUtils.setPaddingTop(this, llSearch)
        SystemBarModeUtils.darkMode(this, true)
        actionBack.click { onBackPressed() }
        etSearchBar.lostFocus()
        ivFilter.click { showDatePickerDialog(this, 4) }

        tvConfirm.click {
            if (etSearchBar.text.toString().isEmpty()) return@click
            etSearchBar.lostFocus()
            searchByKey(true)
        }
        ervSearchOrder.getInnerRecycleView().apply {
            overScrollMode = ScrollView.OVER_SCROLL_NEVER
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
        ervSearchOrder.setEmptyViewRes(R.layout.layout_base_list_empty_view)
        srlSearchOrder.setOnRefreshListener { searchByKey(true) }
        srlSearchOrder.setOnLoadMoreListener { searchByKey(false) }
    }

    private var mCurrentPage = 1
    private fun searchByKey(isRefresh: Boolean, time: String? = null) {
        if (isRefresh) {
            mCurrentPage = 1
            srlSearchOrder.autoRefresh()
        }
        val req = if (time == null || time.isEmpty()) {
            GetOrderReq(mCurrentPage, etSearchBar.text.toString())
        } else GetOrderReq(mCurrentPage, null, time)
        HttpHelper.appApi.searchByKey(req)
            .compose(RxSchedulers.io_main())
            .subscribe(object : HttpObserver<OrderList>() {
                override fun onHandleSuccess(t: OrderList?, message: String) {
                    if (t?.content == null || t.content.size < 20) {
                        srlSearchOrder.finishRefreshWithNoMoreData()
                    } else {
                        if (isRefresh) {
                            srlSearchOrder.finishRefresh()
                        } else {
                            srlSearchOrder.finishLoadMore()
                        }
                    }
                    mCurrentPage++
                    if (isRefresh) mOrderList.clear()
                    if (t?.content != null)
                        mOrderList.addAll(t.content)
                    mAdapter.notifyDataSetChanged()
                }

                override fun onHandleError(code: Int, message: String) {
                    super.onHandleError(code, message)
                    srlSearchOrder.finishRefresh(false)
                }
            })
    }

    @SuppressLint("SetTextI18n")
    fun showDatePickerDialog(activity: Context, themeResId: Int) {
        val calendar = Calendar.getInstance(Locale.CHINA)
        DatePickerDialog(activity, themeResId,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val day = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                etSearchBar.setText("$year-${month + 1}-$day")
                searchByKey(true, "$year-${month + 1}-$day")
            } // 设置初始日期
            , calendar[Calendar.YEAR]
            , calendar[Calendar.MONTH]
            , calendar[Calendar.DAY_OF_MONTH]).show()
    }


    override fun activityInAnim() = arrayOf(0, 0)
    override fun activityOutAnim() = arrayOf(0, 0)

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SearchActivity::class.java))
        }
    }
}