package com.accountbook.app.data.backup

import android.content.Context
import android.content.SharedPreferences
import com.accountbook.app.data.entity.AccountRecord
import com.accountbook.app.data.repository.AccountRepository
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 备份管理器
 *
 * 单例类，负责管理应用的本地备份和还原功能。
 * 使用 JSON 格式存储备份数据，保存在应用内部存储的 backups 目录。
 *
 * 主要功能：
 * - 自动备份检查（间隔 7 天）
 * - 执行备份（导出所有数据为 JSON）
 * - 还原备份（从 JSON 文件恢复数据）
 * - 管理备份文件列表
 * - 删除备份文件
 *
 * 备份文件格式：
 * ```json
 * {
 *   "version": 1,
 *   "timestamp": 1234567890,
 *   "recordCount": 100,
 *   "records": [...]
 * }
 * ```
 */
@Singleton
class BackupManager @Inject constructor(
    /** 账户数据仓库 */
    private val repository: AccountRepository,
    /** 应用上下文 */
    private val context: Context
) {
    /** SharedPreferences，用于存储上次备份时间 */
    private val prefs: SharedPreferences = context.getSharedPreferences("backup_prefs", Context.MODE_PRIVATE)

    /** 备份文件存储目录 */
    private val backupDir = File(context.filesDir, "backups")

    companion object {
        /** SharedPreferences 键：上次备份时间 */
        private const val KEY_LAST_BACKUP_TIME = "last_backup_time"

        /** 自动备份间隔：7 天（毫秒） */
        private const val BACKUP_INTERVAL_MS = 7 * 24 * 60 * 60 * 1000L

        /** 备份文件名前缀 */
        private const val BACKUP_FILE_PREFIX = "backup_"

        /** 备份文件扩展名 */
        private const val BACKUP_FILE_SUFFIX = ".json"
    }

    /**
     * 初始化
     *
     * 创建备份目录（如果不存在）
     */
    init {
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
    }

    /**
     * 获取上次备份时间
     *
     * @return Long 上次备份的时间戳（毫秒），未备份过返回 0
     */
    fun getLastBackupTime(): Long {
        return prefs.getLong(KEY_LAST_BACKUP_TIME, 0L)
    }

    /**
     * 检查是否需要自动备份
     *
     * 如果距离上次备份超过 7 天，返回 true
     *
     * @return Boolean 是否需要自动备份
     */
    fun shouldAutoBackup(): Boolean {
        val lastBackupTime = getLastBackupTime()
        return System.currentTimeMillis() - lastBackupTime >= BACKUP_INTERVAL_MS
    }

    /**
     * 执行备份
     *
     * 将所有账单记录导出为 JSON 文件，保存到 backups 目录。
     * 备份成功后更新上次备份时间。
     *
     * @return Result<String> 备份成功返回文件路径，失败返回异常
     */
    suspend fun performBackup(): Result<String> {
        return try {
            val records = repository.getAllRecords().first()
            val backupData = JSONObject()
            val recordsArray = JSONArray()

            // 序列化每条记录
            records.forEach { record ->
                val recordObj = JSONObject()
                recordObj.put("id", record.id)
                recordObj.put("amount", record.amount)
                recordObj.put("note", record.note)
                recordObj.put("createdAt", record.createdAt)
                recordObj.put("updatedAt", record.updatedAt)
                recordsArray.put(recordObj)
            }

            // 构建备份数据
            backupData.put("version", 1)
            backupData.put("timestamp", System.currentTimeMillis())
            backupData.put("recordCount", records.size)
            backupData.put("records", recordsArray)

            // 生成备份文件名（使用时间戳）
            val timestamp = System.currentTimeMillis()
            val backupFile = File(backupDir, "${BACKUP_FILE_PREFIX}${timestamp}${BACKUP_FILE_SUFFIX}")

            // 写入文件
            FileOutputStream(backupFile).use { fos ->
                fos.write(backupData.toString(2).toByteArray())
            }

            // 更新上次备份时间
            prefs.edit().putLong(KEY_LAST_BACKUP_TIME, timestamp).apply()

            Result.success(backupFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 还原备份
     *
     * 从 JSON 备份文件恢复数据。先清空现有数据，再插入备份中的记录。
     *
     * @param backupFilePath 备份文件路径
     * @return Result<Int> 还原成功返回恢复的记录数，失败返回异常
     */
    suspend fun restoreBackup(backupFilePath: String): Result<Int> {
        return try {
            val backupFile = File(backupFilePath)
            if (!backupFile.exists()) {
                return Result.failure(Exception("备份文件不存在"))
            }

            // 读取备份文件
            val backupData = JSONObject(FileInputStream(backupFile).use { fis ->
                fis.readBytes().toString(Charsets.UTF_8)
            })

            // 反序列化记录
            val recordsArray = backupData.getJSONArray("records")
            val records = mutableListOf<AccountRecord>()

            for (i in 0 until recordsArray.length()) {
                val recordObj = recordsArray.getJSONObject(i)
                val record = AccountRecord(
                    id = recordObj.getLong("id"),
                    amount = recordObj.getDouble("amount"),
                    note = recordObj.getString("note"),
                    createdAt = recordObj.getLong("createdAt"),
                    updatedAt = recordObj.getLong("updatedAt")
                )
                records.add(record)
            }

            // 清空现有数据并插入备份数据
            repository.deleteAllRecords()
            records.forEach { record ->
                repository.insertRecord(record)
            }

            Result.success(records.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取可用备份列表
     *
     * 扫描 backups 目录，返回所有有效备份文件的信息，
     * 按修改时间倒序排列（最新的在前）。
     *
     * @return List<BackupInfo> 备份信息列表
     */
    fun getAvailableBackups(): List<BackupInfo> {
        val backups = mutableListOf<BackupInfo>()
        val files = backupDir.listFiles()?.filter { 
            it.name.startsWith(BACKUP_FILE_PREFIX) && it.name.endsWith(BACKUP_FILE_SUFFIX) 
        } ?: return backups

        files.sortedByDescending { it.lastModified() }.forEach { file ->
            try {
                val backupData = JSONObject(FileInputStream(file).use { fis ->
                    fis.readBytes().toString(Charsets.UTF_8)
                })
                backups.add(
                    BackupInfo(
                        filePath = file.absolutePath,
                        timestamp = backupData.getLong("timestamp"),
                        recordCount = backupData.getInt("recordCount")
                    )
                )
            } catch (e: Exception) {
                // 跳过无效的备份文件
            }
        }

        return backups
    }

    /**
     * 删除备份文件
     *
     * @param backupFilePath 备份文件路径
     * @return Boolean 删除成功返回 true，文件不存在返回 false
     */
    fun deleteBackup(backupFilePath: String): Boolean {
        val file = File(backupFilePath)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }
}

/**
 * 备份信息数据类
 *
 * @property filePath 备份文件路径
 * @property timestamp 备份时间戳（毫秒）
 * @property recordCount 备份包含的记录数
 */
data class BackupInfo(
    val filePath: String,
    val timestamp: Long,
    val recordCount: Int
)
