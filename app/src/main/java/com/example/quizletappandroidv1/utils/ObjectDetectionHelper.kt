package com.example.quizletappandroidv1.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions

class ObjectDetectionHelper(context: Context) {

    private val objectDetector: ObjectDetector

    init {
        // Khởi tạo ObjectDetector
        val options = ObjectDetectorOptions.builder()
            .setMaxResults(5)  // Số lượng vật thể tối đa cần phát hiện
            .setScoreThreshold(0.5f)  // Ngưỡng điểm số của vật thể
            .build()

        val modelFile = "ssd_mobilenet_v1.tflite"  // Đảm bảo tên file chính xác

        objectDetector = ObjectDetector.createFromFileAndOptions(
            context, modelFile, options
        )
    }

    fun detectObjects(bitmap: Bitmap): List<Detection> {
        // Chuyển đổi ảnh Bitmap thành TensorImage
        val tensorImage = TensorImage.fromBitmap(bitmap)

        // Thực hiện phát hiện đối tượng
        val results = objectDetector.detect(tensorImage)
        Log.d("ObjectDetectionHelper", Gson().toJson(results))

        // Ghi log các kết quả phát hiện để kiểm tra
        for (result in results) {
            Log.d("ObjectDetectionHelper", "Object detected: ${result.categories[0].label}, Score: ${result.categories[0].score}")
        }

        return results
    }
}