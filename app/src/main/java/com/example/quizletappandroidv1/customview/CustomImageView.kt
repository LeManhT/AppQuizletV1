package com.example.quizletappandroidv1.customview

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.absoluteValue

class CustomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val points = mutableListOf<ClickablePoint>()

    // Function to set image and points
    fun setImagePoints(image: Bitmap, points: List<ClickablePoint>) {
        this.points.clear()
        this.points.addAll(points)
        invalidate()  // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the points on the image
        points.forEach { point ->
            canvas?.drawCircle(point.x, point.y, 20f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            points.find { it.isClicked(x, y) }?.let { point ->
                // Show the dialog with the point's information
                showDialog(context, point)
            }
        }
        return true
    }

    private fun showDialog(context: Context, point: ClickablePoint) {
        AlertDialog.Builder(context)
            .setTitle(point.title)
            .setMessage(point.description)
            .setPositiveButton("OK", null)
            .show()
    }
}

data class ClickablePoint(
    val x: Float,
    val y: Float,
    val title: String,
    val description: String
) {
    // Check if the point was clicked (with a margin of error)
    fun isClicked(touchX: Float, touchY: Float): Boolean {
        val radius = 50  // Adjust for sensitivity
        return (touchX - x).absoluteValue < radius && (touchY - y).absoluteValue < radius
    }
}
