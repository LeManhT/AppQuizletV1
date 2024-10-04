package com.example.quizletappandroidv1.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentChangeLanguageBinding
import java.util.Locale

class ChangeLanguage : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentChangeLanguageBinding
    private lateinit var sharedPreferences: SharedPreferences
    var radioButtonChecked: RadioButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChangeLanguageBinding.inflate(
            layoutInflater,
            container,
            false
        )
        binding.englishRadioButton.setOnClickListener(this)
        binding.chineseRadioButton.setOnClickListener(this)
        binding.vietnameseRadioButton.setOnClickListener(this)

        val toolbar = binding.toolbarChangeLanguage
        (requireActivity() as AppCompatActivity)
            .setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        sharedPreferences = requireContext().getSharedPreferences(
            "ChangeLanguage",
            Context.MODE_PRIVATE
        )
        var mylang =
            sharedPreferences.getString("language", "en")
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
        binding.txtSave.setOnClickListener { restartApp() }
    }

    private fun changeLanguage(selectedLanguage: String): Locale {
        Log.d("ChangeLanguage", selectedLanguage)

        val newLocale = when (selectedLanguage) {
            resources.getString(R.string.english) -> Locale("en")
            resources.getString(R.string.vietnamese) -> Locale("vi")
            resources.getString(R.string.chinese) -> Locale("zh")
            else -> Locale("en")
        }
        return newLocale
    }

    override fun onClick(view: View) {
        val selectedID =
            binding.languageRadioGroup.checkedRadioButtonId
        radioButtonChecked = view.findViewById(selectedID)
        Toast.makeText(requireContext(), radioButtonChecked?.text.toString(), Toast.LENGTH_SHORT)
            .show()
        val newLocale = changeLanguage(radioButtonChecked?.text.toString())
        val newLangCode = newLocale.language

        Log.d("ChangeLanguage2", newLangCode)

        with(sharedPreferences.edit()) {
            putString(
                "language",
                newLangCode
            )
            apply()
        }
        updateLocale(newLocale)
    }

    private fun updateLocale(locale: Locale) {
        val config =
            resources.configuration
        Locale.setDefault(locale)
        config.locale = locale
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            requireActivity().createConfigurationContext(config)
        }
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun restartApp() {
        val intent =
            requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
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