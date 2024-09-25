package com.example.quizletappandroidv1.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentChangeEmailBinding
import com.example.quizletappandroidv1.utils.Helper
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel

class ChangeEmail : Fragment() {
    private lateinit var binding: FragmentChangeEmailBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeEmailBinding.inflate(layoutInflater, container, false)
        val toolbar = binding.toolbarChangeEmail
        (requireActivity() as AppCompatActivity)
            .setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentEmail =
            requireActivity().intent.getStringExtra("currentEmail")
        binding.btnChangeEmail.setOnClickListener {
            if (binding.edtEmailChange.text?.isEmpty() == true) {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    resources.getString(R.string.cannot_empty_field),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } else {
                val newEmail =
                    binding.edtEmailChange.text.toString()
                if (newEmail == currentEmail) {
                    CustomToast(requireContext()).makeText(
                        requireContext(),
                        resources.getString(R.string.your_current_mail_concide),
                        CustomToast.LONG,
                        CustomToast.WARNING
                    ).show()
                } else {
                    changeEmail(Helper.getDataUserId(requireContext()), newEmail)
                }
            }
        }
        binding.txtSave.setOnClickListener {
            findNavController().popBackStack()
            Log.d("PopBackStack", "txtBackEmail")
        }
    }

    private fun changeEmail(userId: String, newEmail: String) {}
    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
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