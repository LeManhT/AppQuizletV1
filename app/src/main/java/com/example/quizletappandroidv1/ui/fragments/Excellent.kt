package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentExcellentBinding
import com.example.quizletappandroidv1.models.FlashCardModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Excellent : Fragment() {
    private lateinit var binding: FragmentExcellentBinding
    private val args: ExcellentArgs by navArgs()
    private lateinit var listCards: MutableList<FlashCardModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExcellentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val listCardTest = args.listCardTest
        val countTrue = args.countTrue
        val countFalse = args.countFalse
        val listSize = args.listSize

        val percent = (countTrue.toDouble() / listSize.toDouble()) * 100
        listCards =
            Gson().fromJson(listCardTest, object : TypeToken<List<FlashCardModel>>() {}.type)
        binding.txtTotalTrue.text = countTrue.toString()
        binding.txtTotalFalse.text = countFalse.toString()


        binding.customProgressBar.setProgress(percent.toInt(), percent.toInt().toString())

        if (percent <= 50) {
            binding.tooBad.visibility = View.VISIBLE
            binding.keepTrying.visibility = View.GONE
            binding.excellent.visibility = View.GONE

            binding.txtTooBadText.visibility = View.VISIBLE
            binding.txtKeepTryingText.visibility = View.GONE
            binding.txtExcellentText.visibility = View.GONE
        } else if (percent > 50 && percent < 85) {
            binding.keepTrying.visibility = View.VISIBLE
            binding.tooBad.visibility = View.GONE
            binding.excellent.visibility = View.GONE

            binding.txtTooBadText.visibility = View.GONE
            binding.txtKeepTryingText.visibility = View.VISIBLE
            binding.txtExcellentText.visibility = View.GONE
        } else {
            binding.excellent.visibility = View.VISIBLE
            binding.tooBad.visibility = View.GONE
            binding.keepTrying.visibility = View.GONE

            binding.txtExcellentText.visibility = View.VISIBLE
            binding.txtTooBadText.visibility = View.GONE
            binding.txtKeepTryingText.visibility = View.GONE
        }

        binding.btnGoHome.setOnClickListener {
            findNavController().navigate(R.id.action_excellent2_to_fragmentLibrary2)
        }
    }


}