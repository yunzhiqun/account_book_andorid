package com.accountbook.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.accountbook.app.ui.theme.BlueDark
import com.accountbook.app.ui.theme.BlueDivider
import com.accountbook.app.ui.theme.BluePrimary
import com.accountbook.app.ui.theme.TextSecondary
import com.accountbook.app.util.AmountUtils

/**
 * 分组标题组件
 *
 * 提供年份和月份分组的可折叠标题，显示记录数量和金额总计。
 * 点击标题可展开或折叠该分组下的账单记录。
 */

/**
 * 年份分组标题
 *
 * 显示年份、记录数量和年度总计金额。
 * 使用蓝色主色背景，白色文字。
 *
 * @param year 年份
 * @param isExpanded 是否展开
 * @param recordCount 该年份的记录数量
 * @param yearTotal 该年份的总计金额
 * @param onToggle 点击切换展开/折叠状态
 */
@Composable
fun YearGroupHeader(
    year: Int,
    isExpanded: Boolean,
    recordCount: Int,
    yearTotal: Double,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onToggle),
        color = BluePrimary,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${year}年",
                    style = MaterialTheme.typography.titleLarge,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "  共${recordCount}笔",
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¥${AmountUtils.formatDisplayAmount(yearTotal)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "收起" else "展开",
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}

/**
 * 月份分组标题
 *
 * 显示月份、记录数量和月度总计金额。
 * 使用浅蓝色背景，深蓝色文字。
 *
 * @param month 月份（1-12）
 * @param isExpanded 是否展开
 * @param recordCount 该月份的记录数量
 * @param monthTotal 该月份的总计金额
 * @param onToggle 点击切换展开/折叠状态
 */
@Composable
fun MonthGroupHeader(
    month: Int,
    isExpanded: Boolean,
    recordCount: Int,
    monthTotal: Double,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .clickable(onClick = onToggle),
        color = BlueDivider,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${month}月",
                    style = MaterialTheme.typography.titleMedium,
                    color = BlueDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "  ${recordCount}笔",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¥${AmountUtils.formatDisplayAmount(monthTotal)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BlueDark,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "收起" else "展开",
                    tint = BlueDark,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
