package com.example.scodd.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(

    primary = Marigold40,
    onPrimary = Black40,
    primaryContainer = LightMarigold40,
    onPrimaryContainer = YellowBrown40,


    //SnackBar action colors
    inversePrimary = LightMarigold40,

    secondary = RoosterRed40, //9f1d35,91012d, a3133f, a30026
    onSecondary = White40,
    secondaryContainer = RoosterRed40,
    onSecondaryContainer = White40,


    tertiary = Red40,
    tertiaryContainer = Pink40,
    onTertiaryContainer = RedBrown40,

//    onSurfaceVariant = Marigold40,
    surface = White40,
    surfaceTint = Green40,

//    inverseSurface = LightMarigold40,

    outline = LightGray40,
    outlineVariant = White40

//    primary = Burgundy40,
//    onPrimary = Brown40,
//    primaryContainer = Burgundy40,
//    onPrimaryContainer = White40,
////    inversePrimary = ,
//    secondary = Marigold40,
//    onSecondary = YellowBrown40,
//    secondaryContainer = Marigold40,
//    onSecondaryContainer = Red40,
//    tertiary = Pink40,
//    onTertiary = YellowBrown40,
//    onTertiaryContainer = Black40,
////    background = White40,
//    onBackground = Gray40,
//    surface = White40,
//    onSurface = White40,  // Icons & Navigation Bar Icons
//    surfaceVariant = Cream40,
//    onSurfaceVariant = White40,  //Navigation Bar Icons
////    surfaceTint = ,
////    inverseSurface = ,
//    inverseOnSurface = Black40,
//
//    outline = Black40,
//    outlineVariant = White40





    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ScoddTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
            if (darkTheme) DarkColorScheme else LightColorScheme
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            (view.context as Activity).window.statusBarColor = colorScheme.surface.toArgb()
//            WindowCompat.getInsetsController((view.context as Activity).window, view).isAppearanceLightStatusBars =
//                !darkTheme
//        }
//    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}