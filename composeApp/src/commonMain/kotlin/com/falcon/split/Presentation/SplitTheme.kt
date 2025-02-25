package com.falcon.split.Presentation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Light theme colors
val ThemeGrey = Color(0xFFfff6f6)
val ThemePurple = Color(0xFF520c61)
val SuccessGreen = Color(0xFF22C55E)
val ErrorRed = Color(0xFFEF4444)
val LightBackground = Color(0xFFF5F3F7)
val LightSurface = Color.White
val LightTextPrimary = Color(0xFF1E293B)
val LightTextSecondary = Color(0xFF64748B)

// Dark theme colors
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkTextPrimary = Color(0xFFE0E0E0)
val DarkTextSecondary = Color(0xFFAEAEAE)
val DarkSuccessGreen = Color(0xFF4ADE80)
val DarkErrorRed = Color(0xFFFF6B6B)
val DarkPrimary = Color(0xFF9D4EDD)
val DarkSecondary = Color(0xFF03DAC6)

// App color scheme
class SplitColors(
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val success: Color,
    val error: Color,
    val cardBackground: Color,
    val primary: Color,
    val secondary: Color
)

// Provide app colors based on theme
val LocalSplitColors = staticCompositionLocalOf {
    SplitColors(
        backgroundPrimary = LightBackground,
        backgroundSecondary = LightSurface,
        textPrimary = LightTextPrimary,
        textSecondary = LightTextSecondary,
        success = SuccessGreen,
        error = ErrorRed,
        cardBackground = Color.White,
        primary = ThemePurple,
        secondary = Color(0xFF03DAC5)
    )
}

// Get the app typography with the right colors based on theme
@Composable
fun getAppTypography(isDarkTheme: Boolean = isSystemInDarkTheme()): Typography {
    val splitColors = LocalSplitColors.current

    return Typography(
        bodyLarge = TextStyle(
            fontFamily = AppFontFamily.nunitoFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
            color = splitColors.textPrimary
        ),
        bodyMedium = TextStyle(
            fontFamily = AppFontFamily.nunitoFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
            color = splitColors.textPrimary
        ),
        titleLarge = TextStyle(
            fontFamily = AppFontFamily.nunitoFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.1.sp,
            color = splitColors.textPrimary
        ),
        titleSmall = TextStyle(
            fontFamily = AppFontFamily.nunitoFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            lineHeight = 25.sp,
            letterSpacing = 0.1.sp,
            color = splitColors.textSecondary
        ),
        titleMedium = TextStyle(
            fontFamily = AppFontFamily.nunitoFamily(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.1.sp,
            color = splitColors.textSecondary
        )
    )
}

// Provide app colors based on current theme
@Composable
fun getSplitColors(darkTheme: Boolean = isSystemInDarkTheme()): SplitColors {
    return if (darkTheme) {
        SplitColors(
            backgroundPrimary = DarkBackground,
            backgroundSecondary = DarkSurface,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            success = DarkSuccessGreen,
            error = DarkErrorRed,
            cardBackground = Color(0xFF2C2C2C),
            primary = DarkPrimary,
            secondary = DarkSecondary
        )
    } else {
        SplitColors(
            backgroundPrimary = LightBackground,
            backgroundSecondary = LightSurface,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            success = SuccessGreen,
            error = ErrorRed,
            cardBackground = Color.White,
            primary = ThemePurple,
            secondary = Color(0xFF03DAC5)
        )
    }
}

@Composable
fun SplitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    modifier: Modifier = Modifier,
    onThemeUpdated: () -> Unit,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = DarkPrimary,
            secondary = DarkSecondary,
            tertiary = Color(0xFF673AB7),
            background = DarkBackground,
            surface = DarkSurface,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = DarkTextPrimary,
            onSurface = DarkTextPrimary
        )
    } else {
        lightColorScheme(
            primary = ThemePurple,
            secondary = Color(0xFF03DAC5),
            tertiary = Color(0xFF3700B3),
            background = LightBackground,
            surface = LightSurface,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = LightTextPrimary,
            onSurface = LightTextPrimary
        )
    }

    val splitColors = getSplitColors(darkTheme)

    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )

    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    androidx.compose.runtime.CompositionLocalProvider(
        LocalSplitColors provides splitColors
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

@Composable
fun ThemeSwitcher(
    darkTheme: Boolean = false,
    size: Dp = 150.dp,
    iconSize: Dp = size / 3,
    padding: Dp = 10.dp,
    borderWidth: Dp = 1.dp,
    parentShape: Shape = CircleShape,
    toggleShape: Shape = CircleShape,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300),
    onClick: () -> Unit
) {
    val offset by animateDpAsState(
        targetValue = if (darkTheme) 0.dp else size,
        animationSpec = animationSpec
    )
    val colors = LocalSplitColors.current

    Box(modifier = Modifier
        .width(size * 2)
        .height(size)
        .clip(shape = parentShape)
        .clickable { onClick() }
        .background(if (darkTheme) Color(0xFF2C2C2C) else MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = offset)
                .padding(all = padding)
                .clip(shape = toggleShape)
                .background(colors.primary)
        ) {}
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        width = borderWidth,
                        color = colors.primary
                    ),
                    shape = parentShape
                )
        ) {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌙",
                    fontSize = 20.sp
                )
            }
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☀️",
                    fontSize = 20.sp
                )
            }
        }
    }
}