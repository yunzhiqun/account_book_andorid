package com.accountbook.app.di

import android.content.Context
import com.accountbook.app.data.backup.BackupManager
import com.accountbook.app.data.repository.AccountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 备份功能依赖注入模块
 *
 * 使用 Hilt 提供 BackupManager 的单例实例。
 * 通过 @Module 和 @InstallIn 注解声明为 Hilt 模块，
 * 安装到 SingletonComponent 中，确保全局唯一实例。
 *
 * 提供的依赖：
 * - BackupManager：备份管理器实例
 */
@Module
@InstallIn(SingletonComponent::class)
object BackupModule {

    /**
     * 提供备份管理器实例
     *
     * 创建 BackupManager 实例，注入应用上下文和数据仓库。
     *
     * @param context 应用上下文，由 Hilt 注入
     * @param repository 账户数据仓库，由 Hilt 注入
     * @return BackupManager 备份管理器单例实例
     */
    @Provides
    @Singleton
    fun provideBackupManager(
        @ApplicationContext context: Context,
        repository: AccountRepository
    ): BackupManager {
        return BackupManager(repository, context)
    }
}
