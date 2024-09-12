package com.example.quizletappandroidv1.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quizletappandroidv1.databinding.FragmentChangeLanguageBinding
import java.util.Locale

class ChangeLanguage : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentChangeLanguageBinding
    private lateinit var sharedPreferences: SharedPreferences
    var radioButtonChecked: RadioButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeLanguageBinding.inflate(layoutInflater, container, false)

        binding.englishRadioButton.setOnClickListener(this)
        binding.chineseRadioButton.setOnClickListener(this)
        binding.vietnameseRadioButton.setOnClickListener(this)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtBack.setOnClickListener {
            requireActivity().finish()
        }

        sharedPreferences =
            requireContext().getSharedPreferences("ChangeLanguage", Context.MODE_PRIVATE)
        var mylang = sharedPreferences.getString("language", "en")

        radioButtonChecked = when (mylang) {
            "vi" -> binding.vietnameseRadioButton
            "en" -> binding.englishRadioButton
            "zh" -> binding.chineseRadioButton
            else -> null
        }

        if (mylang == null) {
            mylang = "en"
            with(sharedPreferences.edit()) {
                putString("language", mylang)
                apply()
            }
        }


        radioButtonChecked?.isChecked = true
        binding.iconTickChangeLanguage.setOnClickListener {
            restartApp()
        }
    }

    private fun changeLanguage(selectedLanguage: String): Locale {
        val newLocale = when (selectedLanguage) {
            "English" -> Locale("en")
            "Vietnamese" -> Locale("vi")
            "Chinese" -> Locale("zh")
            else -> Locale("en")
        }
        return newLocale
    }

// ...

    override fun onClick(view: View) {
        val selectedID = binding.languageRadioGroup.checkedRadioButtonId
        radioButtonChecked = view.findViewById(selectedID)
        Toast.makeText(requireContext(), radioButtonChecked?.text.toString(), Toast.LENGTH_SHORT)
            .show()

        // Update the language code in mylang variable
        val newLocale = changeLanguage(radioButtonChecked?.text.toString())
        val newLangCode = newLocale.language

        // Update the language in SharedPreferences
        with(sharedPreferences.edit()) {
            putString("language", newLangCode)
            apply()
        }

        // Change the locale and restart the app
        updateLocale(newLocale)
    }

    // Add a function to update the locale
    private fun updateLocale(locale: Locale) {
        val config = resources.configuration
        Locale.setDefault(locale)
        config.locale = locale

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            requireActivity().createConfigurationContext(config)
        }
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun restartApp() {
        // Tạo Intent để khởi động lại ứng dụng
        val intent = requireContext().packageManager
            .getLaunchIntentForPackage(requireContext().packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)

        // Kết thúc Activity hiện tại
        requireActivity().finish()
    }


}