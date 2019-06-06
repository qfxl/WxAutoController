package com.zbycorp.wx.utils

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import java.util.*
import kotlin.collections.ArrayList

internal object WeChatAccessUtil {
    private val TAG = "qfxl"
    /**
     * 微信包名
     */
    private val WX_PACKAGE = "com.tencent.mm"
    /**
     * 微信布局id前缀
     */
    private val BASELAYOUT_ID = "com.tencent.mm:id/"
    /**
     * 微信首页
     */
    val WECHAT_CLASS_LAUNCHUI = "com.tencent.mm.ui.LauncherUI"
    /**
     * 微信聊天页面
     */
    val WECHAT_CLASS_CHATUI = "com.tencent.mm.ui.chatting.ChattingUI"
    /**
     * 输入框
     */
    private val EDIT_TEXT = "android.widget.EditText"
    /**
     * textView
     */
    private val TEXT_VIEW = "android.widget.TextView"
    /**
     * 搜索id
     */
    private val SEARCH_ID = BASELAYOUT_ID + "jb"
    /**
     * 搜素输入框
     */
    private val SEARCH_INPUT_ID = BASELAYOUT_ID + "l3"
    /**
     * 搜索结果item
     */
    private val SEARCH_INPUT_LIST_ITEM_ID = BASELAYOUT_ID + "qj"
    /**
     * 聊天界面的输入框
     */
    private val CHAT_INPUT_ID = BASELAYOUT_ID + "ami"
    /**
     * 聊天界面发送按钮
     */
    private val SEND_ID = BASELAYOUT_ID + "amp"
    /**
     * 聊天界面退出按钮
     */
    private val CHAT_SEND_BACK_ID = BASELAYOUT_ID + "kn"
    /**
     * 搜索界面退出按钮
     */
    private val SEARCH_SEND_BACK_ID = BASELAYOUT_ID + "l1"

    /**
     * 名字及内容
     */
    var nameContentList: ArrayList<Pair<String, String>>? = null
    /**
     * 打开微信
     *
     * @param activity
     */
    fun openWeChat(activity: Activity) {
        val intent = Intent()
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.setClassName(
            WX_PACKAGE,
            WECHAT_CLASS_LAUNCHUI
        )
        activity.startActivity(intent)
    }

    /**
     * 搜索
     *
     * @param service
     * @param name
     */
    @Throws(InterruptedException::class)
    fun sendMessage(service: AccessibilityService) {
        val viewList = findNodesByViewId(
            service,
            SEARCH_ID
        )
        if (viewList != null && viewList.isNotEmpty()) {
            for (accessibilityNodeInfo in viewList) {
                //微信7.0.4版本特殊处理
                val nodeInfo = accessibilityNodeInfo.parent
                if (TextUtils.isEmpty(nodeInfo.contentDescription)) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    Thread.sleep(500)
                    fillInput(
                        service,
                        SEARCH_INPUT_ID,
                        nameContentList?.get(0)?.first
                    )
                    Thread.sleep(500)
                    findViewAndPerformClickParentByText(service, nameContentList?.get(0)?.first)
                    Thread.sleep(500)
                    findViewIdAndPerformClick(
                        service,
                        CHAT_INPUT_ID
                    )
                    Thread.sleep(500)
                    fillInput(
                        service,
                        CHAT_INPUT_ID,
                        nameContentList?.get(0)?.second
                    )
                    Thread.sleep(500)
                    findViewIdAndPerformClick(
                        service,
                        SEND_ID
                    )
                    findViewAndPerformClickParent(service, CHAT_SEND_BACK_ID)
                    Thread.sleep(500)
                    findViewAndPerformClickParent(service, SEARCH_SEND_BACK_ID)

                    nameContentList?.removeAt(0)
                    break
                }
            }
        }
    }

    /**
     * 填充EditText
     *
     * @param service
     * @param viewId
     * @param content
     */
    private fun fillInput(service: AccessibilityService, viewId: String, content: String?) {
        val viewList = findNodesByViewId(service, viewId)
        if (viewList != null && viewList.isNotEmpty()) {
            for (accessibilityNodeInfo in viewList) {
                if (accessibilityNodeInfo.className == EDIT_TEXT && accessibilityNodeInfo.isEnabled) {
                    val clip = ClipData.newPlainText("label", content)
                    val clipboardManager = service.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardManager.primaryClip = clip
                    accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE)
                }
            }
        }
    }

    /**
     * 查找控件并点击其父控件
     *
     * @param service
     * @param text
     */
    private fun findViewAndPerformClickParentByText(service: AccessibilityService, text: String?) {
        val viewList = findNodesByText(service, text)
        if (viewList != null && viewList.isNotEmpty()) {
            for (i in viewList.indices) {
                //微信7.0.4版本特殊处理，7.0.4只能从父控件点击，然后这个通过当前页面文案来查找控件，要排除输入框的内容
                val node = viewList[i]
                val parentNode = node.parent
                if (node.text.toString() == text && node.className == TEXT_VIEW) {
                    parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    break
                }
            }
        }
    }

    /**
     * 查找控件并点击其父控件
     *
     * @param viewId
     */
    private fun findViewAndPerformClickParent(service: AccessibilityService, viewId: String) {
        val viewList = findNodesByViewId(service, viewId)
        if (viewList != null && viewList.isNotEmpty()) {
            for (i in viewList.indices) {
                //微信7.0.4版本特殊处理
                val nodeInfo = viewList[i].parent
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
    }

    /**
     * 查找节点并模拟点击
     *
     * @param viewId
     */
    private fun findViewIdAndPerformClick(service: AccessibilityService, viewId: String) {
        val viewList = findNodesByViewId(service, viewId)
        if (viewList != null && viewList.isNotEmpty()) {
            for (accessibilityNodeInfo in viewList) {
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
    }

    /**
     * 通过viewId查找所有节点
     *
     * @param service
     * @param viewId
     * @return
     */
    private fun findNodesByViewId(service: AccessibilityService, viewId: String?): List<AccessibilityNodeInfo>? {
        return if (service.rootInActiveWindow != null) service.rootInActiveWindow.findAccessibilityNodeInfosByViewId(
            viewId
        ) else null
    }

    /**
     * 通过文本查找所有节点
     *
     * @param text
     * @return
     */
    private fun findNodesByText(service: AccessibilityService, text: String?): List<AccessibilityNodeInfo>? {
        return if (service.rootInActiveWindow != null) service.rootInActiveWindow.findAccessibilityNodeInfosByText(text) else null
    }
}
