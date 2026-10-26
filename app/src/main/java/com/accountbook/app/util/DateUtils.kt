package com.accountbook.app.util

import java.util.Calendar

/**
 * 日期工具类
 *
 * 提供日期时间相关的工具方法，包括：
 * - 获取当前月份的时间范围
 * - 解析搜索时间范围
 * - 格式化日期时间
 * - 从时间戳提取年/月/日
 * - 将日期组件转换为时间戳
 */
object DateUtils {

    /**
     * 获取当前月份的时间范围
     *
     * 返回当前月份第一天 00:00:00.000 到最后一天 23:59:59.999 的时间戳。
     *
     * @return Pair<Long, Long> 开始和结束时间戳（毫秒）
     */
    fun getCurrentMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        // 设置开始时间：当月第一天 00:00:00.000
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        // 设置结束时间：当月最后一天 23:59:59.999
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis

        return Pair(startTime, endTime)
    }

    /**
     * 解析搜索时间范围
     *
     * 将年月日时分秒字符串转换为时间戳。
     * 如果字符串无法解析，使用默认值（当前日期或 0/23:59:59）。
     *
     * @param startYear 开始年份
     * @param startMonth 开始月份
     * @param startDay 开始日期
     * @param startHour 开始小时
     * @param startMinute 开始分钟
     * @param startSecond 开始秒
     * @param endYear 结束年份
     * @param endMonth 结束月份
     * @param endDay 结束日期
     * @param endHour 结束小时
     * @param endMinute 结束分钟
     * @param endSecond 结束秒
     * @return Pair<Long, Long> 开始和结束时间戳（毫秒）
     */
    fun parseSearchRange(
        startYear: String, startMonth: String, startDay: String,
        startHour: String, startMinute: String, startSecond: String,
        endYear: String, endMonth: String, endDay: String,
        endHour: String, endMinute: String, endSecond: String
    ): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // 解析开始时间
        val sYear = startYear.toIntOrNull() ?: currentYear
        val sMonth = startMonth.toIntOrNull() ?: currentMonth
        val sDay = startDay.toIntOrNull() ?: currentDay
        val sHour = startHour.toIntOrNull() ?: 0
        val sMinute = startMinute.toIntOrNull() ?: 0
        val sSecond = startSecond.toIntOrNull() ?: 0

        calendar.set(sYear, sMonth - 1, sDay, sHour, sMinute, sSecond)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        // 解析结束时间
        val eYear = endYear.toIntOrNull() ?: currentYear
        val eMonth = endMonth.toIntOrNull() ?: currentMonth
        val eDay = endDay.toIntOrNull() ?: currentDay
        val eHour = endHour.toIntOrNull() ?: 23
        val eMinute = endMinute.toIntOrNull() ?: 59
        val eSecond = endSecond.toIntOrNull() ?: 59

        calendar.set(eYear, eMonth - 1, eDay, eHour, eMinute, eSecond)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis

        return Pair(startTime, endTime)
    }

    /**
     * 格式化日期时间
     *
     * 将时间戳格式化为 "yyyy-MM-dd HH:mm" 格式。
     *
     * @param timestamp 时间戳（毫秒）
     * @return String 格式化后的日期时间字符串
     */
    fun formatDateTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')} " +
                "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    }

    /**
     * 从时间戳获取年份
     *
     * @param timestamp 时间戳（毫秒）
     * @return Int 年份
     */
    fun getYearFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.get(Calendar.YEAR)
    }

    /**
     * 从时间戳获取月份
     *
     * @param timestamp 时间戳（毫秒）
     * @return Int 月份（1-12）
     */
    fun getMonthFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.get(Calendar.MONTH) + 1
    }

    /**
     * 从时间戳获取日期
     *
     * @param timestamp 时间戳（毫秒）
     * @return Int 日期（1-31）
     */
    fun getDayFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 将日期组件转换为时间戳
     *
     * @param year 年份
     * @param month 月份（1-12）
     * @param day 日期（1-31）
     * @param hour 小时（0-23）
     * @param minute 分钟（0-59）
     * @param second 秒（0-59）
     * @param millisecond 毫秒（0-999）
     * @return Long 时间戳（毫秒）
     */
    fun toTimestamp(
        year: Int, month: Int, day: Int,
        hour: Int, minute: Int, second: Int,
        millisecond: Int
    ): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, second)
        calendar.set(Calendar.MILLISECOND, millisecond)
        return calendar.timeInMillis
    }
}
