package com.example.quizletappandroidv1.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.quizletappandroidv1.adapter.RvStudySetItemAdapter
import com.example.quizletappandroidv1.databinding.FragmentCreatedSetBinding
import com.example.quizletappandroidv1.models.StudySetModel

class FragmentCreatedSet : Fragment(), RvStudySetItemAdapter.onClickSetItem {
    private lateinit var binding: FragmentCreatedSetBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var adapterStudySet: RvStudySetItemAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatedSetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterStudySet =
            RvStudySetItemAdapter(requireContext(), object : RVStudySetItem {
                override fun handleClickStudySetItem(setItem: StudySetModel, position: Int) {
                    setItem.isSelected = setItem.isSelected?.not() ?: true
                    adapterStudySet.notifyItemChanged(position)

//                    val selectedItems = listStudySet.filter { it.isSelected == true }
//                    listSetSelected.clear()
//                    if (selectedItems.isNotEmpty()) {
//                        listSetSelected.addAll(selectedItems)
//                    } else {
//                        Log.d("Selected Items", "No items selected")
//                    }
                }
            }, enableSwipe = false, true)


    }

    override fun handleClickDelete(setId: String) {
        TODO("Not yet implemented")
    }
}