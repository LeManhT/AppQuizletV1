package com.example.quizletappandroidv1.ui.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.RvStudySetItemAdapter
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentStudySetBinding
import com.example.quizletappandroidv1.models.StudySetModel
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StudySet : Fragment() {
    private lateinit var binding: FragmentStudySetBinding
    private val userViewModel: UserViewModel by viewModels()
    private val documentViewModel: DocumentViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudySetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyApplication.userId?.let { userViewModel.getUserData(it) }

        binding.txtCreateNewSet.setOnClickListener {
            val i = Intent(context, CreateSet::class.java)
            startActivity(i)
        }

        val adapterStudySet =
            RvStudySetItemAdapter(requireContext(), object : RVStudySetItem {
                override fun handleClickStudySetItem(setItem: StudySetModel, position: Int) {
                    val action = StudySetDirections.actionStudySetToStudySetDetail(setItem.id)
                    findNavController().navigate(action)
                }
            }, true)

        userViewModel.userData.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                if (it.documents.studySets.isEmpty()) {
                    binding.rvStudySet.visibility = View.GONE
                    binding.layoutNoData.visibility = View.VISIBLE
                }
                adapterStudySet.updateData(it.documents.studySets)
            }.onFailure {
                binding.rvStudySet.visibility = View.GONE
                binding.layoutNoData.visibility = View.VISIBLE
                CustomToast(requireContext()).makeText(
                    requireContext(), "Error loading data", CustomToast.LONG, CustomToast.SUCCESS
                ).show()
            }
        }

        documentViewModel.deleteSetResponse.observe(viewLifecycleOwner) {
            try {
                showLoading(resources.getString(R.string.deleteSetLoading))
                adapterStudySet.updateData(it.documents.studySets)
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                progressDialog.dismiss()
            }
        }

        val rvStudySet = binding.rvStudySet
        rvStudySet.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvStudySet.adapter = adapterStudySet
        adapterStudySet.setOnItemClickListener(object : RvStudySetItemAdapter.onClickSetItem {
            override fun handleClickDelete(setId: String) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.warning))
                    .setMessage(resources.getString(R.string.confirm_delete_set))
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                        MyApplication.userId?.let { documentViewModel.deleteStudySet(setId, it) }
                    }
                    .show()
            }
        })
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
    }

}