package com.accountbook.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.accountbook.app.data.entity.AccountRecord
import kotlinx.coroutines.flow.Flow

/**
 * 账单数据访问对象（DAO）
 *
 * 定义数据库操作的接口，使用 Room 注解声明 SQL 查询。
 * 所有方法都是挂起函数或返回 Flow，支持协程和响应式编程。
 *
 * 主要功能：
 * - 查询所有记录（按创建时间倒序）
 * - 按时间范围查询记录
 * - 插入、更新、删除单条记录
 * - 批量删除记录
 * - 根据 ID 查询记录
 * - 删除所有记录（用于备份还原）
 */
@Dao
interface AccountDao {

    /**
     * 获取所有账单记录
     *
     * @return Flow<List<AccountRecord>> 响应式数据流，按创建时间倒序排列
     */
    @Query("SELECT * FROM account_records ORDER BY createdAt DESC")
    fun getAllRecords(): Flow<List<AccountRecord>>

    /**
     * 按时间范围获取账单记录
     *
     * @param startTime 开始时间戳（毫秒）
     * @param endTime 结束时间戳（毫秒）
     * @return Flow<List<AccountRecord>> 响应式数据流，按创建时间倒序排列
     */
    @Query("""
        SELECT * FROM account_records 
        WHERE createdAt BETWEEN :startTime AND :endTime 
        ORDER BY createdAt DESC
    """)
    fun getRecordsByDateRange(startTime: Long, endTime: Long): Flow<List<AccountRecord>>

    /**
     * 插入账单记录
     *
     * 如果记录已存在（ID 相同），则替换旧记录。
     *
     * @param record 要插入的账单记录
     * @return Long 插入记录的 ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AccountRecord): Long

    /**
     * 更新账单记录
     *
     * @param record 更新后的账单记录
     */
    @Update
    suspend fun updateRecord(record: AccountRecord)

    /**
     * 删除账单记录
     *
     * @param record 要删除的账单记录
     */
    @Delete
    suspend fun deleteRecord(record: AccountRecord)

    /**
     * 批量删除账单记录
     *
     * @param ids 要删除的记录 ID 列表
     */
    @Query("DELETE FROM account_records WHERE id IN (:ids)")
    suspend fun deleteBatch(ids: List<Long>)

    /**
     * 根据 ID 获取账单记录
     *
     * @param id 记录 ID
     * @return AccountRecord? 记录对象，不存在时返回 null
     */
    @Query("SELECT * FROM account_records WHERE id = :id")
    suspend fun getRecordById(id: Long): AccountRecord?

    /**
     * 删除所有账单记录
     *
     * 用于备份还原功能，先清空数据库再插入备份数据。
     */
    @Query("DELETE FROM account_records")
    suspend fun deleteAllRecords()
}
