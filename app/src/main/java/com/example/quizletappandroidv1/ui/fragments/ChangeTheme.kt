package com.example.quizletappandroidv1.ui.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentChangeThemeBinding
import com.example.quizletappandroidv1.utils.Theme

class ChangeTheme : Fragment() {
    private lateinit var binding: FragmentChangeThemeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog

    //    private lateinit var apiService: ApiService
    private var darkMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeThemeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        val btnApplyTheme: Button = view.findViewById(R.id.btnApplyTheme)

        btnApplyTheme.setOnClickListener {
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioDark -> {
                    darkMode = true
                    Theme.setThemePreference(requireContext(), "dark")
//                    changeTheme(Helper.getDataUserId(this), darkMode)
                    changeThemeNoUseApi("dark")
                }


                R.id.radioLight -> {
                    darkMode = false
                    Theme.setThemePreference(requireContext(), "light")
//                    changeTheme(Helper.getDataUserId(this), darkMode)
                    changeThemeNoUseApi("light")
                }

                R.id.radioSystemDefault -> {
                    Theme.setThemePreference(requireContext(), "default")
//                    changeTheme(Helper.getDataUserId(this), darkMode)
                    changeThemeNoUseApi("default")
                }

            }
        }

        binding.txtBack.setOnClickListener {
            requireActivity().finish()
        }

//        val dataSetting = UserM.getUserData()
//        dataSetting.observe(this) {
//            themeStatus = it.setting.darkMode
//            when (themeStatus) {
//                false -> binding.radioLight.isChecked = true
//                true -> binding.radioDark.isChecked = true
//            }
//        }

        sharedPreferences =
            requireContext().getSharedPreferences("changeTheme", Context.MODE_PRIVATE)
        when (sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)) {
            1 -> binding.radioLight.isChecked = true
            2 -> binding.radioDark.isChecked = true
            -1 -> binding.radioSystemDefault.isChecked = true
        }
    }

    private fun setThemeMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        // Set the theme change flag in SharedPreferences
        sharedPreferences.edit().putBoolean("themeChange", true).apply()
        with(sharedPreferences.edit()) {
            putInt("theme", mode)
            apply()
        }
    }


    private fun changeThemeNoUseApi(theme: String) {
//        lifecycleScope.launch {
//            try {
//                when (theme) {
//                    "dark" -> setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
//                    "light" -> setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
//                    else -> setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//                }
////                restartApp()
//                runOnUiThread {
//                    recreate()
//                }
//
//            } catch (e: Exception) {
//                Log.e("ThemeChange", "Exception: ${e.message}")
//            }
//        }

    }

}