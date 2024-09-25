package com.example.quizletappandroidv1.ui.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentAddBinding
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Add : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddBinding
    private lateinit var progressDialog: ProgressDialog
    private val documentViewModel: DocumentViewModel by viewModels()

    override
    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        binding.layoutSet.setOnClickListener {
            findNavController().navigate(R.id.action_add3_to_createSet)
        }
        binding.layoutFolder.setOnClickListener {
            Log.d("CreateFolder", "Vào")
            showCustomDialog(
                resources.getString(R.string.add_folder),
                "",
                resources.getString(R.string.folder_name),
                resources.getString(R.string.desc_optional)
            )
        }
        binding.layoutClass.setOnClickListener {
            Toast.makeText(
                context,
                resources.getString(R.string.create_class),
                Toast.LENGTH_SHORT
            ).show()
        }

        documentViewModel.folderResponse.observe(viewLifecycleOwner) {

        }

    }

    private fun showCustomDialog(
        title: String,
        content: String,
        edtPlaceholderFolderName: String,
        edtPlaceholderDesc: String
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 0, 0, 0)

        if (content.isNotEmpty()) {
            val textContent = TextView(context)
            textContent.text = content
            textContent.setPadding(10, 0, 10, 0)
            layout.addView(textContent)
        }

        // Tạo EditText cho tên thư mục
        val editTextFolder = createEditTextWithCustomBottomBorder(edtPlaceholderFolderName)
        layout.addView(editTextFolder)

        // Tạo EditText cho mô tả
        val editTextDesc = createEditTextWithCustomBottomBorder(edtPlaceholderDesc)
        layout.addView(editTextDesc)

        builder.setView(layout)

        // Nút "OK"
        builder.setPositiveButton("OK") { dialog, _ ->
            val inputText = editTextFolder.text.toString()
            val description = editTextDesc.text.toString()
            val body = JsonObject().apply {
                addProperty(resources.getString(R.string.createFolderNameField), inputText)
                addProperty(resources.getString(R.string.descriptionField), description)
            }

            MyApplication.userId?.let { documentViewModel.createNewFolder(it, body) }

            // createNewFolder(inputText, description, Helper.getDataUserId(requireContext()))
            dialog.dismiss()
        }

        // Nút "Cancel"
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // Hiển thị dialog
        builder.create().show()
    }


    private fun createEditTextWithCustomBottomBorder(hint: String): EditText {
        val editText =
            EditText(context)
        editText.hint = hint
        val defaultBottomBorder =
            GradientDrawable()
//        defaultBottomBorder.setColor(Color.BLACK) // Set your desired border color
        defaultBottomBorder.setSize(0, 5)
        // Set your desired border height
        val focusBottomBorder =
            GradientDrawable()
//        focusBottomBorder.setColor(Color.BLUE) // Set your desired focus border color
        focusBottomBorder.setSize(0, 10) // Set your desired focus border height
        // Set layout parameters with margins
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 10
        layoutParams.bottomMargin = 10
        editText.layoutParams = layoutParams
        editText.background = defaultBottomBorder
        // Set a listener to change the border color when focused //
        editText.setOnFocusChangeListener { view, hasFocus ->
            editText.background =
                if (hasFocus) focusBottomBorder else defaultBottomBorder
        }
        return editText
    }
}
