package com.accountbook.app.util

import com.accountbook.app.data.entity.AccountRecord
import com.accountbook.app.data.repository.AccountRepository
import java.util.Calendar

/**
 * 测试数据助手
 *
 * 在数据库为空时插入测试数据，方便开发和演示。
 * 测试数据包括本月、上月的各种类型账单记录。
 */
object TestDataHelper {

    /**
     * 插入测试数据
     *
     * 插入以下测试数据：
     * - 本月数据：早餐、午餐、地铁、超市购物、咖啡、网购、聚餐、衣服、水果、电影票、公交、电子产品配件等
     * - 无备注数据：用于测试空备注显示
     * - 上月数据：用于测试跨月显示
     *
     * @param repository 账户数据仓库
     */
    suspend fun insertTestData(repository: AccountRepository) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        val testData = mutableListOf<AccountRecord>()

        // 本月数据
        calendar.set(currentYear, currentMonth, 1, 8, 30, 0)
        testData.add(AccountRecord(amount = 15.0, note = "早餐", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 1, 12, 15, 0)
        testData.add(AccountRecord(amount = 35.5, note = "午餐 - 外卖", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 2, 9, 0, 0)
        testData.add(AccountRecord(amount = 6.0, note = "地铁", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 3, 18, 30, 0)
        testData.add(AccountRecord(amount = 128.0, note = "超市购物", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 5, 10, 0, 0)
        testData.add(AccountRecord(amount = 45.0, note = "咖啡", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 7, 14, 20, 0)
        testData.add(AccountRecord(amount = 89.9, note = "网购 - 日用品", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 10, 19, 0, 0)
        testData.add(AccountRecord(amount = 256.0, note = "聚餐", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 12, 8, 45, 0)
        testData.add(AccountRecord(amount = 12.0, note = "早餐", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 15, 16, 30, 0)
        testData.add(AccountRecord(amount = 199.0, note = "衣服", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 18, 11, 0, 0)
        testData.add(AccountRecord(amount = 42.0, note = "水果", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 20, 20, 15, 0)
        testData.add(AccountRecord(amount = 78.5, note = "电影票 + 零食", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 22, 7, 30, 0)
        testData.add(AccountRecord(amount = 5.0, note = "公交", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 25, 13, 0, 0)
        testData.add(AccountRecord(amount = 168.0, note = "电子产品配件", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        // 无备注的测试数据
        calendar.set(currentYear, currentMonth, 8, 10, 0, 0)
        testData.add(AccountRecord(amount = 20.0, note = "", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(currentYear, currentMonth, 16, 15, 0, 0)
        testData.add(AccountRecord(amount = 33.0, note = "", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        // 上个月数据
        val lastMonth = if (currentMonth == 0) 11 else currentMonth - 1
        val lastMonthYear = if (currentMonth == 0) currentYear - 1 else currentYear

        calendar.set(lastMonthYear, lastMonth, 5, 12, 0, 0)
        testData.add(AccountRecord(amount = 50.0, note = "上月 - 午餐", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(lastMonthYear, lastMonth, 15, 18, 30, 0)
        testData.add(AccountRecord(amount = 200.0, note = "上月 - 购物", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        calendar.set(lastMonthYear, lastMonth, 25, 9, 0, 0)
        testData.add(AccountRecord(amount = 15.5, note = "上月 - 交通", createdAt = calendar.timeInMillis, updatedAt = calendar.timeInMillis))

        // 插入所有测试数据
        testData.forEach { record ->
            repository.insertRecord(record)
        }
    }
}
