package com.accountbook.app

import android.app.Application
import com.accountbook.app.data.repository.AccountRepository
import com.accountbook.app.util.TestDataHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 记账本应用类
 *
 * 继承自 Application，是应用的入口点。
 * 通过 @HiltAndroidApp 注解启用 Hilt 依赖注入。
 *
 * 主要功能：
 * - 初始化依赖注入
 * - 在数据库为空时插入测试数据
 */
@HiltAndroidApp
class AccountBookApplication : Application() {

    /** 注入账户数据仓库，用于数据库操作 */
    @Inject
    lateinit var repository: AccountRepository

    /** 应用级协程作用域，使用 SupervisorJob 防止子协程异常影响其他协程 */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * 应用创建时调用
     *
     * 检查数据库是否为空，如果为空则插入测试数据
     */
    override fun onCreate() {
        super.onCreate()
        insertTestDataIfEmpty()
    }

    /**
     * 如果数据库为空则插入测试数据
     *
     * 使用协程在后台线程执行数据库查询和插入操作，
     * 避免阻塞主线程。测试数据包括各种类型的账单记录，
     * 方便开发和演示。
     */
    private fun insertTestDataIfEmpty() {
        applicationScope.launch {
            val records = repository.getAllRecords().first()
            if (records.isEmpty()) {
                TestDataHelper.insertTestData(repository)
            }
        }
    }
}
