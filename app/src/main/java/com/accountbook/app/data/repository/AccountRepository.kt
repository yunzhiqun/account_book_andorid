package com.accountbook.app.data.repository

import com.accountbook.app.data.dao.AccountDao
import com.accountbook.app.data.entity.AccountRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 账户数据仓库
 *
 * 封装数据访问层，提供统一的业务逻辑接口。
 * 通过 Hilt 依赖注入获取 DAO 实例。
 *
 * 主要职责：
 * - 代理 DAO 的数据库操作
 * - 在更新记录时自动更新时间戳
 * - 提供响应式数据流（Flow）
 */
class AccountRepository @Inject constructor(
    /** 账单数据访问对象 */
    private val accountDao: AccountDao
) {
    /**
     * 获取所有账单记录
     *
     * @return Flow<List<AccountRecord>> 响应式数据流
     */
    fun getAllRecords(): Flow<List<AccountRecord>> = accountDao.getAllRecords()

    /**
     * 按时间范围获取账单记录
     *
     * @param startTime 开始时间戳（毫秒）
     * @param endTime 结束时间戳（毫秒）
     * @return Flow<List<AccountRecord>> 响应式数据流
     */
    fun getRecordsByDateRange(startTime: Long, endTime: Long): Flow<List<AccountRecord>> =
        accountDao.getRecordsByDateRange(startTime, endTime)

    /**
     * 插入账单记录
     *
     * @param record 要插入的账单记录
     * @return Long 插入记录的 ID
     */
    suspend fun insertRecord(record: AccountRecord): Long =
        accountDao.insertRecord(record)

    /**
     * 更新账单记录
     *
     * 自动将 updatedAt 字段更新为当前时间戳。
     *
     * @param record 更新后的账单记录
     */
    suspend fun updateRecord(record: AccountRecord) =
        accountDao.updateRecord(record.copy(updatedAt = System.currentTimeMillis()))

    /**
     * 删除账单记录
     *
     * @param record 要删除的账单记录
     */
    suspend fun deleteRecord(record: AccountRecord) =
        accountDao.deleteRecord(record)

    /**
     * 批量删除账单记录
     *
     * @param ids 要删除的记录 ID 列表
     */
    suspend fun deleteBatch(ids: List<Long>) =
        accountDao.deleteBatch(ids)

    /**
     * 根据 ID 获取账单记录
     *
     * @param id 记录 ID
     * @return AccountRecord? 记录对象，不存在时返回 null
     */
    suspend fun getRecordById(id: Long): AccountRecord? =
        accountDao.getRecordById(id)

    /**
     * 删除所有账单记录
     *
     * 用于备份还原功能。
     */
    suspend fun deleteAllRecords() =
        accountDao.deleteAllRecords()
}
