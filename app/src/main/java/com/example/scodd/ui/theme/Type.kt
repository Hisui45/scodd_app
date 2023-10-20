package com.example.scodd.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.scodd.R


val londrinaSolid = FontFamily(
    Font(R.font.londrinasolid_light, FontWeight.Light),
    Font(R.font.londrinasolid_regular, FontWeight.Normal),
    Font(R.font.londrinasolid_black, FontWeight.Black),
    Font(R.font.londrinasolid_thin, FontWeight.Thin)
)

val londrinaShadow = FontFamily(
    Font(R.font.londrinashadow_regular, FontWeight.Normal),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    labelLarge = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp

    ),

    labelMedium = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),

    labelSmall = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.28.sp
    ),

    titleLarge = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    titleMedium = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    titleSmall = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    headlineSmall = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.6.sp
    ),

    displayLarge = TextStyle(
        fontFamily = londrinaShadow,
        fontWeight = FontWeight.Normal,
        color = Marigold40,
        fontSize = 100.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    displayMedium = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        color = Burgundy40,
        fontSize = 100.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    displaySmall = TextStyle(
        fontFamily = londrinaSolid,
        fontWeight = FontWeight.Normal,
        fontSize = 50.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )







    /* Other default text styles to override
    ,
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)