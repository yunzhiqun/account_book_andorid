package com.accountbook.app.di

import android.content.Context
import androidx.room.Room
import com.accountbook.app.data.dao.AccountDao
import com.accountbook.app.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库依赖注入模块
 *
 * 使用 Hilt 提供数据库和数据访问对象的单例实例。
 * 通过 @Module 和 @InstallIn 注解声明为 Hilt 模块，
 * 安装到 SingletonComponent 中，确保全局唯一实例。
 *
 * 提供的依赖：
 * - AppDatabase：Room 数据库实例
 * - AccountDao：账单数据访问对象
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 提供数据库实例
     *
     * 使用 Room.databaseBuilder 创建数据库，
     * 数据库文件名为 "account_book.db"。
     *
     * @param context 应用上下文，由 Hilt 注入
     * @return AppDatabase 数据库单例实例
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "account_book.db"
        ).build()
    }

    /**
     * 提供数据访问对象实例
     *
     * 从数据库实例中获取 AccountDao。
     *
     * @param database 数据库实例，由 Hilt 注入
     * @return AccountDao 数据访问对象单例实例
     */
    @Provides
    @Singleton
    fun provideAccountDao(database: AppDatabase): AccountDao {
        return database.accountDao()
    }
}
