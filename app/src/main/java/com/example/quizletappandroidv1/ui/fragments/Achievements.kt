package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.adapter.AchievementAdapter
import com.example.quizletappandroidv1.databinding.FragmentAchievementsBinding
import com.example.quizletappandroidv1.models.TaskData
import com.example.quizletappandroidv1.viewmodel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class Achievements : Fragment() {
    private lateinit var binding: FragmentAchievementsBinding
    private lateinit var adapterAchievementStudySet: AchievementAdapter
    private lateinit var adapterAchievementStreak: AchievementAdapter

    val userId = MyApplication.userId
    val timeDetect = System.currentTimeMillis()

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAchievementsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userId != null) {
            homeViewModel.getDataAchievement(userId, timeDetect)
        }

        binding.btcCloseAchievement.setOnClickListener {
            requireActivity().finish()
        }

        adapterAchievementStreak =
            AchievementAdapter(requireContext(), 2, object : AchievementAdapter.IAchievementClick {
                override fun handleClickAchievement(itemAchievement: TaskData) {
                    val action =
                        AchievementsDirections.actionAchievementsToItemAchievementBottomSheet(
                            itemAchievement
                        )
                    findNavController().navigate(action)
                }
            })

        adapterAchievementStudySet =
            AchievementAdapter(requireContext(), 2, object : AchievementAdapter.IAchievementClick {
                override fun handleClickAchievement(itemAchievement: TaskData) {
                    val action =
                        AchievementsDirections.actionAchievementsToItemAchievementBottomSheet(
                            itemAchievement
                        )
                    findNavController().navigate(action)
                }
            })


        homeViewModel.dataAchievement.observe(viewLifecycleOwner) { result ->

            result.onSuccess {
                binding.txtCurrentStreak.text =
                    "Current streak : ${it.streak.currentStreak}-days streak"
                val listStreak = it.achievement.taskList.filter { it.type == "Streak" }
                val listStudy = it.achievement.taskList.filter { it.type == "Study" }
                binding.rvAchievementStreak.layoutManager =
                    GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                binding.rvAchievementStudy.adapter = adapterAchievementStudySet
                adapterAchievementStreak.updateData(listStreak)
                binding.rvAchievementStudy.layoutManager =
                    GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                binding.rvAchievementStreak.adapter = adapterAchievementStreak
                adapterAchievementStudySet.updateData(listStudy)
            }.onFailure {
                Log.d("ErrorAchievements", it.message.toString())
            }
        }

        binding.btnViewMoreLessStudy.setOnClickListener {
            adapterAchievementStudySet.setIsExpaned()
            val buttonText = if (adapterAchievementStudySet.isExpanded) "View Less" else "View More"
            binding.btnViewMoreLessStudy.text = buttonText
        }
        binding.btnViewMoreLessStreak.setOnClickListener {
            adapterAchievementStreak.setIsExpandStreak()
            val buttonText =
                if (adapterAchievementStreak.isExpandedStreak) "View Less" else "View More"
            binding.btnViewMoreLessStreak.text = buttonText
        }


        // Lấy ngày hiện tại
        val currentDate = LocalDate.now()

        // Lấy tháng hiện tại
        val currentMonth = YearMonth.from(currentDate)

        // Lấy ngày đầu tiên của tháng
        val firstDayOfMonth = currentMonth.atDay(1)

        // Lấy ngày cuối cùng của tháng
        val lastDayOfMonth = currentMonth.atEndOfMonth()
        // Định dạng ngày và tháng
        val firstDayString = formatDayMonth(firstDayOfMonth)
        val lastDayString = formatDayMonth(lastDayOfMonth)
        val result = "$firstDayString - $lastDayString"

        binding.txtDateFrom.text = result


        // Tạo danh sách ngày từ ngày đầu tiên đến ngày cuối cùng của tháng
        val daysInMonth = mutableListOf<LocalDate>()
        var currentDay = firstDayOfMonth

        while (!currentDay.isAfter(lastDayOfMonth)) {
            daysInMonth.add(currentDay)
            currentDay = currentDay.plusDays(1)
        }

    }

    fun formatDayMonth(date: LocalDate): String {
        val dayOfMonth = date.dayOfMonth
        val month = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

        return "$dayOfMonth $month"
    }

}