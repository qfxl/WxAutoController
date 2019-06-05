package com.zbycorp.wx.access

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.zbycorp.wx.utils.WeChatAccessUtil

class WeChatAccessService : AccessibilityService() {

    private val TAG = "qfxl"

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                when (event.className.toString()) {
                    WeChatAccessUtil.WECHAT_CLASS_LAUNCHUI -> {
                        try {
                            WeChatAccessUtil.search(this@WeChatAccessService)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }

                    }
                    WeChatAccessUtil.WECHAT_CLASS_CHATUI -> Log.i(TAG, "微信聊天页面启动")
                }
            }
        }


    }

    override fun onInterrupt() {

    }
}
