package com.accountbook.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.accountbook.app.ui.components.AddEditDialog
import com.accountbook.app.ui.components.CopyOptionDialog
import com.accountbook.app.ui.components.TotalBar
import com.accountbook.app.ui.theme.BluePrimary
import com.accountbook.app.util.CopyUtils
import com.accountbook.app.viewmodel.AccountViewModel

/**
 * 查询结果界面
 *
 * 显示按时间范围筛选后的账单记录，格式与主页一致。
 * 支持编辑、删除、复制功能，但不提供新增按钮。
 *
 * @param startTime 查询开始时间戳
 * @param endTime 查询结束时间戳
 * @param onNavigateBack 返回上一页回调
 * @param viewModel 账户视图模型
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryResultsScreen(
    startTime: Long,
    endTime: Long,
    onNavigateBack: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val records by viewModel.currentRecords.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()
    val editingRecord by viewModel.editingRecord.collectAsState()
    val showDeleteConfirm by viewModel.showDeleteConfirm.collectAsState()
    val showCopyDialog by viewModel.showCopyDialog.collectAsState()
    val simpleCopyText by viewModel.simpleCopyText.collectAsState()
    val fullCopyText by viewModel.fullCopyText.collectAsState()
    val snackbarMsg by viewModel.showSnackbar.collectAsState()
    val context = LocalContext.current

    // 加载查询结果
    LaunchedEffect(startTime, endTime) {
        viewModel.queryByDateRange(startTime, endTime)
    }

    // 显示 Snackbar 消息
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isSelectionMode) "已选 ${uiState.selectedIds.size} 项" else "查询结果",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (!uiState.isSelectionMode) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                "返回",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary),
                actions = {
                    if (uiState.isSelectionMode) {
                        IconButton(onClick = { viewModel.selectAllVisible() }) {
                            Icon(Icons.Default.SelectAll, "全选", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.showDeleteConfirmDialog() }) {
                            Icon(Icons.Default.Delete, "删除", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(Icons.Default.Close, "取消", tint = Color.White)
                        }
                    }
                }
            )
        },
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
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BluePrimary
                )
            } else if (records.isEmpty()) {
                EmptyState()
            } else {
                RecordList(
                    records = records,
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }

    // 编辑对话框
    editingRecord?.let { record ->
        com.accountbook.app.ui.components.AddEditDialog(
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
            text = { Text("确定要删除选中的 ${uiState.selectedIds.size} 条记录吗？") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        viewModel.deleteBatch(uiState.selectedIds.toList())
                    }
                ) {
                    Text("删除", color = BluePrimary)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.dismissDeleteConfirm() }
                ) {
                    Text("取消")
                }
            }
        )
    }
}
