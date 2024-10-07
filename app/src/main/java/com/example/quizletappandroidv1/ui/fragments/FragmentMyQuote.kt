package com.example.quizletappandroidv1.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.QuoteLocalAdapter
import com.example.quizletappandroidv1.adapter.QuoteLocalAdapter.OnQuotifyLocalListener
import com.example.quizletappandroidv1.databinding.FragmentMyQuoteBinding
import com.example.quizletappandroidv1.viewmodel.quote.QuoteViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentMyQuote : Fragment() {
    private lateinit var quoteAdapter: QuoteLocalAdapter
    private lateinit var binding: FragmentMyQuoteBinding
    private val myViewModel: QuoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyQuoteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyApplication.userId?.let { myViewModel.getLocalQuotes(it) }

        quoteAdapter = QuoteLocalAdapter(binding.rvQuote, object : OnQuotifyLocalListener {
            override fun handleShareQuote(position: Int) {
                val quoteText = quoteAdapter.getQuoteText(position)
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, quoteText.joinToString("\n"))
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, null))
            }

            override fun handleDeleteQuote(quoteId: Long) {
                MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.warning))
                    .setMessage(resources.getString(R.string.confirm_delete_quote))
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }.setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                        myViewModel.deleteQuote(quoteId)
                    }.show()
            }

            override fun handleNextQuote(position: Int) {
                quoteAdapter.handleNextQuote(position)
            }

            override fun handlePrevQuote(position: Int) {
                quoteAdapter.handlePrevQuote(position)
            }
        })
        binding.rvQuote.adapter = quoteAdapter
        binding.rvQuote.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val snapHelperQuotify = PagerSnapHelper()
        snapHelperQuotify.attachToRecyclerView(binding.rvQuote)

        myViewModel.localQuote.observe(viewLifecycleOwner) {
            if (myViewModel.localQuote.value?.isEmpty() == true) {
                binding.rvQuote.visibility = View.GONE
                binding.layoutNoData.visibility = View.VISIBLE
            } else {
                binding.rvQuote.visibility = View.VISIBLE
                binding.layoutNoData.visibility = View.GONE
                quoteAdapter.updateQuotes(it)
            }
            quoteAdapter.notifyDataSetChanged()
        }

        binding.txtCreateNewSet.setOnClickListener {
            val i = Intent(requireContext(), CreateQuote::class.java)
            startActivity(i)
        }

        binding.txtBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

}
