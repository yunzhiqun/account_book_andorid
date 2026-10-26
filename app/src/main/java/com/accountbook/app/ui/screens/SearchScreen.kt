package com.accountbook.app.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.accountbook.app.ui.theme.BlueDark
import com.accountbook.app.ui.theme.BlueDivider
import com.accountbook.app.ui.theme.BluePrimary
import com.accountbook.app.ui.theme.BlueSurface
import com.accountbook.app.ui.theme.TextSecondary
import com.accountbook.app.util.DateUtils
import com.accountbook.app.viewmodel.AccountViewModel
import java.util.Calendar

/**
 * 查询界面
 *
 * 提供时间范围选择功能，用户可以选择开始时间和结束时间进行账单查询。
 * 使用滚轮式选择器选择年月日时分秒。
 *
 * 默认范围：上个月今天 00:00:00 到今天 23:59:59
 *
 * @param onNavigateBack 返回上一页回调
 * @param onQueryComplete 查询完成回调，返回开始和结束时间戳
 * @param viewModel 账户视图模型
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onQueryComplete: (Long, Long) -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH) + 1
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    // 默认开始时间：上个月今天
    calendar.add(Calendar.MONTH, -1)
    val defaultStartYear = calendar.get(Calendar.YEAR)
    val defaultStartMonth = calendar.get(Calendar.MONTH) + 1
    val defaultStartDay = calendar.get(Calendar.DAY_OF_MONTH)

    // 开始时间状态
    var startYear by remember { mutableIntStateOf(defaultStartYear) }
    var startMonth by remember { mutableIntStateOf(defaultStartMonth) }
    var startDay by remember { mutableIntStateOf(defaultStartDay) }
    var startHour by remember { mutableIntStateOf(0) }
    var startMinute by remember { mutableIntStateOf(0) }
    var startSecond by remember { mutableIntStateOf(0) }

    // 结束时间状态
    var endYear by remember { mutableIntStateOf(currentYear) }
    var endMonth by remember { mutableIntStateOf(currentMonth) }
    var endDay by remember { mutableIntStateOf(currentDay) }
    var endHour by remember { mutableIntStateOf(23) }
    var endMinute by remember { mutableIntStateOf(59) }
    var endSecond by remember { mutableIntStateOf(59) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "查询账单",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "返回",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 开始时间卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlueSurface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "开始时间",
                        style = MaterialTheme.typography.titleMedium,
                        color = BlueDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        WheelPicker("年", startYear, 2020..2030, { startYear = it })
                        WheelPicker("月", startMonth, 1..12, { startMonth = it })
                        WheelPicker("日", startDay, 1..31, { startDay = it })
                        WheelPicker("时", startHour, 0..23, { startHour = it })
                        WheelPicker("分", startMinute, 0..59, { startMinute = it })
                        WheelPicker("秒", startSecond, 0..59, { startSecond = it })
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 结束时间卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlueSurface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "结束时间",
                        style = MaterialTheme.typography.titleMedium,
                        color = BlueDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        WheelPicker("年", endYear, 2020..2030, { endYear = it })
                        WheelPicker("月", endMonth, 1..12, { endMonth = it })
                        WheelPicker("日", endDay, 1..31, { endDay = it })
                        WheelPicker("时", endHour, 0..23, { endHour = it })
                        WheelPicker("分", endMinute, 0..59, { endMinute = it })
                        WheelPicker("秒", endSecond, 0..59, { endSecond = it })
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 查询按钮
            Button(
                onClick = {
                    val startTime = DateUtils.toTimestamp(
                        startYear, startMonth, startDay, startHour, startMinute, startSecond, 0
                    )
                    val endTime = DateUtils.toTimestamp(
                        endYear, endMonth, endDay, endHour, endMinute, endSecond, 999
                    )
                    onQueryComplete(startTime, endTime)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("查询", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 重置按钮
            Button(
                onClick = {
                    startYear = defaultStartYear; startMonth = defaultStartMonth; startDay = defaultStartDay
                    startHour = 0; startMinute = 0; startSecond = 0
                    endYear = currentYear; endMonth = currentMonth; endDay = currentDay
                    endHour = 23; endMinute = 59; endSecond = 59
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueDivider),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("重置为默认范围", color = BlueDark, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

/**
 * 滚轮选择器组件
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
fun WheelPicker(
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
