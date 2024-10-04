//package com.example.quizletappandroidv1.ui.admin
//
//import android.content.Intent
//import android.os.Bundle
//import android.text.Editable
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import com.example.quizletappandroidv1.MainActivity
//import com.example.quizletappandroidv1.R
//import com.example.quizletappandroidv1.databinding.FragmentEditUserBinding
//import com.example.quizletappandroidv1.utils.Helper
//import com.example.quizletappandroidv1.viewmodel.admin.AdminViewModel
//import kotlinx.coroutines.launch
//
//class FragmentEditUser : Fragment(), View.OnFocusChangeListener {
//    private lateinit var binding: FragmentEditUserBinding
//    private val adminViewModel by viewModels<AdminViewModel>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentEditUserBinding.inflate(layoutInflater, container, false)
//
//        binding.edtEditDOBLayout.onFocusChangeListener = this
//        binding.edtEditEmailLayout.onFocusChangeListener = this
//        binding.edtEmail.onFocusChangeListener = this
//        binding.edtDOB.onFocusChangeListener = this
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.btnClose.setOnClickListener {
//            parentFragmentManager.popBackStack()
//        }
//
//        binding.edtEmail.text = Editable.Factory.getInstance().newEditable(contact?.email)
//        binding.edtName.text = Editable.Factory.getInstance().newEditable(contact?.name)
//        binding.edtDOB.text =
//            Editable.Factory.getInstance().newEditable(contact?.phoneNumber)
//        binding.edtEditImageUrl.text =
//            Editable.Factory.getInstance().newEditable(contact?.image)
//
//        binding.btnSaveEditContact.setOnClickListener {
//            val newName = binding.edtName.text.toString()
//            val newPhoneNumber = binding.edtPhoneNumber.text.toString()
//            val newEmail = binding.edtEmail.text.toString()
//            val newImageUrl = binding.edtEditImageUrl.text.toString()
//
//            if (newName == contact?.name.toString() && newPhoneNumber == contact?.phoneNumber.toString() && newEmail == contact?.email.toString() && newImageUrl == contact?.image.toString()) {
//                Toast.makeText(
//                    context, "No changes detected. Data remains unchanged.", Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                if (newName.isNotEmpty() && newPhoneNumber.isNotEmpty() && newEmail.isNotEmpty()) {
//                    if (context?.let { it1 ->
//                            Helper.validateEmail(
//                                it1, newEmail, binding.edtEditEmailLayout
//                            )
//                        } == true) {
//                        val contactEdit = contact?.id?.let { it1 ->
////                            ContactEntity(
////                                it1,
////                                newName,
////                                newPhoneNumber,
////                                newEmail,
////                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS3qQ8nsNWc1RD6-4Tq4iInPTgPwn524Tdgrds-J5J_hQ&s"
////                            )
//                        }
//                        if (contactEdit != null) {
//                            lifecycleScope.launch {
////                                contactViewModel.updateContact(contactEdit)
//                            }
//                        }
//                        val intent = Intent(context, MainActivity::class.java)
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(
//                            context,
//                            resources.getString(R.string.email_not_valid),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                } else {
//                    Toast.makeText(
//                        context,
//                        resources.getString(R.string.you_must_field_all),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//    override fun onFocusChange(v: View?, hasFocus: Boolean) {
//        if (v?.id != null) {
//            when (v.id) {
//                R.id.edtEmail -> {
//                    if (hasFocus) {
//                        if (binding.edtEditEmailLayout.isErrorEnabled) {
//                            binding.edtEditEmailLayout.isErrorEnabled = false
//                        }
//                    } else {
//                        context?.let {
//                            Helper.validateEmail(
//                                it, binding.edtEmail.text.toString(), binding.edtEditEmailLayout
//                            )
//                        }
//                    }
//                }
//
//                R.id.edtDOB -> {
//                    if (hasFocus) {
//                        if (binding.edtEditDOBLayout.isErrorEnabled) {
//                            binding.edtEditDOBLayout.isErrorEnabled = false
//                        }
//                    } else {
//
//                    }
//                }
//            }
//        }
//    }
//}