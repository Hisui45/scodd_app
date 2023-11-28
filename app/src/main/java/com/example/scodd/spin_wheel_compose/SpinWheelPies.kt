package com.example.scodd.spin_wheel_compose

import android.graphics.Paint
import android.graphics.Typeface
import androidx.annotation.IntRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
internal fun SpinWheelPies(
    modifier: Modifier = Modifier,
    spinSize: Dp,
    pieCount: Int,
    pieColors: List<Color>,
    rotationDegree: Float,
    onClick: () -> Unit,
    titles: List<String>
) {
    val pieAngle = 360f / pieCount
    val startAngleOffset = 270
    val style = MaterialTheme.typography.labelSmall
    val customTypeface = Typeface.createFromAsset(
        LocalContext.current.assets,
        "londrinasolid_regular.ttf"
    )
    val updatedTextPaint = Paint()
    updatedTextPaint.textSize = 44.5f
    updatedTextPaint.color = Color.Black.toArgb()
    updatedTextPaint.typeface = customTypeface
    val fontMetrics = updatedTextPaint.fontMetrics
    val textHeight = fontMetrics.descent - fontMetrics.ascent

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(spinSize)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        ) {

            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = min(canvasWidth, canvasHeight) / 2f

            for (i in 0 until pieCount) {
                val startAngle = pieAngle * i + rotationDegree + startAngleOffset
                val nextColor = pieColors[(i + 1) % pieColors.size]
//                val nextColor = pieColors.getOrElse(i) { Color.LightGray }

                drawArc(
                    color = nextColor,
                    startAngle = startAngle,
                    sweepAngle = pieAngle,
                    useCenter = true,
                    size = Size(canvasWidth, canvasHeight)
                )

                // Calculate the position and size for the text
                val centerX = canvasWidth / 2f
                val centerY = canvasHeight / 2f
                val outerRadius = radius - 90
                // Calculate the position for the inner text using polar coordinates
                val innerRadius = outerRadius + 0 // Adjust textSize as needed

                val polarX = centerX + innerRadius * cos(Math.toRadians((startAngle + pieAngle / 2f).toDouble()).toFloat())
                val polarY = centerY + innerRadius * sin(Math.toRadians((startAngle + pieAngle / 2f).toDouble()).toFloat())

                // Convert polar coordinates to Cartesian coordinates
                val textX = polarX + (canvasWidth / 2f - polarX) * 0.5f // Adjust the ratio as needed
                val textY =
                    polarY + (((canvasHeight / 2f) - polarY) * 0.5f) + (textHeight / 2) // Adjust the ratio as needed

                // Calculate the rotation angle for the text
                val rotationAngle = startAngle + pieAngle / 2f

                val rotatedTextX = polarX + (canvasWidth / 2f - polarX) * 0.5f // Adjust the ratio as needed
                val rotatedTextY =
                    polarY + (((canvasHeight / 2f) - polarY) * 0.5f) + textHeight // Adjust the ratio as needed


                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.save()
                    canvas.nativeCanvas.rotate(rotationAngle, textX, textY)


                    //Use rotated text position if the index is higher than half
                    val useRotatedText = i > pieCount / 2
                    val textX = if (useRotatedText) rotatedTextX else textX
                    val textY = if (useRotatedText) rotatedTextY else textY

                    // Draw text into the canvas
                    canvas.nativeCanvas.drawText(
                        titles[i], // Replace with your actual text
                        textX,
                        textY,
                        updatedTextPaint // Use a Paint object for styling
                    )

                    canvas.nativeCanvas.restore()
                }
//                fun getNextColor(): String {
//                    val color = colors[i]
//                    currentIndex = (currentIndex + 1) % colors.size // Use modulo to cycle through the list
//                    return color
//                }
            }
        }
    }

}

