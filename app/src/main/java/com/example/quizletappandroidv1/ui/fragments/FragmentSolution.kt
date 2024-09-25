package com.example.quizletappandroidv1.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentSolutionBinding
import com.example.quizletappandroidv1.utils.ObjectDetectionHelper
import org.tensorflow.lite.task.vision.detector.Detection

class FragmentSolution : Fragment() {

    private lateinit var binding: FragmentSolutionBinding
    private lateinit var objectDetectionHelper: ObjectDetectionHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSolutionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo ObjectDetectionHelper
        objectDetectionHelper = ObjectDetectionHelper(requireContext())

        // Lấy ảnh từ resource
        val imageResource = arguments?.getInt("imageResource") ?: R.raw.ac101
        val image = BitmapFactory.decodeResource(resources, imageResource)
        val resizedImage = Bitmap.createScaledBitmap(image, 300, 300, true)

        // Phát hiện các vật thể trong ảnh
        val detectedObjects = objectDetectionHelper.detectObjects(resizedImage)

// Kiểm tra kết quả phát hiện đối tượng
        for (obj in detectedObjects) {
            Log.d("FragmentSolution", "Object: ${obj.categories[0].label}, Score: ${obj.categories[0].score}")
        }

// Hiển thị kết quả lên CustomImageView
        displayDetectedObjects(image, detectedObjects)
    }

    private fun displayDetectedObjects(image: Bitmap, detections: List<Detection>) {
        // Gửi ảnh và các vật thể đã phát hiện đến CustomImageView
        binding.customImageView.setImageAndDetectedObjects(image, detections)
    }
}
