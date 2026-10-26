package com.accountbook.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.accountbook.app.ui.navigation.NavGraph
import com.accountbook.app.ui.theme.AccountBookTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 记账本应用主界面
 *
 * 这是应用的唯一 Activity，使用 Jetpack Compose 作为 UI 框架。
 * 通过 @AndroidEntryPoint 注解启用 Hilt 依赖注入。
 *
 * 主要功能：
 * - 启用边到边显示（Edge-to-Edge）
 * - 设置 Compose 内容视图
 * - 应用自定义主题
 * - 加载导航图
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Activity 创建时调用
     *
     * @param savedInstanceState 保存的实例状态，首次创建时为 null
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 启用边到边显示，让内容延伸到系统栏下方
        enableEdgeToEdge()
        // 设置 Compose 内容视图
        setContent {
            // 应用自定义主题
            AccountBookTheme {
                // 加载导航图，管理页面跳转
                NavGraph()
            }
        }
    }
}
