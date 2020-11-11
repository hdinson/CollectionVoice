package com.zjta.collectionvoice

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.dinson.blingbase.annotate.BindEventBus
import com.dinson.blingbase.utils.SystemBarModeUtils
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.zjta.collectionvoice.event.NoLogin
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 所有activity的基类
 */
@SuppressLint("Registered")
@BindEventBus
open class BaseActivity : RxAppCompatActivity() {

    private val mCompositeDisposable = CompositeDisposable()
    fun Disposable.addToManaged() {
        mCompositeDisposable.add(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*禁止截屏*/
        //window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        /*共享元素*/
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        /*透明状态栏*/
        SystemBarModeUtils.immersive(this)
        /*activity的出现动画*/
        overridePendingTransition(activityInAnim()[0], activityInAnim()[1])
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (BuildConfig.DEBUG) {
            /*logcat点击跳转对用activity*/
            logShowActivity()
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        if (this.javaClass.isAnnotationPresent(BindEventBus::class.java) &&
            !EventBus.getDefault().isRegistered(this)
        ) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStart() {
        super.onStart()
        logShowActivity()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(activityOutAnim()[0], activityOutAnim()[1])
    }

    open fun activityInAnim() =
        arrayOf(R.anim.activity_in_from_right, R.anim.activity_out_to_left)

    open fun activityOutAnim(): Array<Int> =
        arrayOf(R.anim.activity_in_from_left, R.anim.activity_out_to_right)


    /**
     * 用户未登录
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NoLogin) {
        SplashActivity.logout(this)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        logShowActivity()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        logShowActivity()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        logShowActivity()
    }

    override fun onActivityReenter(resultCode: Int, data: Intent) {
        super.onActivityReenter(resultCode, data)
        logShowActivity()
    }

    override fun onLocalVoiceInteractionStarted() {
        super.onLocalVoiceInteractionStarted()
        logShowActivity()
    }

    override fun onLocalVoiceInteractionStopped() {
        super.onLocalVoiceInteractionStopped()
        logShowActivity()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        logShowActivity()
    }

    override fun onPause() {
        super.onPause()
        logShowActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        logShowActivity()
    }

    override fun onStop() {
        super.onStop()
        logShowActivity()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        logShowActivity()
    }


    override fun onRestart() {
        super.onRestart()
        logShowActivity()
    }

    override fun onResume() {
        super.onResume()
        logShowActivity()
    }

    open fun onTitleLeftBtnClick(view: View) {
        onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()

        if (this.javaClass.isAnnotationPresent(BindEventBus::class.java) &&
            EventBus.getDefault().isRegistered(this)
        ) {
            EventBus.getDefault().unregister(this)
        }
        logShowActivity()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        logShowActivity()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        logShowActivity()
        return super.onCreateOptionsMenu(menu)
    }

    private fun logShowActivity() {
        val stackTraceElement = Thread.currentThread().stackTrace

        var currentIndex = stackTraceElement.indices
            .firstOrNull { stackTraceElement[it].methodName.compareTo("logShowActivity") == 0 }
            ?.let { it + 1 }
            ?: -1

        currentIndex += 1
        val fullClassName = stackTraceElement[currentIndex].className
        if (!fullClassName.startsWith(BuildConfig.APPLICATION_ID)) return
        val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
        val methodName = stackTraceElement[currentIndex].methodName
        val lineNumber = stackTraceElement[currentIndex].lineNumber.toString()
        Log.v(
            "EtherTreasure",
            "│ --> " + "at " + fullClassName + "." + methodName + "("
                    + className + ".java:" + lineNumber + ")"
        )
    }
}
