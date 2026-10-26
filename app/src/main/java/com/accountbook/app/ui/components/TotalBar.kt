package com.accountbook.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.accountbook.app.ui.theme.BlueDark
import com.accountbook.app.ui.theme.BluePrimary
import com.accountbook.app.ui.theme.BlueTotalBg
import com.accountbook.app.ui.theme.TextSecondary
import com.accountbook.app.util.AmountUtils

/**
 * 总计栏组件
 *
 * 显示在页面底部，展示当前筛选条件下的记录总数和总金额。
 * 提供复制按钮，可将总计信息复制到剪贴板。
 *
 * @param totalAmount 总金额
 * @param recordCount 记录数量
 * @param onCopyClick 复制按钮点击回调
 */
@Composable
fun TotalBar(
    totalAmount: Double,
    recordCount: Int,
    onCopyClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BlueTotalBg,
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "总计 ${recordCount} 笔",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "¥${AmountUtils.formatDisplayAmount(totalAmount)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = BlueDark,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onCopyClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "复制总计",
                        tint = BluePrimary
                    )
                    Text(
                        text = "复制",
                        style = MaterialTheme.typography.bodySmall,
                        color = BluePrimary
                    )
                }
            }
        }
    }
}
