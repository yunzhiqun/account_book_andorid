package com.accountbook.app.util

import com.accountbook.app.data.entity.AccountRecord

/**
 * 金额工具类
 *
 * 提供金额格式化和复制文本生成的工具方法。
 * 支持简单格式（仅金额）和完整格式（包含时间和备注）的复制文本。
 */
object AmountUtils {

    /**
     * 生成简单格式复制文本
     *
     * 格式：金额1+金额2+...=总计
     * 按创建时间正序排列，金额为整数时不显示小数位。
     *
     * @param records 账单记录列表
     * @return String 简单格式复制文本
     */
    fun generateSimpleCopyText(records: List<AccountRecord>): String {
        val sorted = records.sortedBy { it.createdAt }
        val amounts = sorted.map { it.amount }
        val total = amounts.sum()

        val equation = amounts.joinToString("+") { formatAmount(it) }
        val totalStr = formatAmount(total)
        return "$equation=$totalStr"
    }

    /**
     * 生成完整格式复制文本
     *
     * 格式：
     * ```
     * 2024-01-01 12:00
     * 备注内容
     * 100.00
     *
     * 2024-01-02 13:30
     * 50.50
     *
     * ---
     * 100.5+50.5=151
     * ```
     *
     * @param records 账单记录列表
     * @return String 完整格式复制文本
     */
    fun generateFullCopyText(records: List<AccountRecord>): String {
        val sorted = records.sortedBy { it.createdAt }
        val total = sorted.sumOf { it.amount }

        val sb = StringBuilder()
        sorted.forEach { record ->
            sb.appendLine(DateUtils.formatDateTime(record.createdAt))
            if (record.note.isNotEmpty()) {
                sb.appendLine(record.note)
            }
            sb.appendLine(formatDisplayAmount(record.amount))
            sb.appendLine()
        }
        sb.appendLine("---")
        val amounts = sorted.map { it.amount }
        val equation = amounts.joinToString("+") { formatAmount(it) }
        sb.append("$equation=${formatAmount(total)}")

        return sb.toString().trimEnd()
    }

    /**
     * 格式化金额（用于复制文本）
     *
     * 如果金额为整数，不显示小数位；否则保留两位小数并去除末尾的 0。
     *
     * @param amount 金额
     * @return String 格式化后的金额字符串
     */
    fun formatAmount(amount: Double): String {
        return if (amount == amount.toLong().toDouble()) {
            amount.toLong().toString()
        } else {
            val formatted = String.format("%.2f", amount)
            formatted.trimEnd('0').trimEnd('.')
        }
    }

    /**
     * 格式化显示金额（用于界面显示）
     *
     * 始终保留两位小数。
     *
     * @param amount 金额
     * @return String 格式化后的金额字符串（如 "100.00"）
     */
    fun formatDisplayAmount(amount: Double): String {
        return String.format("%.2f", amount)
    }
}
