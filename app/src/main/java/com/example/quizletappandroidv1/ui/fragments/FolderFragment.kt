package com.example.quizletappandroidv1.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.RVFolderItemAdapter
import com.example.quizletappandroidv1.databinding.FragmentFolderBinding
import com.example.quizletappandroidv1.listener.RVFolderItem
import com.example.quizletappandroidv1.models.FolderModel
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FolderFragment : Fragment() {
    private lateinit var binding: FragmentFolderBinding
    private val userViewModel: UserViewModel by viewModels()
    private val documentViewModel: DocumentViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog

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
                    val action =
                        FolderFragmentDirections.actionFragmentFoldersToFolderDetail(folderItem.id)
                    findNavController().navigate(action)
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

        documentViewModel.folderResponse.observe(viewLifecycleOwner) { result ->
            try {
                showLoading(resources.getString(R.string.delete_folder_loading))
                adapterFolder.updateData(result.documents.folders)
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                progressDialog.dismiss()
            }
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
                        MyApplication.userId?.let { documentViewModel.deleteFolder(it, folderId) }
                    }
                    .show()
            }
        })
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
    }

}

