package com.accountbook.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.accountbook.app.data.entity.AccountRecord
import com.accountbook.app.ui.theme.BlueDivider
import com.accountbook.app.ui.theme.BluePrimary
import com.accountbook.app.ui.theme.ErrorRed
import com.accountbook.app.ui.theme.TextSecondary
import com.accountbook.app.util.DateUtils
import java.util.Calendar

/**
 * 新增/编辑账单对话框
 *
 * 提供金额输入、备注输入和时间选择功能。
 * 时间选择使用滚轮式选择器，包含年月日时分秒。
 *
 * @param record 要编辑的记录，null 表示新增
 * @param onDismiss 关闭对话框回调
 * @param onConfirm 确认回调，返回金额、备注和时间戳
 */
@Composable
fun AddEditDialog(
    record: AccountRecord? = null,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, note: String, customTimestamp: Long?) -> Unit
) {
    var amountText by remember { mutableStateOf(formatAmountInput(record?.amount)) }
    var noteText by remember { mutableStateOf(record?.note ?: "") }
    var amountError by remember { mutableStateOf<String?>(null) }

    val calendar = remember { Calendar.getInstance().apply { record?.let { timeInMillis = it.createdAt } } }
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }
    var selectedDay by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }
    var selectedSecond by remember { mutableIntStateOf(calendar.get(Calendar.SECOND)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = if (record == null) "新增账单" else "编辑账单",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                // 金额输入框
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            amountText = value
                            amountError = null
                        }
                    },
                    label = { Text("金额 *") },
                    placeholder = { Text("请输入金额") },
                    isError = amountError != null,
                    supportingText = { amountError?.let { Text(it, color = ErrorRed) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        focusedLabelColor = BluePrimary,
                        cursorColor = BluePrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 备注输入框
                OutlinedTextField(
                    value = noteText,
                    onValueChange = {
                        if (it.length <= 200) noteText = it
                    },
                    label = { Text("备注（选填）") },
                    placeholder = { Text("添加备注信息") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        focusedLabelColor = BluePrimary,
                        cursorColor = BluePrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 时间滚轮选择器（年月日时分秒）
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    DialogWheelPicker("年", selectedYear, 2020..2030, { selectedYear = it })
                    DialogWheelPicker("月", selectedMonth, 1..12, { selectedMonth = it })
                    DialogWheelPicker("日", selectedDay, 1..31, { selectedDay = it })
                    DialogWheelPicker("时", selectedHour, 0..23, { selectedHour = it })
                    DialogWheelPicker("分", selectedMinute, 0..59, { selectedMinute = it })
                    DialogWheelPicker("秒", selectedSecond, 0..59, { selectedSecond = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    when {
                        amountText.isBlank() -> amountError = "请输入金额"
                        amount == null -> amountError = "请输入有效金额"
                        amount < 0 -> amountError = "金额不能为负数"
                        else -> {
                            val timestamp = DateUtils.toTimestamp(
                                selectedYear, selectedMonth, selectedDay,
                                selectedHour, selectedMinute, selectedSecond, 0
                            )
                            onConfirm(amount, noteText.trim(), timestamp)
                        }
                    }
                }
            ) {
                Text("保存", color = BluePrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = TextSecondary)
            }
        }
    )
}

/**
 * 对话框内滚轮选择器
 *
 * 使用 LazyColumn 实现可滚动的数字选择器，
 * 带有吸附效果和选中高亮。
 *
 * @param label 选择器标签（年/月/日/时/分/秒）
 * @param selectedValue 当前选中的值
 * @param range 可选值范围
 * @param onValueChange 值改变回调
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DialogWheelPicker(
    label: String,
    selectedValue: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    val items = range.toList()
    val initialIndex = items.indexOf(selectedValue).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val itemHeightPx = 50
    val density = LocalDensity.current
    val itemHeightDp = with(density) { itemHeightPx.toDp() }

    var selectedIndex by remember { mutableIntStateOf(initialIndex) }

    LaunchedEffect(selectedIndex) {
        val target = items.getOrNull(selectedIndex)
        if (target != null && target != selectedValue) {
            onValueChange(target)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (index != selectedIndex && index in items.indices) {
                    selectedIndex = index
                }
            }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(itemHeightDp * 3)
        ) {
            LazyColumn(
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(itemHeightDp)) }
                items(items.size) { index ->
                    val isSelected = index == selectedIndex
                    Text(
                        text = String.format("%02d", items[index]),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) BluePrimary else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .height(itemHeightDp)
                            .alpha(if (isSelected) 1f else 0.4f)
                    )
                }
                item { Spacer(modifier = Modifier.height(itemHeightDp)) }
            }

            Surface(
                modifier = Modifier
                    .height(itemHeightDp)
                    .fillMaxWidth()
                    .align(Alignment.Center),
                color = BlueDivider.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.small
            ) {}
        }
    }
}

/**
 * 格式化金额输入
 *
 * 将 Double 转换为字符串，整数不显示小数位。
 *
 * @param amount 金额
 * @return String 格式化后的金额字符串
 */
private fun formatAmountInput(amount: Double?): String {
    if (amount == null) return ""
    return if (amount == amount.toLong().toDouble()) {
        amount.toLong().toString()
    } else {
        amount.toString()
    }
}
