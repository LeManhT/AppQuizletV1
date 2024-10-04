package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.RVFolderItemAdapter
import com.example.quizletappandroidv1.databinding.FragmentFolderBinding
import com.example.quizletappandroidv1.listener.RVFolderItem
import com.example.quizletappandroidv1.models.FolderModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderFragment : Fragment() {
    private lateinit var binding: FragmentFolderBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyApplication.userId?.let { userViewModel.getUserData(it) }

        val adapterFolder =
            RVFolderItemAdapter(requireContext(), object : RVFolderItem {
                override fun handleClickFolderItem(folderItem: FolderModel, position: Int) {
//                    val action =
//                        FragmentFolderDirections.actionFragmentFolderToFolderDetail(folderItem.id)
//                    findNavController().navigate(action)
                }
            })
        userViewModel.userData.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                if (it.documents.folders.isEmpty()) {
                    binding.rvFolderFragment.visibility = View.GONE
                    binding.layoutNoData.visibility = View.VISIBLE
                } else {
                    binding.rvFolderFragment.visibility = View.VISIBLE
                    binding.layoutNoData.visibility = View.GONE
                }
                adapterFolder.updateData(it.documents.folders)
            }.onFailure { }
        }
        // Access the RecyclerView through the binding
        val rvFolder = binding.rvFolderFragment
        rvFolder.layoutManager = LinearLayoutManager(context)
        rvFolder.adapter = adapterFolder

        adapterFolder.setOnClickFolderListener(object : RVFolderItemAdapter.onClickFolder {
            override fun handleDeleteFolder(folderId: String) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.warning))
                    .setMessage(resources.getString(R.string.confirm_delete_folder))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    }
                    .show()

            }
        })
    }

}

