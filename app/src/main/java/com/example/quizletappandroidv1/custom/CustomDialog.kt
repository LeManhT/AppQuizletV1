package com.example.quizletappandroidv1.custom

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.quizletappandroidv1.R

class CustomDialog(
    private val context: Context
) {
    private lateinit var dialog: Dialog
    private lateinit var iconView: ImageView
    private lateinit var titleView: TextView
    private lateinit var messageView: TextView
    private lateinit var buttonView: Button

    @SuppressLint("InflateParams")
    fun showDialog(
        icon: Drawable,
        title: String,
        message: String,
        buttonText: String,
        buttonAction: () -> Unit
    ) {
        // Create the dialog
        dialog = Dialog(context)
        val view = LayoutInflater.from(context).inflate(
            R.layout.custom_result_dialog,
            null
        )
        dialog.setContentView(view)        // Initialize views
        iconView = view.findViewById(R.id.dialog_icon)
        titleView = view.findViewById(R.id.dialog_title)
        messageView = view.findViewById(R.id.dialog_message)
        buttonView = view.findViewById(R.id.dialog_button)        // Set dialog content
        iconView.setImageDrawable(icon)
        titleView.text = title
        messageView.text = message
        buttonView.text = buttonText
        // Set button action
        buttonView.setOnClickListener {
            buttonAction.invoke()
            dialog.dismiss() // Close dialog after button click
        }        // Set dialog size to 70% of screen height
        val displayMetrics = DisplayMetrics()
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val screenWidth =
            displayMetrics.widthPixels
        // Set dialog width to match parent and height to 70% of the screen
        dialog.window?.setLayout(
            (screenWidth * 0.9).toInt(),   // 90% chiều rộng màn hình
            (screenHeight * 0.35).toInt()   // 70% chiều cao màn hình
        )        // Show the dialog//
        dialog.show()
    }
}