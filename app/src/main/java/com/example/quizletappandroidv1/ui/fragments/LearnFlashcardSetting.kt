package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.quizletappandroidv1.databinding.FragmentLearnFlashcardSettingBinding

class LearnFlashcardSetting : DialogFragment() {
    private lateinit var binding: FragmentLearnFlashcardSettingBinding
    private var onClickButton: OnClickButton? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLearnFlashcardSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.iconClose.setOnClickListener {
            dismiss()
        }

        binding.layoutOrientation.setOnClickListener {
            onClickButton?.handleClickModeDisplay()
            dismiss()
        }

        binding.layoutShuffle.setOnClickListener {
            onClickButton?.handleClickShuffle()
            dismiss()
        }
        binding.switchShuffle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                onClickButton?.handleClickPlayAudio()
            }
        }

        binding.layoutReset.setOnClickListener {
            onClickButton?.handleResetCard()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    // Assign the OnClickButton listener
    fun setOnClickButtonListener(listener: OnClickButton) {
        onClickButton = listener
    }

}