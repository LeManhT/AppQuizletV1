package com.example.appquizlet.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.quizletappandroidv1.ui.fragments.FolderFragment
import com.example.quizletappandroidv1.ui.fragments.StudySet

class ViewPagerLibAdapter(fragment: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragment, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> {
                StudySet()
            }

            1 -> {
                FolderFragment()
            }
//
//            2 -> {
//                Classes()
//            }

            else -> {
                FolderFragment()
            }
        }
    }
}