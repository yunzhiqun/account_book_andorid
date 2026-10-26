package com.accountbook.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.accountbook.app.ui.screens.HomeScreen
import com.accountbook.app.ui.screens.QueryResultsScreen
import com.accountbook.app.ui.screens.SearchScreen

/**
 * 应用导航图
 *
 * 定义应用的页面导航结构，使用 Compose Navigation 管理页面跳转。
 * 包含三个主要页面：
 * - home：主页，显示账单列表
 * - search：查询页，选择时间范围
 * - query_results：查询结果页，显示筛选后的账单
 */
@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        // 主页
        composable("home") {
            HomeScreen(
                onNavigateToSearch = { navController.navigate("search") }
            )
        }
        // 查询页
        composable("search") {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onQueryComplete = { startTime, endTime ->
                    navController.navigate("query_results/$startTime/$endTime")
                }
            )
        }
        // 查询结果页
        composable(
            route = "query_results/{startTime}/{endTime}",
            arguments = listOf(
                navArgument("startTime") { type = NavType.LongType },
                navArgument("endTime") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val startTime = backStackEntry.arguments?.getLong("startTime") ?: 0L
            val endTime = backStackEntry.arguments?.getLong("endTime") ?: 0L
            QueryResultsScreen(
                startTime = startTime,
                endTime = endTime,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
