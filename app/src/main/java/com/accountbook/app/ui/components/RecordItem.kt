package com.accountbook.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.accountbook.app.data.entity.AccountRecord
import com.accountbook.app.ui.theme.BlueDark
import com.accountbook.app.ui.theme.BlueLight
import com.accountbook.app.ui.theme.BluePrimary
import com.accountbook.app.ui.theme.CardBackground
import com.accountbook.app.ui.theme.SelectedBg
import com.accountbook.app.ui.theme.TextPrimary
import com.accountbook.app.ui.theme.TextSecondary
import com.accountbook.app.util.AmountUtils
import com.accountbook.app.util.DateUtils

/**
 * 账单记录项组件
 *
 * 显示单条账单记录，包括备注、时间和金额。
 * 支持单击和长按操作，长按进入多选模式。
 * 在多选模式下显示复选框。
 *
 * @param record 账单记录数据
 * @param isSelected 是否被选中
 * @param isSelectionMode 是否处于多选模式
 * @param onClick 单击回调（选中/取消选中或打开编辑）
 * @param onLongClick 长按回调（进入多选模式）
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordItem(
    record: AccountRecord,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val bgColor = if (isSelected) SelectedBg else CardBackground

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        color = bgColor,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 多选模式下显示复选框
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = BluePrimary,
                        uncheckedColor = TextSecondary
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // 左侧：备注和时间
            Column(modifier = Modifier.weight(1f)) {
                if (record.note.isNotEmpty()) {
                    Text(
                        text = record.note,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = DateUtils.formatDateTime(record.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 右侧：金额
            Text(
                text = "-¥${AmountUtils.formatDisplayAmount(record.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = BlueDark,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
