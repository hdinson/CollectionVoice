package com.zjta.collectionvoice.widget

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.zjta.collectionvoice.R
import kotlinx.android.synthetic.main.common_title_bar.view.*

/**
 * 通用标题栏
 */
@Suppress("unused")
class CommonTitleBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mSettings: CommonTitleBarSettings

    init {
        mSettings = CommonTitleBarSettings(context, attrs)
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        LayoutInflater.from(context).inflate(R.layout.common_title_bar, this, true)

        //标题
        tvTitleCenter.text = mSettings.titleText   //设置标题文字
        tvTitleCenter.setTextColor(mSettings.titleTextColor) //设置标题颜色

        //左边按钮
        ibTitleLeft.setImageResource(mSettings.leftBtnDrawable)
        ibTitleLeft.visibility = if (mSettings.isLeftBtnVisible) View.VISIBLE else View.GONE

        //右边按钮
        ibTitleRight.setImageResource(mSettings.rightBtnDrawable)
        ibTitleRight.visibility = if (mSettings.isRightBtnVisible) View.VISIBLE else View.GONE

        //右边标题
        tvTitleRight.text = mSettings.rightText
        tvTitleRight.setTextColor(mSettings.rightTextColor)
        tvTitleRight.visibility = if (mSettings.isRightTvVisible) View.VISIBLE else View.GONE

        //左边边标题
        tvTitleLeft.text = mSettings.leftText
        tvTitleLeft.setTextColor(mSettings.leftTextColor)

        //左右按钮默认关闭
        setBackgroundResource(mSettings.backGroundResource)
        transparentPadding(context, this, mSettings.ancestorFitsSystemWindow)
    }


    fun getLeftBtn(): ImageButton = ibTitleLeft

    fun getCenterTv(): TextView = tvTitleCenter

    fun getRightTv(): TextView = tvTitleRight

    fun getLeftTv(): TextView = tvTitleLeft

    fun getRightBtn(): ImageButton = ibTitleRight


    /////////////////////////////以下解决标题栏问题//////////////////////////////////////////
    private fun transparentPadding(
        context: Context, view: View, ancestorFitsSystemWindow: Boolean
    ) {
        if (!ancestorFitsSystemWindow) {
            val resources = context.resources
            val barSize = getStatusBarSize(resources)
            if (barSize > 0) {
                val layoutParams = view.layoutParams
                if (layoutParams != null) {
                    layoutParams.height += barSize
                    view.setPadding(
                        view.paddingLeft,
                        view.paddingTop + barSize,
                        view.paddingRight,
                        view.paddingBottom
                    )
                }
            }
        }
    }

    /**
     * 获取statusBar大小
     */
    private fun getStatusBarSize(res: Resources): Int {
        var result = 0
        val resourceId = res.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId)
        }
        return result
    }


    /**
     * 标题栏设置
     */
    class CommonTitleBarSettings internal constructor(
        context: Context, attrs: AttributeSet?
    ) {
        var titleText: String? = ""
        var rightText: String? = ""
        var leftText: String? = ""
        val isLeftBtnVisible: Boolean
        val isRightBtnVisible: Boolean
        val isRightTvVisible: Boolean
        val ancestorFitsSystemWindow: Boolean
        val titleTextColor: Int
        val rightTextColor: Int
        val leftTextColor: Int
        val leftBtnDrawable: Int
        val rightBtnDrawable: Int
        val backGroundResource: Int

        init {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar)
            isLeftBtnVisible =
                attributes.getBoolean(R.styleable.CommonTitleBar_leftBtnVisible, false)
            isRightBtnVisible =
                attributes.getBoolean(R.styleable.CommonTitleBar_rightBtnVisible, false)
            isRightTvVisible =
                attributes.getBoolean(R.styleable.CommonTitleBar_rightTextVisible, false)
            leftBtnDrawable = attributes.getResourceId(
                R.styleable.CommonTitleBar_leftBtnDrawable,
                R.mipmap.ic_common_arrow_left_grey
            )
            rightBtnDrawable = attributes.getResourceId(
                R.styleable.CommonTitleBar_rightBtnDrawable,
                R.mipmap.ic_common_arrow_left_grey
            )
            titleText = attributes.getString(R.styleable.CommonTitleBar_titleText)
            titleTextColor =
                attributes.getColor(
                    R.styleable.CommonTitleBar_titleTextColor,
                    ContextCompat.getColor(context, R.color.font_primary)
                )
            rightText = attributes.getString(R.styleable.CommonTitleBar_rightText)
            leftText = attributes.getString(R.styleable.CommonTitleBar_leftText)
            rightTextColor =
                attributes.getColor(
                    R.styleable.CommonTitleBar_rightTextColor,
                    ContextCompat.getColor(context, R.color.font_primary)
                )
            leftTextColor =
                attributes.getColor(
                    R.styleable.CommonTitleBar_leftTextColor,
                    ContextCompat.getColor(context, R.color.font_primary)
                )
            ancestorFitsSystemWindow =
                attributes.getBoolean(R.styleable.CommonTitleBar_ancestorFitsSystemWindow, false)

            backGroundResource =
                attributes.getResourceId(
                    R.styleable.CommonTitleBar_backgroundResource,
                    R.color.white
                )

            attributes.recycle()
        }
    }
}