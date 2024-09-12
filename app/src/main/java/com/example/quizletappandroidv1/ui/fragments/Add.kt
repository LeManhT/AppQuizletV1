package com.example.quizletappandroidv1.ui.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Add : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddBinding
    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
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
            val intent = Intent(context, CreateSet::class.java)
            startActivity(intent)
        }
        binding.layoutFolder.setOnClickListener {
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
    }

    private fun showCustomDialog(
        title: String,
        content: String,
        edtPlaceholderFolderName: String,
        edtPlaceholderDesc: String
    ) {
        val builder =
            AlertDialog.Builder(requireContext())
        builder.setTitle(title)         // Tạo layout cho dialog

        val layout =
            LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 0, 40, 0)
        if (!content.isEmpty()) {
            //            builder.setMessage(content)
            val textContent =
                TextView(context)
            textContent.setText(content)
            textContent.setPadding(10, 0, 10, 0)
            layout.addView(textContent)
        }         // Tạo EditText
        val editTextFolder = createEditTextWithCustomBottomBorder(edtPlaceholderFolderName)
        editTextFolder.hint = edtPlaceholderFolderName
        layout.addView(editTextFolder)
        val editTextDesc = createEditTextWithCustomBottomBorder(edtPlaceholderDesc)
        editTextDesc.hint = edtPlaceholderDesc
        layout.addView(editTextDesc)
        builder.setView(layout)
        builder.setPositiveButton("OK") { dialog, _ ->
            val inputText = editTextFolder.text.toString()
            val description = editTextDesc.text.toString()
            // Xử lý dữ liệu từ EditText sau khi người dùng nhấn OK
            // Ví dụ: Hiển thị nó hoặc thực hiện các tác vụ khác             // ở đây //
//            createNewFolder(inputText, description, Helper.getDataUserId(requireContext()))
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            // Xử lý khi người dùng nhấn Cancel
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun createEditTextWithCustomBottomBorder(hint: String): EditText {
        val editText = EditText(context)
        editText.hint = hint
        // Custom drawable for bottom border
        val defaultBottomBorder = GradientDrawable()
        defaultBottomBorder.setColor(Color.BLACK) // Set your desired border color
        defaultBottomBorder.setSize(0, 5) // Set your desired border height
        val focusBottomBorder = GradientDrawable()
        focusBottomBorder.setColor(Color.BLUE) // Set your desired focus border color
        focusBottomBorder.setSize(0, 10) // Set your desired focus border height
        // Set layout parameters with margins
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 10
        layoutParams.bottomMargin = 10
        editText.layoutParams =
            layoutParams //        editText.background = defaultBottomBorder
        // Set a listener to change the border color when focused //
        editText.setOnFocusChangeListener { view, hasFocus ->
            editText.background = if (hasFocus) focusBottomBorder else defaultBottomBorder
        }
        return editText
    }
}