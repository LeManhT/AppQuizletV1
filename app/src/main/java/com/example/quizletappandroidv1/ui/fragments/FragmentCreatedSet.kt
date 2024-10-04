package com.example.quizletappandroidv1.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.adapter.RvStudySetItemAdapter
import com.example.quizletappandroidv1.databinding.FragmentCreatedSetBinding
import com.example.quizletappandroidv1.models.StudySetModel
import com.example.quizletappandroidv1.utils.Helper
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentCreatedSet : Fragment(), RvStudySetItemAdapter.onClickSetItem {
    private lateinit var binding: FragmentCreatedSetBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var adapterStudySet: RvStudySetItemAdapter
    private val listSetSelected: MutableList<StudySetModel> = mutableListOf()
    private val documentViewModel by viewModels<DocumentViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatedSetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyApplication.userId?.let { userViewModel.getUserData(it) }

        val listStudySet = mutableListOf<StudySetModel>()

        adapterStudySet =
            RvStudySetItemAdapter(requireContext(), object : RVStudySetItem {
                override fun handleClickStudySetItem(setItem: StudySetModel, position: Int) {
                    setItem.isSelected = setItem.isSelected?.not() ?: true
                    adapterStudySet.notifyItemChanged(position)

                    val selectedItems = listStudySet.filter { it.isSelected == true }
                    listSetSelected.clear()
                    if (selectedItems.isNotEmpty()) {
                        listSetSelected.addAll(selectedItems)
                    } else {
                        Log.d("Selected Items", "No items selected")
                    }
                }
            }, enableSwipe = false, true)

        userViewModel.userData.observe(viewLifecycleOwner) {
            it.onSuccess {
                listStudySet.clear()
                val listSets = Helper.getAllStudySets(it)
//                .filter {
//                it.folderOwnerId != folderId
//            }
                listStudySet.addAll(listSets)
                adapterStudySet.updateData(listStudySet)

                if (listStudySet.isEmpty()) {
                    binding.layoutNoData.visibility = View.VISIBLE
                    binding.rvStudySet.visibility = View.GONE
                } else {
                    binding.layoutNoData.visibility = View.GONE
                    binding.rvStudySet.visibility = View.VISIBLE
                }
                // Thông báo cho adapter rằng dữ liệu đã thay đổi để cập nhật giao diện người dùng
                adapterStudySet.notifyDataSetChanged()
            }.onFailure { }

        }


        val rvStudySet = binding.rvStudySet
        rvStudySet.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvStudySet.adapter = adapterStudySet
    }

    override fun handleClickDelete(setId: String) {
        TODO("Not yet implemented")
    }
}