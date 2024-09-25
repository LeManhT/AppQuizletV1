package com.example.quizletappandroidv1.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentItemAchievementBottomSheetBinding
import com.example.quizletappandroidv1.models.TaskData
import com.example.quizletappandroidv1.utils.Helper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

import androidx.navigation.fragment.navArgs

class ItemAchievementBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentItemAchievementBottomSheetBinding
    private var progressText: String = ""
    private lateinit var sharedPreferences: SharedPreferences
    private var currentAchieveStreak: Int = 0

    // Nhận dữ liệu từ SafeArgs
    private val args: ItemAchievementBottomSheetArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemAchievementBottomSheetBinding.inflate(inflater, container, false)

        sharedPreferences = context?.getSharedPreferences("currentStreak", Context.MODE_PRIVATE)!!

        currentAchieveStreak = sharedPreferences.getInt("countStreak", 0)

        // Nhận dữ liệu từ SafeArgs
        val taskData = args.taskData

        if (taskData != null) {
            binding.txtNameAchievement.text = taskData.taskName
            binding.txtAchievementDesc.text = taskData.description
            binding.txtPoints.text = taskData.score.toString()

            val imageName = "ac${taskData.id}"
            val imageResourceId = context?.resources?.getIdentifier(
                imageName,
                "raw",
                requireContext().packageName
            )

            if (taskData.status != 2) {
                val originalBitmap: Bitmap? = imageResourceId?.let {
                    BitmapFactory.decodeResource(context?.resources, it)
                }
                val grayscaleBitmap = originalBitmap?.let { Helper.toGrayscale(it) }
                binding.imgAchievement.setImageBitmap(grayscaleBitmap)
            } else {
                imageResourceId?.let { binding.imgAchievement.setImageResource(it) }
            }

            Log.d("testTask", "${taskData.progress} ti ${Gson().toJson(taskData)}")

            if (taskData.type == "Streak" && taskData.condition > 1) {
                progressText = "${taskData.progress} / ${taskData.condition}"
                val progress = (taskData.progress.toDouble() / taskData.condition.toDouble()) * 100
                binding.customProgressBar.setProgress(progress.toInt(), progressText)
            } else if (taskData.type == "Study") {
                if (taskData.condition <= 1) {
                    if (taskData.status == 2) {
                        progressText = resources.getString(R.string.awarded)
                        binding.customProgressBar.setProgress(100, progressText)
                    } else {
                        progressText = resources.getString(R.string.uncompleted)
                        binding.customProgressBar.setProgress(0, progressText)
                    }
                } else {
                    progressText = "${taskData.progress} / ${taskData.condition}"
                    val progress = (taskData.progress.toDouble() / taskData.condition.toDouble()) * 100
                    binding.customProgressBar.setProgress(progress.toInt(), progressText)
                }
            }
        }

        return binding.root
    }

    companion object {
        const val TAG = "ItemAchievementBottomSheet"
    }
}
