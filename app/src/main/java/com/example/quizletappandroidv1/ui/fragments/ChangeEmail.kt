package com.example.quizletappandroidv1.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeEmailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentEmail = requireActivity().intent.getStringExtra("currentEmail")

        binding.btnChangeEmail.setOnClickListener {
            if (binding.edtEmailChange.text?.isEmpty() == true) {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    resources.getString(R.string.cannot_empty_field),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } else {
                val newEmail = binding.edtEmailChange.text.toString()
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

        binding.txtBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun changeEmail(userId: String, newEmail: String) {
//        lifecycleScope.launch {
//            try {
//                showLoading(resources.getString(R.string.changing_your_email))
//                val body = JsonObject().apply {
//                    addProperty("email", newEmail)
//                }
//                val result = apiService.updateUserInfoNoImg(userId, body)
//                if (result.isSuccessful) {
//                    result.body().let {
//                        if (it != null) {
//                            UserM.setDataSettings(it)
//                        }
//                    }
//                    CustomToast(requireContext()).makeText(
//                        requireContext(),
//                        resources.getString(R.string.change_email_success),
//                        CustomToast.LONG,
//                        CustomToast.SUCCESS
//                    ).show()
//
//                    val i = Intent(requireContext(), Settings::class.java)
//                    startActivity(i)
//                } else {
//                    result.errorBody()?.let {
//                        // Show your CustomToast or handle the error as needed
//                        CustomToast(requireContext()).makeText(
//                            requireContext(),
//                            it.toString(),
//                            CustomToast.LONG,
//                            CustomToast.ERROR
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                CustomToast(requireContext()).makeText(
//                    requireContext(),
//                    e.message.toString(),
//                    CustomToast.LONG,
//                    CustomToast.ERROR
//                ).show()
//            } finally {
//                progressDialog.dismiss()
//            }
//        }
    }


    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
    }

}