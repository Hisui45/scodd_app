package com.example.scodd.spin_wheel_compose

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.scodd.ui.mode.calculateAngle
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun SpinWheelContent(
    modifier: Modifier = Modifier,
    spinSize: Dp,
    pieCount: Int,
    rotationDegree: Float,
    content: BoxScope.(pieIndex: Int) -> String
) {
    val pieAngle = 360f / pieCount
    val startOffset = 180
    val radius = (spinSize.value / 2)
    val pieRadius = getPieRadius(pieCount, radius)
    val frequencies = List(pieCount) { pieAngle.toDouble() }
    Box(
        modifier = modifier
            .size(spinSize),
        contentAlignment = Alignment.Center
    ){
        for(pieIndex in 0 until pieCount){
            val startAngle = pieAngle * pieIndex + startOffset  + pieAngle / 2
            val offsetX = -(pieRadius * sin(Math.toRadians(startAngle.toDouble()))).toFloat()
            val offsetY = (pieRadius * cos(Math.toRadians(startAngle.toDouble()))).toFloat()
            Box(
                modifier = Modifier
                    .size(getPieBoxSize(pieCount, spinSize))
                    .offset(x = Dp(offsetX), y = Dp(offsetY)),
                contentAlignment = Alignment.Center
            ) {
//                content(pieIndex)
                Text(content(pieIndex),overflow = TextOverflow.Ellipsis, modifier = Modifier.rotate(
                calculateAngle(pieIndex, frequencies, pieAngle.toDouble()).toFloat()
            ))
            }
        }
    }
}

private fun getPieBoxSize(pieCount: Int, spinSize: Dp): Dp{
    return when(pieCount){
        2 -> spinSize / 3
        3 -> spinSize / 4
        4 -> spinSize / 4
        5 -> spinSize / 5
        6 -> spinSize / 5
        7 -> spinSize / 6
        8 -> spinSize / 6
        else -> spinSize / 2
    }
}


fun getPieRadius(pieCount: Int, radius: Float): Float{
    return when(pieCount){
        2 -> radius / 2f
        3 -> radius / 1.8f
        4 -> radius / 1.8f
        5 -> radius / 1.6f
        6 -> radius / 1.6f
        7 -> radius / 1.4f
        8 -> radius / 1.4f
        else -> radius / 2f
    }
}