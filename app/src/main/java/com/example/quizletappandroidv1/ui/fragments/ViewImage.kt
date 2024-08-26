package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentViewImageBinding
import com.github.chrisbanes.photoview.PhotoView

class ViewImage : Fragment() {
    private lateinit var binding: FragmentViewImageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photoView: PhotoView = binding.photoView

        // Đặt ảnh vào PhotoView
        Glide.with(this).load(R.raw.owl_default_avatar).into(photoView)

        binding.imgClose.setOnClickListener {
            requireActivity().finish()
        }

        photoView.setOnClickListener {
            requireActivity().finish()
        }
    }

}