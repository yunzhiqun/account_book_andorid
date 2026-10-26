package com.accountbook.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * 应用主题定义
 *
 * 定义 Material3 浅色主题的颜色方案，使用蓝色主题。
 */

/** 浅色主题颜色方案 */
private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueLight,
    onPrimaryContainer = Color.White,
    secondary = BlueLight,
    onSecondary = Color.White,
    secondaryContainer = BlueDivider,
    onSecondaryContainer = BlueDark,
    background = WhiteBackground,
    onBackground = TextPrimary,
    surface = WhiteBackground,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    outline = BlueDivider,
    error = ErrorRed,
    onError = Color.White,
)

/**
 * 记账本主题 Composable
 *
 * 应用 Material3 主题，包括颜色方案和字体排版。
 *
 * @param content 主题内容
 */
@Composable
fun AccountBookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
