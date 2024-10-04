package com.example.quizletappandroidv1.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentChangeEmailBinding
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import timber.log.Timber

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
                    MyApplication.userId?.let { it1 -> userViewModel.changeEmail(it1, newEmail) }
                }
            }
        }

        userViewModel.changeEmailResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    resources.getString(R.string.change_email_success),
                    CustomToast.LONG,
                    CustomToast.SUCCESS
                ).show()
                findNavController().popBackStack()
            } else {
                Timber.d("Change email failed")
            }
        }
    }

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