package com.example.quizletappandroidv1.customview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import org.tensorflow.lite.task.vision.detector.Detection
import kotlin.math.absoluteValue

class CustomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bitmap: Bitmap? = null
    private val boundingBoxPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val pointPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    private var detectedObjects: List<Detection> = emptyList()
    private val clickablePoints = mutableListOf<ClickablePoint>()

    // Set the image and detected objects
    fun setImageAndDetectedObjects(image: Bitmap, objects: List<Detection>) {
        this.bitmap = image
        this.detectedObjects = objects
        createClickablePoints(objects)
        invalidate()  // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the bitmap image first
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

// Vẽ các khung bao quanh vật thể được phát hiện
        detectedObjects.forEach { detectedObject ->

            Log.d(
                "DetectionResult",
                "Object: ${detectedObject.boundingBox}, Score: ${detectedObject.categories[0].score}"
            )

            detectedObject.boundingBox?.let { box ->
                canvas.drawRect(box, boundingBoxPaint)
            }
        }

        // Vẽ các điểm có thể nhấp
        clickablePoints.forEach { point ->
            canvas.drawCircle(point.x, point.y, 10f, pointPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y

            // Check if any ClickablePoint is clicked
            clickablePoints.find { it.isClicked(x, y) }?.let { point ->
                Log.d(
                    "CustomImageView",
                    "Clicked on object with ID: ${point.detectedObject}"
                )
                showDialog(context, point.detectedObject)
            }
        }
        return true
    }

    private fun showDialog(context: Context, detectedObject: Detection) {
        AlertDialog.Builder(context)
            .setTitle("Detected Object")
            .setMessage("Object ID: ${detectedObject}")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun createClickablePoints(objects: List<Detection>) {
        clickablePoints.clear()  // Clear previous points
        objects.forEach { detectedObject ->
            detectedObject.boundingBox?.let { box ->
                // Tạo một ClickablePoint tại trung tâm của bounding box
                val centerX = (box.left + box.right) / 2f
                val centerY = (box.top + box.bottom) / 2f
                clickablePoints.add(ClickablePoint(centerX, centerY, detectedObject))
            }
        }
    }

    data class ClickablePoint(
        val x: Float,
        val y: Float,
        val detectedObject: Detection
    ) {
        // Check if the point was clicked (with a margin of error)
        fun isClicked(touchX: Float, touchY: Float): Boolean {
            val radius = 50  // Sensitivity for detecting the click
            return (touchX - x).absoluteValue < radius && (touchY - y).absoluteValue < radius
        }
    }


}
