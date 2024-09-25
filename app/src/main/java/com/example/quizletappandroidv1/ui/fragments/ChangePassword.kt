package com.example.quizletappandroidv1.ui.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentChangePasswordBinding
import com.example.quizletappandroidv1.ui.activity.AuthActivity
import com.example.quizletappandroidv1.utils.SharedPreferencesManager
import java.util.regex.Pattern

class ChangePassword : Fragment(), OnFocusChangeListener {
    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var progressDialog: ProgressDialog
    private val PASSWORD_PATTERN: Pattern =
        Pattern.compile(
            "^" + "(?=.*[@#$%^&+=])" +  // at least 1 special character
                    "(?=\\S+$)" +  // no white spaces
                    ".{6,}" +  // at least 6 characters
                    "$"
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(
            layoutInflater,
            container,
            false
        )
        binding.layoutNewPass.onFocusChangeListener = this
        binding.layoutConfirmPass.onFocusChangeListener = this
        binding.edtNewPassword.onFocusChangeListener = this
        binding.edtConfirmYourPassword.onFocusChangeListener = this
        val toolbar =
            binding.toolbarChangePassword
        (requireActivity() as AppCompatActivity)
            .setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val curPass =
            requireActivity().intent.getStringExtra("currentPass")
        binding.txtSave.setOnClickListener {
            val currentPass = binding.edtCurrentPassword.text.toString()
            val newPass = binding.edtNewPassword.text.toString()
            val confirmPass =
                binding.edtConfirmYourPassword.text.toString()
            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    resources.getString(R.string.cannot_empty_field),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } else {
                if (currentPass != curPass) {
                    CustomToast(requireContext()).makeText(
                        requireContext(),
                        resources.getString(R.string.current_pass_incorrect),
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                } else if (newPass != confirmPass) {
                    CustomToast(requireContext()).makeText(
                        requireContext(),
                        resources.getString(R.string.pass_not_equal_confirm),
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                } else {
                    if (newPass == currentPass) {
                        CustomToast(requireContext()).makeText(
                            requireContext(),
                            resources.getString(R.string.pass_and_new_pass_coincide),
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    } else if (validatePassword(newPass) && validatePassword(confirmPass)) {
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                Log.d("PopBackStack", "Vaooo")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changePassword(
        userId: String,
        oldPass: String,
        newPass: String
    ) {
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
    }

    private fun validatePassword(pass: String): Boolean {
        var errorMessage: String? = null
        if (pass.trim().isEmpty()) {
            errorMessage = resources.getString(R.string.errBlankPass)
        } else if (!PASSWORD_PATTERN.matcher(pass.trim()).matches()) {
            errorMessage = resources.getString(R.string.errInsufficientLength)
        }
        if (errorMessage != null) {
            binding.layoutNewPass.apply {
                isErrorEnabled = true
                error = errorMessage
            }
            binding.layoutConfirmPass.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        } else {
            binding.layoutNewPass.apply {
                isErrorEnabled = false
            }
            binding.layoutConfirmPass.apply { isErrorEnabled = false }
        }
        return errorMessage == null
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v?.id != null) {
            when (v.id) {
                R.id.edt_new_password -> {
                    if (hasFocus) {
                        if (binding.layoutNewPass.isErrorEnabled) {
                            binding.layoutNewPass.isErrorEnabled = false
                        }
                    } else {
                        validatePassword(binding.edtNewPassword.text.toString())
                    }
                }

                R.id.edt_confirm_your_password -> {
                    if (hasFocus) {
                        if (binding.layoutConfirmPass.isErrorEnabled) {
                            binding.layoutConfirmPass.isErrorEnabled = false
                        }
                    } else {
                        validatePassword(binding.edtConfirmYourPassword.text.toString())
                    }
                }
            }
        }
    }

    private fun logOut() {
        val intent = Intent(
            requireContext(),
            AuthActivity::class.java
        )
        SharedPreferencesManager.clearAllPreferences(requireContext())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(
            intent
        )
        requireActivity().finish()
    }
}