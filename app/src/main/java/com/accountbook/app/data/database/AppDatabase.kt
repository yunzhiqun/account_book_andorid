package com.accountbook.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.accountbook.app.data.dao.AccountDao
import com.accountbook.app.data.entity.AccountRecord

/**
 * 应用数据库类
 *
 * 使用 Room 持久化库定义的抽象数据库类。
 * 通过 @Database 注解声明数据库配置和包含的实体。
 *
 * 数据库配置：
 * - 实体列表：AccountRecord（账单记录）
 * - 数据库版本：1
 * - 不导出 Schema 文件
 *
 * 提供数据访问对象（DAO）的实例方法
 */
@Database(entities = [AccountRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /**
     * 获取账单数据访问对象
     *
     * @return AccountDao 实例，提供账单记录的增删改查方法
     */
    abstract fun accountDao(): AccountDao
}
