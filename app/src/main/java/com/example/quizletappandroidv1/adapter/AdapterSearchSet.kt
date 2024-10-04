package com.example.quizletappandroidv1.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.quizletappandroidv1.ui.fragments.FragmentAllResult
import com.example.quizletappandroidv1.ui.fragments.FragmentSearchSetResult
import com.example.quizletappandroidv1.ui.fragments.FragmentSearchUser

class AdapterSearchSet(fragment: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragment, lifecycle) {

    private val allResultsFragment = FragmentAllResult()
    private val setsFragment = FragmentSearchSetResult()
    private val userFragment = FragmentSearchUser()

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> {
                allResultsFragment
            }

            1 -> {
                setsFragment
            }

            2 -> {
                userFragment
            }

            else -> {
                userFragment
            }
        }
    }

//    fun updateSetsData(query: String) {
//        setsFragment.updateData(query)
//    }
//
//    // Phương thức để cập nhật dữ liệu cho tab user khi tìm kiếm
//    fun updateUserData(query: String) {
//        userFragment.updateData(query)
//    }
}
