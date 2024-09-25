package com.example.quizletappandroidv1.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentSettingsBinding
import com.example.quizletappandroidv1.ui.activity.AuthActivity
import com.example.quizletappandroidv1.utils.SharedPreferencesManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Settings : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    lateinit var dialog_update_email: AlertDialog
    private var currentPass: String = ""
    private var currentPassHash: String = ""
    private var currentEmail: String = ""
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesTheme: SharedPreferences
    private var currentPoint: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(
            layoutInflater, container, false
        )
        val toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity)
            .setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view, savedInstanceState
        )
        sharedPreferences = requireContext().getSharedPreferences(
            "languageChoose", Context.MODE_PRIVATE
        )
        val languageDisplay = sharedPreferences.getString(
            "languageDisplay", "English"
        )
        if (languageDisplay == "English") {
            binding.txtDisplayLanguage.text = resources.getString(R.string.default_n)
        } else {
            binding.txtDisplayLanguage.text = languageDisplay
        }
        sharedPreferencesTheme = requireContext().getSharedPreferences(
            "changeTheme", Context.MODE_PRIVATE
        )
        when (sharedPreferencesTheme.getInt("theme", -1)) {
            1 -> binding.txtThemeMode.text = resources.getString(R.string.light)
            2 -> binding.txtThemeMode.text = resources.getString(R.string.dark)
            -1 -> binding.txtThemeMode.text = resources.getString(R.string.system_default)
        }
        binding.layoutChangeLanguage.setOnClickListener { findNavController().navigate(R.id.action_settings_to_changeLanguage) }
        binding.layoutChangeTheme.setOnClickListener {
            findNavController().navigate(
                R.id.action_settings_to_changeTheme
            )
        }
        binding.changeEmail.setOnClickListener { findNavController().navigate(R.id.action_settings_to_changeEmail) }
        binding.layoutPolicy.setOnClickListener {
            val privacyPolicyUrl = "https://quizlet.com/privacy"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
            startActivity(intent)
        }
        binding.layoutTermAndService.setOnClickListener {
            val termsOfServiceUrl = "https://quizlet.com/tos"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsOfServiceUrl))
            startActivity(intent)
        }
        binding.layoutHelpCenter.setOnClickListener {
            val termsOfServiceUrl = "https://help.quizlet.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsOfServiceUrl))
            startActivity(intent)
        }
        binding.changePassword.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_changePassword)
        }
        binding.layoutLogout.setOnClickListener {
            showConfirmLogout(
                requireContext()
            )
        }
        if (currentPoint > 7000) {
            binding.btnPremium.visibility = View.GONE
            binding.txtVerified.visibility = View.VISIBLE
            binding.btnPremium.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.premium_account))
                    .setMessage(resources.getString(R.string.premium_account_desc))
                    .setNegativeButton(resources.getString(R.string.close)) { dialog, which -> run { dialog.dismiss() } }
                    .show()
            }
        } else {
            binding.btnPremium.visibility = View.VISIBLE
            binding.txtVerified.visibility = View.GONE
            binding.btnPremium.setOnClickListener {
                findNavController().navigate(R.id.action_settings_to_quizletPlus)
            }
        }
    }

    private fun showConfirmLogout(context: Context) {
        MaterialAlertDialogBuilder(context).setTitle(resources.getString(R.string.confirm_logout))
            .setMessage(resources.getString(R.string.are_u_sure_logout))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which -> logOut() }
            .show()
    }

    private fun logOut() {
        val intent = Intent(
            requireContext(), AuthActivity::class.java
        )
        SharedPreferencesManager.clearAllPreferences(requireContext())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(
            intent
        )
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
