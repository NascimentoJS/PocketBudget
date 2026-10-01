package com.example.pocketbudget.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ============================================================
// CORES DO POCKETBUDGET
// ============================================================

private val BluePrimary = Color(0xFF1565C0)
private val BlueDark = Color(0xFF0D47A1)
private val BlueLight = Color(0xFFE3F2FD)

private val BackgroundLight = Color(0xFFF5F7FA)
private val SurfaceLight = Color(0xFFFFFFFF)

private val TextPrimary = Color(0xFF17202A)
private val TextSecondary = Color(0xFF667085)

private val IncomeGreen = Color(0xFF2E7D32)
private val ExpenseRed = Color(0xFFC62828)

// ============================================================
// TEMA CLARO
// ============================================================

private val LightColorScheme = lightColorScheme(

    primary = BluePrimary,

    onPrimary = Color.White,

    primaryContainer = BlueLight,

    onPrimaryContainer = BlueDark,

    secondary = BlueDark,

    onSecondary = Color.White,

    secondaryContainer = Color(0xFFE8EEF7),

    onSecondaryContainer = BlueDark,

    background = BackgroundLight,

    onBackground = TextPrimary,

    surface = SurfaceLight,

    onSurface = TextPrimary,

    surfaceVariant = Color(0xFFEEF1F5),

    onSurfaceVariant = TextSecondary,

    outline = Color(0xFFD0D5DD),

    outlineVariant = Color(0xFFE4E7EC),

    error = ExpenseRed,

    onError = Color.White,

    errorContainer = Color(0xFFFFE8E6),

    onErrorContainer = ExpenseRed
)

// ============================================================
// TEMA ESCURO
// ============================================================

private val DarkColorScheme = darkColorScheme(

    primary = Color(0xFF90CAF9),

    onPrimary = Color(0xFF003258),

    primaryContainer = Color(0xFF0D47A1),

    onPrimaryContainer = Color(0xFFD6EAFF),

    secondary = Color(0xFF90CAF9),

    onSecondary = Color(0xFF003258),

    secondaryContainer = Color(0xFF1C344D),

    onSecondaryContainer = Color(0xFFD6EAFF),

    background = Color(0xFF101418),

    onBackground = Color(0xFFE7EAF0),

    surface = Color(0xFF171B20),

    onSurface = Color(0xFFE7EAF0),

    surfaceVariant = Color(0xFF252B32),

    onSurfaceVariant = Color(0xFFB9C0CA),

    outline = Color(0xFF747C87),

    outlineVariant = Color(0xFF3A414A),

    error = Color(0xFFFFB4AB),

    onError = Color(0xFF690005),

    errorContainer = Color(0xFF93000A),

    onErrorContainer = Color(0xFFFFDAD6)
)

// ============================================================
// TEMA DO POCKETBUDGET
// ============================================================

@Composable
fun PocketBudgetTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme =
        if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}