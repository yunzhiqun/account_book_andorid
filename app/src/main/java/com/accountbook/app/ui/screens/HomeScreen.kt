package com.accountbook.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.accountbook.app.data.entity.AccountRecord
import com.accountbook.app.ui.components.AddEditDialog
import com.accountbook.app.ui.components.CopyOptionDialog
import com.accountbook.app.ui.components.MonthGroupHeader
import com.accountbook.app.ui.components.RecordItem
import com.accountbook.app.ui.components.TotalBar
import com.accountbook.app.ui.components.YearGroupHeader
import com.accountbook.app.ui.theme.BluePrimary
import com.accountbook.app.ui.theme.ErrorRed
import com.accountbook.app.util.CopyUtils
import com.accountbook.app.util.DateUtils
import com.accountbook.app.viewmodel.AccountViewModel

/**
 * 主页界面
 *
 * 显示账单列表，支持以下功能：
 * - 按年/月分组折叠显示
 * - 新增、编辑、删除账单
 * - 多选批量删除
 * - 复制总计信息
 * - 备份和还原数据
 * - 跳转到查询页面
 *
 * @param onNavigateToSearch 跳转到查询页面的回调
 * @param viewModel 账户视图模型
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val records by viewModel.currentRecords.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val editingRecord by viewModel.editingRecord.collectAsState()
    val showDeleteConfirm by viewModel.showDeleteConfirm.collectAsState()
    val showCopyDialog by viewModel.showCopyDialog.collectAsState()
    val simpleCopyText by viewModel.simpleCopyText.collectAsState()
    val fullCopyText by viewModel.fullCopyText.collectAsState()
    val snackbarMsg by viewModel.showSnackbar.collectAsState()
    val showBackupDialog by viewModel.showBackupDialog.collectAsState()
    val showRestoreDialog by viewModel.showRestoreDialog.collectAsState()
    val availableBackups by viewModel.availableBackups.collectAsState()
    val isBackupInProgress by viewModel.isBackupInProgress.collectAsState()
    val context = LocalContext.current

    // 显示 Snackbar 消息
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        // 顶部应用栏
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isSelectionMode) "已选 ${uiState.selectedIds.size} 项" else "记账本",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary),
                actions = {
                    if (uiState.isSelectionMode) {
                        // 多选模式下的操作按钮
                        IconButton(onClick = { viewModel.selectAllVisible() }) {
                            Icon(Icons.Default.SelectAll, "全选", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.showDeleteConfirmDialog() }) {
                            Icon(Icons.Default.Delete, "删除", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(Icons.Default.Close, "取消", tint = Color.White)
                        }
                    } else {
                        // 普通模式下的操作按钮
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(Icons.Default.Search, "搜索", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.performBackup() }) {
                            Icon(Icons.Default.Save, "备份", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.showRestoreDialog() }) {
                            Icon(Icons.Default.Restore, "还原", tint = Color.White)
                        }
                    }
                }
            )
        },
        // 悬浮操作按钮（新增）
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick = { viewModel.showAddDialog() },
                    containerColor = BluePrimary
                ) {
                    Icon(Icons.Default.Add, "新增", tint = Color.White)
                }
            }
        },
        // 底部总计栏
        bottomBar = {
            TotalBar(
                totalAmount = totalAmount,
                recordCount = records.size,
                onCopyClick = {
                    if (records.isNotEmpty()) {
                        viewModel.showCopyDialog()
                    } else {
                        Toast.makeText(context, "暂无记录可复制", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                // 加载指示器
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BluePrimary
                )
            } else if (records.isEmpty()) {
                // 空状态
                EmptyState()
            } else {
                // 记录列表
                RecordList(
                    records = records,
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }

    // 新增对话框
    if (showAddDialog) {
        AddEditDialog(
            onDismiss = { viewModel.dismissAddDialog() },
            onConfirm = { amount, note, timestamp ->
                viewModel.insertRecord(amount, note, timestamp)
            }
        )
    }

    // 编辑对话框
    editingRecord?.let { record ->
        AddEditDialog(
            record = record,
            onDismiss = { viewModel.dismissEditing() },
            onConfirm = { amount, note, timestamp ->
                val updatedRecord = record.copy(
                    amount = amount,
                    note = note,
                    updatedAt = System.currentTimeMillis(),
                    createdAt = timestamp ?: record.createdAt
                )
                viewModel.updateRecord(updatedRecord)
            }
        )
    }

    // 复制选项对话框
    if (showCopyDialog) {
        CopyOptionDialog(
            simpleText = simpleCopyText,
            fullText = fullCopyText,
            onDismiss = { viewModel.dismissCopyDialog() },
            onCopySimple = {
                CopyUtils.copyToClipboard(context, simpleCopyText)
                Toast.makeText(context, "已复制简要内容到剪贴板", Toast.LENGTH_SHORT).show()
                viewModel.dismissCopyDialog()
            },
            onCopyFull = {
                CopyUtils.copyToClipboard(context, fullCopyText)
                Toast.makeText(context, "已复制全部内容到剪贴板", Toast.LENGTH_SHORT).show()
                viewModel.dismissCopyDialog()
            }
        )
    }

    // 删除确认对话框
    if (showDeleteConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirm() },
            title = { Text("确认删除") },
            text = { Text("确定删除选中的 ${uiState.selectedIds.size} 条记录？") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.deleteBatch(uiState.selectedIds.toList()) }
                ) {
                    Text("删除", color = ErrorRed)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { viewModel.dismissDeleteConfirm() }) {
                    Text("取消")
                }
            }
        )
    }

    // 还原对话框
    if (showRestoreDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.dismissRestoreDialog() },
            title = { Text("选择备份还原") },
            text = {
                Column {
                    if (availableBackups.isEmpty()) {
                        Text("暂无可用备份")
                    } else {
                        availableBackups.forEach { backup ->
                            androidx.compose.material3.TextButton(
                                onClick = { viewModel.restoreBackup(backup.filePath) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = "备份时间：${DateUtils.formatDateTime(backup.timestamp)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "记录数：${backup.recordCount}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = com.accountbook.app.ui.theme.TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { viewModel.dismissRestoreDialog() }) {
                    Text("关闭")
                }
            }
        )
    }

    // 备份/还原进行中遮罩
    if (isBackupInProgress) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Card(
                modifier = Modifier.padding(32.dp),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = BluePrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("正在处理...")
                }
            }
        }
    }
}

/**
 * 空状态组件
 *
 * 当没有账单记录时显示，提示用户点击新增按钮开始记账。
 */
@Composable
internal fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            text = "🐋",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "暂无记录",
            style = MaterialTheme.typography.titleMedium,
            color = com.accountbook.app.ui.theme.TextSecondary
        )
        Text(
            text = "点击右下角 + 开始记账吧",
            style = MaterialTheme.typography.bodyMedium,
            color = com.accountbook.app.ui.theme.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 记录列表组件
 *
 * 按年/月分组显示账单记录，支持折叠展开。
 * 默认展开最近一年和最近月份。
 *
 * @param records 账单记录列表
 * @param uiState UI 状态
 * @param viewModel 账户视图模型
 */
@Composable
internal fun RecordList(
    records: List<AccountRecord>,
    uiState: com.accountbook.app.viewmodel.HomeUiState,
    viewModel: AccountViewModel
) {
    // 按年份分组
    val groupedByYear = records.groupBy { DateUtils.getYearFromTimestamp(it.createdAt) }
    val sortedYears = groupedByYear.keys.sortedDescending()

    // 默认展开项
    val defaultExpandedYear = sortedYears.firstOrNull()
    val defaultExpandedMonth = records.firstOrNull()?.let {
        "${DateUtils.getYearFromTimestamp(it.createdAt)}-${DateUtils.getMonthFromTimestamp(it.createdAt)}"
    }

    // 展开状态
    val expandedYears = if (uiState.isExpansionInitialized) {
        uiState.expandedYears
    } else {
        defaultExpandedYear?.let { setOf(it) } ?: emptySet()
    }
    val expandedMonths = if (uiState.isExpansionInitialized) {
        uiState.expandedMonths
    } else {
        defaultExpandedMonth?.let { setOf(it) } ?: emptySet()
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        sortedYears.forEach { year ->
            val yearRecords = groupedByYear[year] ?: emptyList()
            val yearTotal = yearRecords.sumOf { it.amount }
            val isYearExpanded = expandedYears.contains(year)

            // 年份标题
            item(key = "year_$year") {
                YearGroupHeader(
                    year = year,
                    isExpanded = isYearExpanded,
                    recordCount = yearRecords.size,
                    yearTotal = yearTotal,
                    onToggle = { viewModel.toggleYear(year) }
                )
            }

            if (isYearExpanded) {
                // 按月份分组
                val groupedByMonth = yearRecords.groupBy { DateUtils.getMonthFromTimestamp(it.createdAt) }
                val sortedMonths = groupedByMonth.keys.sortedDescending()

                sortedMonths.forEach { month ->
                    val monthRecords = groupedByMonth[month] ?: emptyList()
                    val monthTotal = monthRecords.sumOf { it.amount }
                    val monthKey = "$year-$month"
                    val isMonthExpanded = expandedMonths.contains(monthKey)

                    // 月份标题
                    item(key = "month_$monthKey") {
                        MonthGroupHeader(
                            month = month,
                            isExpanded = isMonthExpanded,
                            recordCount = monthRecords.size,
                            monthTotal = monthTotal,
                            onToggle = { viewModel.toggleMonth(monthKey) }
                        )
                    }

                    // 月份下的记录
                    if (isMonthExpanded) {
                        items(monthRecords, key = { "record_${it.id}" }) { record ->
                            RecordItem(
                                record = record,
                                isSelected = uiState.selectedIds.contains(record.id),
                                isSelectionMode = uiState.isSelectionMode,
                                onClick = {
                                    if (uiState.isSelectionMode) {
                                        viewModel.toggleRecordSelection(record.id)
                                    } else {
                                        viewModel.startEditing(record)
                                    }
                                },
                                onLongClick = {
                                    if (!uiState.isSelectionMode) {
                                        viewModel.toggleSelectionMode()
                                        viewModel.toggleRecordSelection(record.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // 底部间距
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
