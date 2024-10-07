package com.example.quizletappandroidv1.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.PhotoSplashAdapter
import com.example.quizletappandroidv1.databinding.FragmentStartAppBinding
import com.example.quizletappandroidv1.models.PhotoSplash

class StartAppFragment : Fragment() {
    private lateinit var binding: FragmentStartAppBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val termsTextView = binding.txtTermsServices
        val text =
            resources.getString(R.string.terms_of_service)// Tìm vị trí của các từ "Terms of Services" và "Privacy Policy" trong văn bản
        val spannableStringBuilder = SpannableStringBuilder(text)

        // Tùy chỉnh màu và font chữ cho "Terms of Services"
        val termsOfServiceClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Xử lý khi người dùng bấm vào "Terms of Services"

                // Chuyển đến trang web của "Terms of Services" (hoặc trang Activity tùy thuộc vào nhu cầu của bạn)
                val termsOfServiceUrl = "https://quizlet.com/tos"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsOfServiceUrl))
                startActivity(intent)
            }
        }
        // Tùy chỉnh màu và font chữ cho "Privacy Policy"
        val privacyPolicyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Xử lý khi người dùng bấm vào "Privacy Policy"

                // Chuyển đến trang web của "Privacy Policy" (hoặc trang Activity tùy thuộc vào nhu cầu của bạn)
                val privacyPolicyUrl = "https://quizlet.com/privacy"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                startActivity(intent)
            }
        }

        val spannableString =
            SpannableString(getString(R.string.if_you_want_sign_in_with_admin_account_please_click_here))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle the click event for "click here"
                findNavController().navigate(R.id.action_startAppFragment_to_fragmentLoginAdmin)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE // Set the color for the clickable text
                ds.isUnderlineText = false // Remove underline if not needed
            }
        }

        val startIndex = spannableString.indexOf("click here")
        val endIndex = startIndex + "click here".length

        spannableString.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.txtLoginAdmin.text = spannableString
        binding.txtLoginAdmin.movementMethod = LinkMovementMethod.getInstance()


        val indexOfTerms = text.indexOf(resources.getString(R.string.tos))
        val indexOfPrivacyPolicy = text.indexOf(resources.getString(R.string.pp))
        if (indexOfTerms != -1 && indexOfPrivacyPolicy != -1) {
            // Thay đổi font chữ (đặt kiểu đậm) cho "Terms of Services"
            spannableStringBuilder.setSpan(
                StyleSpan(Typeface.BOLD),
                indexOfTerms,
                indexOfTerms + resources.getString(R.string.tos).length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Thay đổi font chữ (đặt kiểu đậm) cho "Privacy Policy"
            spannableStringBuilder.setSpan(
                StyleSpan(Typeface.BOLD),
                indexOfPrivacyPolicy,
                indexOfPrivacyPolicy + resources.getString(R.string.pp).length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

// Thay đổi màu chữ cho "Terms of Services" và "Privacy Policy"
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


            // Áp dụng ClickableSpan cho "Terms of Services" và "Privacy Policy"

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
        }

        val indicators = binding.circleIndicator3
        val listItemPhoto = mutableListOf<PhotoSplash>()
        listItemPhoto.add(
            PhotoSplash(
                R.raw.splash2,
                resources.getString(R.string.splash_text1)
            )
        )
        listItemPhoto.add(
            PhotoSplash(
                R.raw.splash4,
                resources.getString(R.string.splash_text2)
            )
        )
        listItemPhoto.add(
            PhotoSplash(
                R.raw.splash5,
                resources.getString(R.string.splash_text3)
            )
        )
        listItemPhoto.add(
            PhotoSplash(
                R.raw.splash6,
                resources.getString(R.string.splash_text4)
            )
        )
        val adapterPhotoSlash = PhotoSplashAdapter(listItemPhoto)
        binding.viewPagerSplash.adapter = adapterPhotoSlash
        indicators.setViewPager(binding.viewPagerSplash)

        binding.btnSignin.setOnClickListener {
            findNavController().navigate(R.id.action_startAppFragment_to_signIn2)
        }

        binding.btnSignup.setOnClickListener {
            findNavController().navigate(R.id.action_startAppFragment_to_signUp2)
        }


        binding.imgSplashSearch.setOnClickListener {
            findNavController().navigate(R.id.action_startAppFragment_to_fragmentSearch2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartAppBinding.inflate(inflater, container, false)
        return binding.root
    }

}