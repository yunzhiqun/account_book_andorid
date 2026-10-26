package com.accountbook.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.accountbook.app.ui.theme.BluePrimary
import com.accountbook.app.ui.theme.TextSecondary

/**
 * 复制选项对话框
 *
 * 提供两种复制方式选择：
 * - 简要复制：只复制金额等式（如 1+2+3=6）
 * - 全部复制：复制每条记录详情 + 金额等式
 *
 * @param simpleText 简要格式复制文本
 * @param fullText 完整格式复制文本
 * @param onDismiss 关闭对话框回调
 * @param onCopySimple 简要复制回调
 * @param onCopyFull 全部复制回调
 */
@Composable
fun CopyOptionDialog(
    simpleText: String,
    fullText: String,
    onDismiss: () -> Unit,
    onCopySimple: () -> Unit,
    onCopyFull: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "选择复制方式",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = "简要复制：只复制金额等式（如 1+2+3=6）\n全部复制：复制每条记录详情 + 金额等式",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onCopyFull) {
                Text("全部复制", color = BluePrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onCopySimple) {
                Text("简要复制", color = TextSecondary)
            }
        }
    )
}
