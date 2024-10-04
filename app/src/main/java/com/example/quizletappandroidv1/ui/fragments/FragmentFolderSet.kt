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
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.RVFolderItemAdapter
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentFolderSetBinding
import com.example.quizletappandroidv1.listener.RVFolderItem
import com.example.quizletappandroidv1.models.FolderModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentFolderSet : Fragment() {
    private lateinit var binding: FragmentFolderSetBinding
    private lateinit var progressDialog: ProgressDialog
    private val listFolderSelected: MutableList<FolderModel> = mutableListOf()
    private val listSetId: MutableSet<String> = mutableSetOf()
    private var folderId: String? = null

    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderSetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyApplication.userId?.let { userViewModel.getUserData(it) }

        val listFolderItems = mutableListOf<FolderModel>()


        val adapterFolder =
            RVFolderItemAdapter(requireContext(), object : RVFolderItem {
                override fun handleClickFolderItem(folderItem: FolderModel, position: Int) {
                    folderItem.isSelected = folderItem.isSelected?.not() ?: true
                    if (folderItem.studySets.isEmpty()) {
                        context?.let {
                            CustomToast(it).makeText(
                                requireContext(),
                                resources.getString(R.string.this_folder_empty),
                                CustomToast.LONG,
                                CustomToast.WARNING
                            ).show()
                        }
                    } else {
                        val selectedFolder = listFolderItems.filter { it.isSelected == true }
                        if (selectedFolder.isNotEmpty()) {
                            listFolderSelected.addAll(selectedFolder)
                            listFolderSelected.map {
                                it.studySets.forEach { it1 ->
                                    listSetId.add(it1.id)
                                }
                            }
                            Log.d("ids", Gson().toJson(listSetId))
                        }
                    }
                }
            })

        userViewModel.userData.observe(viewLifecycleOwner) { userResponse ->
            userResponse.onSuccess {
                listFolderItems.clear()
                val listFolderAdd = it.documents.folders.filter {
                    it.id != folderId
                }

                listFolderItems.addAll(listFolderAdd)
                adapterFolder.updateData(listFolderItems)

                if (listFolderItems.isEmpty()) {
                    binding.layoutNoData.visibility = View.VISIBLE
                    binding.rvFolderFragment.visibility = View.GONE
                } else {
                    binding.layoutNoData.visibility = View.GONE
                    binding.rvFolderFragment.visibility = View.VISIBLE
                }
                adapterFolder.notifyDataSetChanged()
            }.onFailure { }
        }
        // Access the RecyclerView through the binding
        val rvFolder = binding.rvFolderFragment
        rvFolder.layoutManager = LinearLayoutManager(context)
        rvFolder.adapter = adapterFolder

    }


}