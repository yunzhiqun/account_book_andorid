package com.accountbook.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accountbook.app.data.backup.BackupInfo
import com.accountbook.app.data.backup.BackupManager
import com.accountbook.app.data.entity.AccountRecord
import com.accountbook.app.data.repository.AccountRepository
import com.accountbook.app.util.AmountUtils
import com.accountbook.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 账户视图模型
 *
 * 使用 Hilt 依赖注入的 ViewModel，管理应用的业务逻辑和 UI 状态。
 * 通过 @HiltViewModel 注解声明，由 Hilt 自动创建实例。
 *
 * 主要职责：
 * - 管理账单数据的增删改查
 * - 管理 UI 状态（加载、选择、展开等）
 * - 管理备份和还原功能
 * - 生成复制文本（简单/完整格式）
 * - 管理对话框显示状态
 */
@HiltViewModel
class AccountViewModel @Inject constructor(
    /** 账户数据仓库，提供数据库操作 */
    private val repository: AccountRepository,
    /** 备份管理器，处理备份和还原 */
    private val backupManager: BackupManager
) : ViewModel() {

    // ==================== UI 状态 ====================

    /** 主页 UI 状态 */
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /** 当前显示的账单记录列表 */
    private val _currentRecords = MutableStateFlow<List<AccountRecord>>(emptyList())
    val currentRecords: StateFlow<List<AccountRecord>> = _currentRecords.asStateFlow()

    /** 当前记录总金额 */
    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount: StateFlow<Double> = _totalAmount.asStateFlow()

    // ==================== 复制文本 ====================

    /** 简单格式复制文本（仅金额和备注） */
    private val _simpleCopyText = MutableStateFlow("")
    val simpleCopyText: StateFlow<String> = _simpleCopyText.asStateFlow()

    /** 完整格式复制文本（包含时间戳） */
    private val _fullCopyText = MutableStateFlow("")
    val fullCopyText: StateFlow<String> = _fullCopyText.asStateFlow()

    // ==================== 对话框状态 ====================

    /** 是否显示新增对话框 */
    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    /** 当前正在编辑的记录 */
    private val _editingRecord = MutableStateFlow<AccountRecord?>(null)
    val editingRecord: StateFlow<AccountRecord?> = _editingRecord.asStateFlow()

    /** 是否显示删除确认对话框 */
    private val _showDeleteConfirm = MutableStateFlow(false)
    val showDeleteConfirm: StateFlow<Boolean> = _showDeleteConfirm.asStateFlow()

    /** 是否显示复制选项对话框 */
    private val _showCopyDialog = MutableStateFlow(false)
    val showCopyDialog: StateFlow<Boolean> = _showCopyDialog.asStateFlow()

    /** Snackbar 消息 */
    private val _showSnackbar = MutableStateFlow<String?>(null)
    val showSnackbar: StateFlow<String?> = _showSnackbar.asStateFlow()

    // ==================== 备份状态 ====================

    /** 是否显示备份对话框 */
    private val _showBackupDialog = MutableStateFlow(false)
    val showBackupDialog: StateFlow<Boolean> = _showBackupDialog.asStateFlow()

    /** 是否显示还原对话框 */
    private val _showRestoreDialog = MutableStateFlow(false)
    val showRestoreDialog: StateFlow<Boolean> = _showRestoreDialog.asStateFlow()

    /** 可用备份列表 */
    private val _availableBackups = MutableStateFlow<List<BackupInfo>>(emptyList())
    val availableBackups: StateFlow<List<BackupInfo>> = _availableBackups.asStateFlow()

    /** 备份/还原操作是否进行中 */
    private val _isBackupInProgress = MutableStateFlow(false)
    val isBackupInProgress: StateFlow<Boolean> = _isBackupInProgress.asStateFlow()

    // ==================== 查询状态 ====================

    /** 当前查询的时间范围，null 表示查询全部 */
    private var currentQueryRange: Pair<Long, Long>? = null

    /** 数据观察协程 Job */
    private var observeJob: Job? = null

    /**
     * 初始化
     *
     * 加载全部数据并检查是否需要自动备份
     */
    init {
        loadAllData()
        checkAndAutoBackup()
    }

    // ==================== 数据加载 ====================

    /**
     * 加载全部数据
     *
     * 清除查询范围，观察所有记录
     */
    fun loadAllData() {
        currentQueryRange = null
        observeAllRecords()
    }

    /**
     * 加载当前月份数据
     *
     * 设置查询范围为当前月份，观察该范围内的记录
     */
    fun loadCurrentMonthData() {
        currentQueryRange = DateUtils.getCurrentMonthRange()
        val (startTime, endTime) = currentQueryRange!!
        observeRecords(startTime, endTime)
    }

    /**
     * 按时间范围查询
     *
     * @param startTime 开始时间戳（毫秒）
     * @param endTime 结束时间戳（毫秒）
     */
    fun queryByDateRange(startTime: Long, endTime: Long) {
        currentQueryRange = Pair(startTime, endTime)
        observeRecords(startTime, endTime)
    }

    /**
     * 观察所有记录
     *
     * 取消之前的观察 Job，订阅全部记录数据流，
     * 更新记录列表、总金额和复制文本
     */
    private fun observeAllRecords() {
        observeJob?.cancel()
        _uiState.update { it.copy(isLoading = true) }
        repository.getAllRecords()
            .onEach { records ->
                _currentRecords.value = records
                _totalAmount.value = records.sumOf { it.amount }
                _simpleCopyText.value = AmountUtils.generateSimpleCopyText(records)
                _fullCopyText.value = AmountUtils.generateFullCopyText(records)
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)
            .also { observeJob = it }
    }

    /**
     * 观察指定时间范围内的记录
     *
     * @param startTime 开始时间戳（毫秒）
     * @param endTime 结束时间戳（毫秒）
     */
    private fun observeRecords(startTime: Long, endTime: Long) {
        observeJob?.cancel()
        _uiState.update { it.copy(isLoading = true) }
        repository.getRecordsByDateRange(startTime, endTime)
            .onEach { records ->
                _currentRecords.value = records
                _totalAmount.value = records.sumOf { it.amount }
                _simpleCopyText.value = AmountUtils.generateSimpleCopyText(records)
                _fullCopyText.value = AmountUtils.generateFullCopyText(records)
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)
            .also { observeJob = it }
    }

    // ==================== 数据操作 ====================

    /**
     * 插入账单记录
     *
     * @param amount 金额
     * @param note 备注
     * @param customTimestamp 自定义时间戳，默认为当前时间
     */
    fun insertRecord(amount: Double, note: String, customTimestamp: Long? = null) {
        viewModelScope.launch {
            val timestamp = customTimestamp ?: System.currentTimeMillis()
            repository.insertRecord(AccountRecord(amount = amount, note = note, createdAt = timestamp, updatedAt = timestamp))
            _showAddDialog.value = false
            refreshCurrentQuery()
            _showSnackbar.value = "保存成功"
        }
    }

    /**
     * 更新账单记录
     *
     * @param record 更新后的记录
     */
    fun updateRecord(record: AccountRecord) {
        viewModelScope.launch {
            repository.updateRecord(record)
            _editingRecord.value = null
            refreshCurrentQuery()
            _showSnackbar.value = "修改成功"
        }
    }

    /**
     * 删除单条账单记录
     *
     * @param record 要删除的记录
     */
    fun deleteRecord(record: AccountRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
            refreshCurrentQuery()
            _showSnackbar.value = "删除成功"
        }
    }

    /**
     * 批量删除账单记录
     *
     * @param ids 要删除的记录 ID 列表
     */
    fun deleteBatch(ids: List<Long>) {
        viewModelScope.launch {
            repository.deleteBatch(ids)
            _uiState.update { it.copy(isSelectionMode = false, selectedIds = emptySet()) }
            _showDeleteConfirm.value = false
            refreshCurrentQuery()
            _showSnackbar.value = "已删除 ${ids.size} 条记录"
        }
    }

    /**
     * 刷新当前查询
     *
     * 根据当前查询范围重新加载数据
     */
    private fun refreshCurrentQuery() {
        currentQueryRange?.let { (start, end) ->
            observeRecords(start, end)
        } ?: observeAllRecords()
    }

    // ==================== 选择模式 ====================

    /**
     * 切换选择模式
     *
     * 进入或退出多选模式，清空已选 ID
     */
    fun toggleSelectionMode() {
        _uiState.update { state ->
            state.copy(
                isSelectionMode = !state.isSelectionMode,
                selectedIds = emptySet()
            )
        }
    }

    /**
     * 切换单条记录的选择状态
     *
     * @param recordId 记录 ID
     */
    fun toggleRecordSelection(recordId: Long) {
        _uiState.update { state ->
            val newSelectedIds = state.selectedIds.toMutableSet()
            if (newSelectedIds.contains(recordId)) {
                newSelectedIds.remove(recordId)
            } else {
                newSelectedIds.add(recordId)
            }
            state.copy(selectedIds = newSelectedIds)
        }
    }

    /**
     * 全选/取消全选当前可见记录
     */
    fun selectAllVisible() {
        _uiState.update { state ->
            val allIds = _currentRecords.value.map { it.id }.toSet()
            state.copy(selectedIds = if (state.selectedIds == allIds) emptySet() else allIds)
        }
    }

    // ==================== 对话框控制 ====================

    /** 显示新增对话框 */
    fun showAddDialog() { _showAddDialog.value = true }

    /** 关闭新增对话框 */
    fun dismissAddDialog() { _showAddDialog.value = false }

    /**
     * 开始编辑记录
     *
     * @param record 要编辑的记录
     */
    fun startEditing(record: AccountRecord) { _editingRecord.value = record }

    /** 关闭编辑对话框 */
    fun dismissEditing() { _editingRecord.value = null }

    /** 显示删除确认对话框 */
    fun showDeleteConfirmDialog() { _showDeleteConfirm.value = true }

    /** 关闭删除确认对话框 */
    fun dismissDeleteConfirm() { _showDeleteConfirm.value = false }

    /** 显示复制选项对话框 */
    fun showCopyDialog() { _showCopyDialog.value = true }

    /** 关闭复制选项对话框 */
    fun dismissCopyDialog() { _showCopyDialog.value = false }

    /** 清除 Snackbar 消息 */
    fun clearSnackbar() { _showSnackbar.value = null }

    // ==================== 备份功能 ====================

    /**
     * 检查并执行自动备份
     *
     * 如果距离上次备份超过 7 天，自动执行备份
     */
    fun checkAndAutoBackup() {
        if (backupManager.shouldAutoBackup()) {
            performBackup()
        }
    }

    /**
     * 执行备份
     *
     * 将所有数据导出为 JSON 文件保存到本地
     */
    fun performBackup() {
        viewModelScope.launch {
            _isBackupInProgress.value = true
            val result = backupManager.performBackup()
            _isBackupInProgress.value = false
            result.fold(
                onSuccess = { backupPath ->
                    _showSnackbar.value = "备份成功"
                },
                onFailure = { error ->
                    _showSnackbar.value = "备份失败: ${error.message}"
                }
            )
        }
    }

    /** 显示备份对话框 */
    fun showBackupDialog() { _showBackupDialog.value = true }

    /** 关闭备份对话框 */
    fun dismissBackupDialog() { _showBackupDialog.value = false }

    /**
     * 显示还原对话框
     *
     * 加载可用备份列表
     */
    fun showRestoreDialog() {
        viewModelScope.launch {
            _availableBackups.value = backupManager.getAvailableBackups()
            _showRestoreDialog.value = true
        }
    }

    /** 关闭还原对话框 */
    fun dismissRestoreDialog() { _showRestoreDialog.value = false }

    /**
     * 还原备份
     *
     * @param backupFilePath 备份文件路径
     */
    fun restoreBackup(backupFilePath: String) {
        viewModelScope.launch {
            _isBackupInProgress.value = true
            val result = backupManager.restoreBackup(backupFilePath)
            _isBackupInProgress.value = false
            result.fold(
                onSuccess = { recordCount ->
                    _showSnackbar.value = "还原成功，共恢复 $recordCount 条记录"
                    _showRestoreDialog.value = false
                    loadAllData()
                },
                onFailure = { error ->
                    _showSnackbar.value = "还原失败: ${error.message}"
                }
            )
        }
    }

    /**
     * 删除备份
     *
     * @param backupFilePath 备份文件路径
     */
    fun deleteBackup(backupFilePath: String) {
        viewModelScope.launch {
            backupManager.deleteBackup(backupFilePath)
            _availableBackups.value = backupManager.getAvailableBackups()
            _showSnackbar.value = "备份已删除"
        }
    }

    // ==================== 折叠展开 ====================

    /**
     * 切换年份的展开/折叠状态
     *
     * @param year 年份
     */
    fun toggleYear(year: Int) {
        _uiState.update { state ->
            val newExpanded = state.expandedYears.toMutableSet()
            if (newExpanded.contains(year)) newExpanded.remove(year) else newExpanded.add(year)
            state.copy(expandedYears = newExpanded, isExpansionInitialized = true)
        }
    }

    /**
     * 切换月份的展开/折叠状态
     *
     * @param key 月份键（格式：年-月）
     */
    fun toggleMonth(key: String) {
        _uiState.update { state ->
            val newExpanded = state.expandedMonths.toMutableSet()
            if (newExpanded.contains(key)) newExpanded.remove(key) else newExpanded.add(key)
            state.copy(expandedMonths = newExpanded, isExpansionInitialized = true)
        }
    }
}

/**
 * 主页 UI 状态数据类
 *
 * @property isLoading 是否正在加载数据
 * @property isSelectionMode 是否处于多选模式
 * @property selectedIds 已选中的记录 ID 集合
 * @property expandedYears 已展开的年份集合
 * @property expandedMonths 已展开的月份集合（格式：年-月）
 * @property isExpansionInitialized 折叠状态是否已初始化
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val isSelectionMode: Boolean = false,
    val selectedIds: Set<Long> = emptySet(),
    val expandedYears: Set<Int> = emptySet(),
    val expandedMonths: Set<String> = emptySet(),
    val isExpansionInitialized: Boolean = false
)
