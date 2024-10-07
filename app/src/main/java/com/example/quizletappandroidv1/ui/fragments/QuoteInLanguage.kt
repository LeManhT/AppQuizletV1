package com.example.quizletappandroidv1.ui.fragments

import QuotifyAdapter
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentQuoteInLanguageBinding
import com.example.quizletappandroidv1.entity.QuoteEntity
import com.example.quizletappandroidv1.viewmodel.quote.QuoteViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuoteInLanguage : Fragment(), QuotifyAdapter.OnQuotifyListener {
    private var _binding: FragmentQuoteInLanguageBinding? = null
    private val binding get() = _binding!!
    private lateinit var quoteAdapter: QuotifyAdapter
    private var progressDialog: ProgressDialog? = null
    private val myViewModel: QuoteViewModel by viewModels()
    private var currentPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuoteInLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myViewModel.getRemoteQuote()

        binding.txtBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        quoteAdapter = QuotifyAdapter(binding.rvQuote)
        binding.rvQuote.adapter = quoteAdapter
        binding.rvQuote.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val snapHelperQuotify = PagerSnapHelper()
        snapHelperQuotify.attachToRecyclerView(binding.rvQuote)
        myViewModel.quotes.observe(viewLifecycleOwner) {
            quoteAdapter.updateQuotes(it.results)
        }

        myViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressDialog =
                    ProgressDialog.show(context, null, getString(R.string.loading_data))
            } else {
                progressDialog?.dismiss()
            }
        }

        binding.txtNextQuote.setOnClickListener {
            currentPosition = quoteAdapter.handleNextQuote(currentPosition)
        }

        binding.txtPrevQuote.setOnClickListener {
            currentPosition = quoteAdapter.handlePrevQuote(currentPosition)
        }

        quoteAdapter.setOnQuoteShareListener(this)

        binding.txtCreateNewQuote.setOnClickListener {
            findNavController().navigate(R.id.action_quoteInLanguage_to_createQuote)
        }

        binding.txtSaved.setOnClickListener {
            findNavController().navigate(R.id.action_quoteInLanguage_to_fragmentMyQuote)
        }
    }

    override fun handleShareQuote(position: Int) {
        val quoteText = quoteAdapter.getQuoteText(position)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, quoteText.joinToString("\n"))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }

    override fun handleAddToMyQuote(position: Int) {
        val quoteText = quoteAdapter.getQuoteText(position)
        val contentQuote = quoteText[0].toString()
        val authorQuote = quoteText[1].toString()
        myViewModel.insertQuote(
            QuoteEntity(
                0, "", contentQuote, authorQuote, "", 0, MyApplication.userId
            )
        )
        Snackbar.make(
            binding.txtSaved,
            resources.getString(R.string.add_to_your_quote_success),
            Snackbar.LENGTH_LONG
        )
            .setAction(resources.getString(R.string.view)) {
                findNavController().navigate(R.id.action_quoteInLanguage_to_fragmentMyQuote)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}