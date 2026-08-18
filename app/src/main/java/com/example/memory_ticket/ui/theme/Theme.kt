package com.example.memory_ticket.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MemoryTicketColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueLight,
    tertiary = BlueDark,

    background = BackgroundLight,
    surface = White,

    onPrimary = White,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun Memory_TicketTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MemoryTicketColorScheme,
        content = content
    )
}