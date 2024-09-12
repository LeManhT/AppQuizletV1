package com.example.quizletappandroidv1.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresExtension
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.quizletappandroidv1.MainActivity
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentSignInBinding
import com.example.quizletappandroidv1.utils.UserSession
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignIn : Fragment(), View.OnFocusChangeListener {
    private lateinit var binding: FragmentSignInBinding
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=])" +  // at least 1 special character
                "(?=\\S+$)" +  // no white spaces
                ".{6,}" +  // at least 8 characters
                "$"
    )

    //    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        binding.txtLayout1.onFocusChangeListener = this
        binding.txtLayout2.onFocusChangeListener = this
        binding.edtEmail.onFocusChangeListener = this
        binding.edtPass.onFocusChangeListener = this

//        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        return binding.root
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            hideLoading()
            result.onSuccess { userResponse ->
                UserSession.saveUserId(requireContext(), userResponse.id)
//                userViewModel.setUserResponse(result)
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            }.onFailure { exception ->
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    exception.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }

        binding.btnSignin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPass.text.toString()
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            startActivity(intent)
            if (validateEmail(email) && validatePass(pass)) {
                showLoading()
                userViewModel.loginUser(email, pass)
            } else {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    resources.getString(R.string.wrong_email_or_pass),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }

    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v?.id != null) {
            when (v.id) {
                R.id.edtEmail -> {
                    if (hasFocus) {
                        if (binding.txtLayout1.isErrorEnabled) {
                            binding.txtLayout1.isErrorEnabled = false
                        }
                    } else {
                        validateEmail(binding.edtEmail.text.toString())
                    }
                }

                R.id.edtPass -> {
                    if (hasFocus) {
                        if (binding.txtLayout2.isErrorEnabled) {
                            binding.txtLayout2.isErrorEnabled = false
                        }
                    } else {
                        validatePass(binding.edtPass.text.toString())
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSignin.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSignin.visibility = View.VISIBLE
    }


    private fun validateEmail(email: String): Boolean {
        var errorMess: String? = null
        if (email.trim().isEmpty()) {
            errorMess = resources.getString(R.string.errBlankEmail)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errorMess = resources.getString(R.string.errEmailInvalid)
        }
        if (errorMess != null) {
            binding.txtLayout1.apply {
                isErrorEnabled = true
                error = errorMess
            }
        }
        return errorMess == null
    }

    private fun validatePass(pass: String): Boolean {
        var errorMess: String? = null
        if (pass.trim().isEmpty()) {
            errorMess = resources.getString(R.string.errBlankEmail)
        } else if (!PASSWORD_PATTERN.matcher(pass.trim()).matches()) {
            errorMess = resources.getString(R.string.errInsufficientLength)
        }
        if (errorMess != null) {
            binding.txtLayout2.apply {
                isErrorEnabled = true
                error = errorMess
            }
        }
        return errorMess == null
    }


    private fun saveIdUser(
        userId: String,
        userName: String,
        password: String,
        isLoggedIn: Boolean
    ) {
        sharedPreferences =
            requireActivity().getSharedPreferences("idUser", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("key_userid", userId)
        editor.putString("key_userPass", password)
        editor.putString("key_username", userName)
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

}