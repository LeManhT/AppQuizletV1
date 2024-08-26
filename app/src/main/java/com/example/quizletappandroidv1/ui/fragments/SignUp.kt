package com.example.quizletappandroidv1.ui.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentSignUpBinding
import com.example.quizletappandroidv1.utils.Helper
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUp : Fragment(), View.OnFocusChangeListener {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var calendar: Calendar
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=])" +  // at least 1 special character
                "(?=\\S+$)" +  // no white spaces
                ".{6,}" +  // at least 6 characters
                "$"
    )

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

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

        //        set toolbar back display
        // Lấy Toolbar từ layout
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        // Thiết lập Toolbar cho AppCompatActivity
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        // Hiển thị biểu tượng quay lại
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false) // Tắt tiêu đề của ActionBar

        // Xử lý sự kiện click vào nút quay lại
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp() // Quay lại màn hình trước
        }

//        Date dialog
        val edtDOB = binding.edtDOB

        // Khởi tạo Calendar
        calendar = Calendar.getInstance()

        // Định dạng ngày
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Calendar.getInstance().time) // Lấy ngày hiện tại
        binding.edtDOB.setText(currentDate)

        // Sự kiện khi EditText được nhấn
        edtDOB.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _: DatePicker?, year: Int, month: Int, day: Int ->
                    // Xử lý khi người dùng chọn ngày
                    calendar.set(year, month, day)
                    val formattedDate = dateFormat.format(calendar.time)
                    edtDOB.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()

        }
//      Spannable text
        val termsTextView = binding.txtTermsOfService
        val text =
            resources.getString(R.string.terms_of_service)// Tìm vị trí của các từ resources.getString(R.string.tos) và resources.getString(R.string.pp) trong văn bản
        val spannableStringBuilder = SpannableStringBuilder(text)

        // Tùy chỉnh màu và font chữ cho resources.getString(R.string.tos)
        val termsOfServiceClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Xử lý khi người dùng bấm vào resources.getString(R.string.tos)
                Toast.makeText(requireContext(), "Bấm vào Terms of Services", Toast.LENGTH_SHORT)
                    .show()

                // Chuyển đến trang web của resources.getString(R.string.tos) (hoặc trang Activity tùy thuộc vào nhu cầu của bạn)
                val termsOfServiceUrl = "https://quizlet.com/tos"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsOfServiceUrl))
                startActivity(intent)
            }
        }
        // Tùy chỉnh màu và font chữ cho resources.getString(R.string.pp)
        val privacyPolicyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Xử lý khi người dùng bấm vào resources.getString(R.string.pp)
                Toast.makeText(requireContext(), "Bấm vào Privacy Policy", Toast.LENGTH_SHORT)
                    .show()

                // Chuyển đến trang web của resources.getString(R.string.pp) (hoặc trang Activity tùy thuộc vào nhu cầu của bạn)
                val privacyPolicyUrl = "https://quizlet.com/privacy"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                startActivity(intent)
            }
        }


        val indexOfTerms = text.indexOf(resources.getString(R.string.tos))
        val indexOfPrivacyPolicy = text.indexOf(resources.getString(R.string.pp))

        // Thay đổi font chữ (đặt kiểu đậm) cho resources.getString(R.string.tos)
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfTerms,
            indexOfTerms + resources.getString(R.string.tos).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Thay đổi font chữ (đặt kiểu đậm) cho resources.getString(R.string.pp)
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfPrivacyPolicy,
            indexOfPrivacyPolicy + resources.getString(R.string.pp).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// Thay đổi màu chữ cho resources.getString(R.string.tos) và resources.getString(R.string.pp)
        val color = Color.BLUE // Chọn màu mong muốn
        spannableStringBuilder.setSpan(
            ForegroundColorSpan(color),
            indexOfTerms,
            indexOfTerms + resources.getString(R.string.tos).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableStringBuilder.setSpan(
            ForegroundColorSpan(color),
            indexOfPrivacyPolicy,
            indexOfPrivacyPolicy + resources.getString(R.string.pp).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        // Áp dụng ClickableSpan cho resources.getString(R.string.tos) và resources.getString(R.string.pp)

        spannableStringBuilder.setSpan(
            termsOfServiceClickableSpan,
            indexOfPrivacyPolicy,
            indexOfPrivacyPolicy + resources.getString(R.string.pp).length,
            0
        )
        spannableStringBuilder.setSpan(
            privacyPolicyClickableSpan,
            indexOfTerms,
            indexOfTerms + resources.getString(R.string.tos).length,
            0
        )
        // Đặt SpannableStringBuilder vào TextView và đặt movementMethod để kích hoạt tính năng bấm vào liên kết
        termsTextView.text = spannableStringBuilder
        termsTextView.movementMethod = LinkMovementMethod.getInstance()


        userViewModel.signUpResult.observe(viewLifecycleOwner, Observer { result ->
            hideLoading()
            result.onSuccess {
                // Handle success
                val msgSignInSuccess = getString(R.string.sign_up_success)
                val intent = Intent(requireContext(), SignIn::class.java)
                startActivity(intent)
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    msgSignInSuccess,
                    CustomToast.LONG,
                    CustomToast.SUCCESS
                ).show()
            }.onFailure { exception ->
                // Handle error
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    exception.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        })


        binding.btnSignUpForm.setOnClickListener {
            val edtEmail = binding.edtEmail.text.toString()
            val edtPass = binding.edtPass.text.toString()
            val dob = binding.edtDOB.text.toString()

            if (validateEmail(edtEmail) && validatePassword(edtPass)) {
                if (Helper.checkBorn(dob)) {
                    showLoading()
                    userViewModel.createUser(edtEmail, edtPass, dob)
                } else {
                    CustomToast(requireContext()).makeText(
                        requireContext(),
                        resources.getString(R.string.not_enough_age),
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                }
            } else {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    resources.getString(R.string.failed_sign_up),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        var errorMessage: String? = null
        if (email.trim().isEmpty()) {
            errorMessage = resources.getString(R.string.errBlankEmail)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errorMessage = resources.getString(R.string.errEmailInvalid)
        }
        if (errorMessage != null) {
            binding.txtLayout1.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    private fun validatePassword(pass: String): Boolean {
        var errorMessage: String? = null
        if (pass.trim().isEmpty()) {
            errorMessage = resources.getString(R.string.errBlankPass)
        } else if (!PASSWORD_PATTERN.matcher(pass.trim()).matches()) {
            errorMessage = resources.getString(R.string.errInsufficientLength)
        }
        if (errorMessage != null) {
            binding.txtLayout2.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSignUpForm.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSignUpForm.visibility = View.VISIBLE
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v?.id != null) {
            when (v.id) {
//                R.id.edtDOB -> {
//                    if(hasFocus){
//                        binding.txtLayout0.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
//                            // Xử lý dữ liệu khi người dùng đã chọn ngày mới
//                            val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
//                        }
//                    }
//                }

                R.id.edtEmail -> {
                    if (hasFocus) {
                        //isCounterEnabled được đặt thành true, thì trường nhập liệu hoặc widget đó sẽ theo dõi và hiển thị số ký tự mà người dùng đã nhập vào
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
                        validatePassword(binding.edtPass.text.toString())
                    }
                }
            }
        }
    }


}