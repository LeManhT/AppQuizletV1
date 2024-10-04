package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.ViewPagerTabAddSet
import com.example.quizletappandroidv1.databinding.FragmentAddSetToFolderBinding
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddSetToFolder : Fragment() {
    private lateinit var binding: FragmentAddSetToFolderBinding
    private val args : AddSetToFolderArgs by navArgs()
    private val documentViewModel: DocumentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddSetToFolderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapterTabSet = ViewPagerTabAddSet(childFragmentManager, lifecycle)
        binding.pagerAddSetLib.adapter = adapterTabSet
//        adapterTabSet.setFolderId(folderId)
        TabLayoutMediator(binding.tabLibAddSet, binding.pagerAddSetLib) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = resources.getString(R.string.created)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.note, null)
                }
//                1 -> tab.text = resources.getString(R.string.studied)
                1 -> {
                    tab.text = resources.getString(R.string.folders)
                    tab.icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.folder_outlined, null)
                }
            }
        }.attach()

        binding.iconAddToSet.setOnClickListener {
//            // Lấy fragment hiện tại của ViewPager
//            val currentFragment =
//                supportFragmentManager.fragments[binding.pagerAddSetLib.currentItem]
//
//            if (currentFragment is FragmentCreatedSet) {
//                // Gọi phương thức trong fragment
//                currentFragment.insertSetToFolder(folderId)
//            }
//
//            if (currentFragment is FragmentFolderSet) {
//                currentFragment.insertFolderToFolder(folderId)
//            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
