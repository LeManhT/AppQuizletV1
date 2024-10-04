package com.example.quizletappandroidv1.ui.fragments

import android.app.AlertDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.appquizlet.adapter.ViewPagerLibAdapter
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentLibraryBinding
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonObject

class FragmentLibrary : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private lateinit var navController: NavController
    private lateinit var adapter: ViewPagerLibAdapter
    private val documentViewModel: DocumentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(layoutInflater, container, false)
        navController = findNavController()
        setupViewPager()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtLibPlus.setOnClickListener {
            val currentItem = binding.pagerLib.currentItem
            if (currentItem < childFragmentManager.fragments.size) {
                val currentFragment = childFragmentManager.fragments[currentItem]
                //                Log.d("tf", "$currentItem $currentFragment")
                if (currentFragment is FolderFragment) {
                    showCustomDialog(
                        resources.getString(R.string.add_folder),
                        "",
                        resources.getString(R.string.folder_name),
                        resources.getString(R.string.desc_optional)
                    )
                }
                if (currentFragment is StudySet) {
                    findNavController().navigate(R.id.action_fragmentLibrary2_to_createSet)
                }
            }
        }

    }

    private fun setupViewPager() {
        adapter = ViewPagerLibAdapter(childFragmentManager, lifecycle)

        binding.pagerLib.adapter = adapter

        TabLayoutMediator(binding.tabLib, binding.pagerLib) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.lb_study_sets)
                1 -> tab.text = getString(R.string.folders)
            }
        }.attach()

// Handle tab changes and trigger navigation
//        binding.pagerLib.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                // Navigate to the appropriate fragment based on tab selection
//                when (position) {
//                    0 -> navController.navigate(R.id.fragmentStudySets)
//                    1 -> navController.navigate(R.id.fragmentFolders)
//                }
//            }
//        })
    }

    private fun showCustomDialog(
        title: String,
        content: String,
        edtPlaceholderFolderName: String,
        edtPlaceholderDesc: String
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 0, 0, 0)

        if (content.isNotEmpty()) {
            val textContent = TextView(context)
            textContent.text = content
            textContent.setPadding(10, 0, 10, 0)
            layout.addView(textContent)
        }

        val editTextFolder = createEditTextWithCustomBottomBorder(edtPlaceholderFolderName)
        layout.addView(editTextFolder)

        val editTextDesc = createEditTextWithCustomBottomBorder(edtPlaceholderDesc)
        layout.addView(editTextDesc)
        builder.setView(layout)
        builder.setPositiveButton("OK") { dialog, _ ->
            val inputText = editTextFolder.text.toString()
            val description = editTextDesc.text.toString()
            val body = JsonObject().apply {
                addProperty(resources.getString(R.string.createFolderNameField), inputText)
                addProperty(resources.getString(R.string.descriptionField), description)
            }

            MyApplication.userId?.let { documentViewModel.createNewFolder(it, body) }

            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }


    private fun createEditTextWithCustomBottomBorder(hint: String): EditText {
        val editText =
            EditText(context)
        editText.hint = hint
        val defaultBottomBorder =
            GradientDrawable()
        defaultBottomBorder.setSize(0, 5)
        val focusBottomBorder =
            GradientDrawable()
        focusBottomBorder.setSize(0, 10)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 10
        layoutParams.bottomMargin = 10
        editText.layoutParams = layoutParams
        editText.background = defaultBottomBorder
        editText.setOnFocusChangeListener { view, hasFocus ->
            editText.background =
                if (hasFocus) focusBottomBorder else defaultBottomBorder
        }
        return editText
    }

}