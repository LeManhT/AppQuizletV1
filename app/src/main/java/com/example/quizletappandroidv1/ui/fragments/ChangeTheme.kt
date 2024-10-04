package com.example.quizletappandroidv1.ui.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentChangeThemeBinding
import com.example.quizletappandroidv1.utils.Theme
import kotlinx.coroutines.launch

class ChangeTheme : Fragment() {
    private lateinit var binding: FragmentChangeThemeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private val mainHandler = Handler(Looper.getMainLooper())


    private var darkMode: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeThemeBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        val btnApplyTheme: Button = view.findViewById(R.id.btnApplyTheme)
        val toolbar = binding.toolbarChangeTheme
        (requireActivity() as AppCompatActivity)
            .setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )
        btnApplyTheme.setOnClickListener {
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioDark -> {
                    darkMode =
                        true
                    Theme.setThemePreference(requireContext(), "dark")
                    changeThemeNoUseApi("dark")
                }

                R.id.radioLight -> {
                    darkMode =
                        false
                    Theme.setThemePreference(requireContext(), "light")
                    changeThemeNoUseApi("light")
                }

                R.id.radioSystemDefault -> {
                    Theme.setThemePreference(
                        requireContext(),
                        "default"
                    )
                    changeThemeNoUseApi("default")
                }
            }
        }
        sharedPreferences = requireContext().getSharedPreferences(
            "changeTheme",
            Context.MODE_PRIVATE
        )
        when (sharedPreferences.getInt(
            "theme",
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )) {
            1 -> binding.radioLight.isChecked = true
            2 -> binding.radioDark.isChecked = true
            -1 -> binding.radioSystemDefault.isChecked = true
        }
    }

    private fun setThemeMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        sharedPreferences.edit().putBoolean(
            "themeChange",
            true
        ).apply()
        with(sharedPreferences.edit()) {
            putInt("theme", mode)
            apply()
        }
    }

    private fun changeThemeNoUseApi(theme: String) {
        lifecycleScope.launch {
            try {
                when (theme) {
                    "dark" -> setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
                    "light" -> setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
                    else -> setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                // Use the Handler to post to the main thread
                mainHandler.post {
                    requireActivity().recreate()
                }
            } catch (e: Exception) {
                Log.e("ThemeChange", "Exception: ${e.message}")
            }
        }
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