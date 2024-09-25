package com.example.quizletappandroidv1.ui.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.RvStudySetItemAdapter
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentFolderDetailBinding
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.models.StudySetModel
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderDetail : Fragment() {
    private lateinit var progressDialog: ProgressDialog
    private lateinit var binding: FragmentFolderDetailBinding

    private val documentViewModel: DocumentViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private val args: FolderDetailArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyApplication.userId?.let { userViewModel.getUserData(it) }

        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)


        val adapterStudySet = RvStudySetItemAdapter(requireContext(), object : RVStudySetItem {
            override fun handleClickStudySetItem(setItem: StudySetModel, position: Int) {
                val i = Intent(requireContext(), StudySetDetail::class.java)
//                i.putExtra("setId", listStudySet[position].id)
                i.putExtra("args.folderIdCl", args.folderId)
                startActivity(i)
            }
        }, true)

        binding.rvStudySet.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvStudySet.adapter = adapterStudySet


        userViewModel.userData.observe(viewLifecycleOwner) { userResponse ->
            userResponse.onSuccess {
                val targetFolder =
                    it.documents.folders.find { folderItem -> folderItem.id == args.folderId }
                if (targetFolder != null) {
//                    listStudySet.addAll(targetFolder.studySets)
                    adapterStudySet.updateData(targetFolder.studySets)
                }
                if (targetFolder != null) {
                    binding.txtCountSet.text =
                        if (targetFolder.studySets.size == 1) targetFolder.studySets.size.toString() + " term" else targetFolder.studySets.size.toString() + (" terms")
                }
                if (targetFolder != null) {
                    binding.txtFolderName.text = targetFolder.name
                    if (targetFolder.description.isEmpty()) {
                        binding.txtDesc.visibility = View.GONE
                    } else {
                        binding.txtDesc.visibility = View.VISIBLE
                        binding.txtDesc.text = targetFolder.description
                    }
                }
                binding.txtFolderClickUsername.text = it.loginName
                if (targetFolder != null) {
                    if (targetFolder.studySets.isEmpty()) {
                        binding.rvStudySet.visibility = View.GONE
                        binding.layoutNoData.visibility = View.VISIBLE
                    }
                }
            }.onFailure {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    resources.getString(R.string.error_occurred),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }

        }

        documentViewModel.removeSetFromFolderResponse.observe(viewLifecycleOwner) { response ->
            handleResponse(response, resources.getString(R.string.operation_successful))
            adapterStudySet.updateData(
                response.documents.folders.find { it.id == args.folderId }?.studySets ?: emptyList()
            )
            progressDialog.dismiss()
        }

        documentViewModel.folderResponse.observe(viewLifecycleOwner) { response ->
            handleResponse(response, resources.getString(R.string.operation_successful))
        }


        adapterStudySet.setOnItemClickListener(object : RvStudySetItemAdapter.onClickSetItem {
            override fun handleClickDelete(setId: String) {
                MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.warning))
                    .setMessage(resources.getString(R.string.confirm_remove_set_from_folder))
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                        showLoading(resources.getString(R.string.remove_set_folder_loading))
                        MyApplication.userId?.let {
                            documentViewModel.removeSetFromFolder(it, args.folderId, setId)
                        }
                    }.show()
            }
        })

        // Other setup code...
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                return true
            }

            R.id.option_edit -> {
                showEditCustomDialog(
                    resources.getString(R.string.edit_folder),
                    resources.getString(R.string.folder_name),
                    resources.getString(R.string.desc_optional)
                )
                return true
            }

            R.id.option_add_sets -> {
                val i = Intent(requireContext(), AddSetToFolder::class.java)
                i.putExtra("folderAddSet", args.folderId)
                startActivity(i)
            }

            R.id.option_delete -> {
                showDeleteDialog(
                    resources.getString(R.string.delete_text)
                )
            }

            R.id.option_share -> {
                MyApplication.userId?.let { shareDialog(it, args.folderId) }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun showEditCustomDialog(
        title: String, folderNameHint: String, folderDescHint: String
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(60, 0, 0, 0)

        val editTextFolderName = EditText(requireContext())
        editTextFolderName.hint = folderNameHint
        layout.addView(editTextFolderName)

        val editTextFolderDesc = EditText(requireContext())
        editTextFolderDesc.hint = folderDescHint
        layout.addView(editTextFolderDesc)

        // Set layout parameters with margins
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 10
        layoutParams.bottomMargin = 10
        editTextFolderName.layoutParams = layoutParams
        editTextFolderDesc.layoutParams = layoutParams

        builder.setView(layout)

        builder.setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
            MyApplication.userId?.let {
                val folderName = editTextFolderName.text.toString()
                val folderDesc = editTextFolderDesc.text.toString()
                documentViewModel.updateFolder(it, args.folderId, JsonObject().apply {
                    addProperty("name", folderName)
                    addProperty("description", folderDesc)
                })
            }
            dialog.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun showDeleteDialog(desc: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(desc)
        builder.setPositiveButton(resources.getString(R.string.delete)) { dialog, _ ->
            MyApplication.userId?.let { documentViewModel.deleteFolder(it, args.folderId) }
            dialog.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun shareDialog(userId: String, folderId: String) {
        val deepLinkBaseUrl = "www.ttcs_quizlet.com/folder"
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "$deepLinkBaseUrl/$userId/$folderId")
        val shareIntent = Intent.createChooser(sharingIntent, "Share via")
        startActivity(shareIntent)
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle(resources.getString(R.string.loading_data))
        progressDialog.setMessage(msg)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun handleResponse(response: UserResponse?, handleMessage: String) {
        response?.let {
            CustomToast(requireContext()).makeText(
                requireContext(), handleMessage, CustomToast.LONG, CustomToast.SUCCESS
            ).show()
            progressDialog.dismiss()
        }
    }
}

