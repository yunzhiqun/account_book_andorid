package com.accountbook.app.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * 复制工具类
 *
 * 提供将文本复制到系统剪贴板的功能。
 */
object CopyUtils {

    /**
     * 复制文本到剪贴板
     *
     * @param context 应用上下文
     * @param text 要复制的文本
     */
    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("account_total", text)
        clipboard.setPrimaryClip(clip)
    }
}
