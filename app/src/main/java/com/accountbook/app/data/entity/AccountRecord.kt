package com.accountbook.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 账单记录实体类
 *
 * 使用 Room 注解定义数据库表结构。
 * 每条记录代表一笔账单，包含金额、备注和时间戳。
 *
 * 数据库表名：account_records
 *
 * @property id 记录唯一标识，自增主键
 * @property amount 账单金额（正数表示支出）
 * @property note 账单备注，描述消费内容
 * @property createdAt 创建时间戳（毫秒）
 * @property updatedAt 最后更新时间戳（毫秒）
 */
@Entity(tableName = "account_records")
data class AccountRecord(
    /** 记录唯一标识，自增主键 */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 账单金额（正数表示支出） */
    val amount: Double,

    /** 账单备注，描述消费内容 */
    val note: String = "",

    /** 创建时间戳（毫秒） */
    val createdAt: Long = System.currentTimeMillis(),

    /** 最后更新时间戳（毫秒） */
    val updatedAt: Long = System.currentTimeMillis()
)
