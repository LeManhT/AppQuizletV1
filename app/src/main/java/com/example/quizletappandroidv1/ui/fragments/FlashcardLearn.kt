package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.adapter.LearnFlashcardAdapter
import com.example.quizletappandroidv1.databinding.FragmentFlashcardLearnBinding
import com.example.quizletappandroidv1.listener.LearnCardClick
import com.example.quizletappandroidv1.models.FlashCardModel
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

interface OnClickButton {
    fun handleClickShuffle()

    fun handleClickPlayAudio()

    fun handleClickModeDisplay()

    fun handleResetCard()
}
@AndroidEntryPoint
class FlashcardLearn : Fragment(), OnClickButton, LearnFlashcardAdapter.onLearnCardClick,
    TextToSpeech.OnInitListener {
    private var _binding: FragmentFlashcardLearnBinding? = null
    private val binding get() = _binding!!
    private var currentPosition: Int = 0
    private lateinit var manager: CardStackLayoutManager
    private lateinit var listCards: MutableList<FlashCardModel>
    private lateinit var copiedArr: MutableList<FlashCardModel>
    private var isShuffle: Boolean? = false
    private var isPlayAudio: Boolean? = false
    private lateinit var settingFragment: LearnFlashcardSetting
    private lateinit var adapterLearn: LearnFlashcardAdapter
    private lateinit var textToSpeech: TextToSpeech
    private var isFront: Boolean = true

    private val handler = Handler(Looper.getMainLooper())
    private val autoPlayDelay = 2000L // Delay between swipes
    private var isAutoPlay = false

    private val documentViewModel: DocumentViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val args: FlashcardLearnArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFlashcardLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        MyApplication.userId?.let { userViewModel.getUserData(it) }
        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(requireContext(), this)

        listCards = mutableListOf()

//        // Retrieve listCards from arguments (replace with actual data retrieval)
//        val jsonList = arguments?.getString("listCard")
//        listCards = Gson().fromJson(jsonList, object : TypeToken<List<FlashCardModel>>() {}.type)
//        copiedArr = listCards.toMutableList()

        userViewModel.userData.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                val targetId = args.setId
                val studySetTarget = it.documents.studySets.find { studySet ->
                    studySet.id == targetId
                }
                if (studySetTarget != null) {
                    listCards.clear()
                    listCards.addAll(studySetTarget.cards)
                    adapterLearn.updateData(studySetTarget.cards)
                }

            }.onFailure { }
        }

        adapterLearn = LearnFlashcardAdapter(object : LearnCardClick {
            override fun handleLearnCardClick(position: Int, cardItem: FlashCardModel) {
                cardItem.isUnMark = cardItem.isUnMark?.not() ?: true
            }
        })

        adapterLearn.setOnLearnCardClick(this)
        init()

        binding.swipeStack.layoutManager = manager
        binding.swipeStack.itemAnimator = DefaultItemAnimator()
        binding.swipeStack.adapter = adapterLearn

        binding.swipeStack.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            currentPosition = manager.topPosition
        }

        binding.iconBack.setOnClickListener {
            if (currentPosition > 0) {
                manager.scrollToPosition(currentPosition - 1)
                binding.txtCount.text = "${currentPosition}/${listCards.size}"
            } else {
                Toast.makeText(requireContext(), "No more previous cards", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.iconClose.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.learnIconSetting.setOnClickListener {
            showSettingBottomsheet()
        }

        binding.iconAutoPlay.setOnClickListener {
            isAutoPlay = !isAutoPlay
            if (isAutoPlay) {
                startAutoPlay()
            } else {
                stopAutoPlay()
            }
        }
    }

    private fun showSettingBottomsheet() {
//        settingFragment.show(childFragmentManager, "")
    }

    override fun handleClickModeDisplay() {
//        isFront = isFront?.not() ?: true
//        val btnToggleMode = settingFragment.dialog?.findViewById<AppCompatButton>(R.id.btnToggleMode)
//        if (isFront) {
//            btnToggleMode?.text = resources.getString(R.string.term)
//            listCards.forEach { it.isUnMark = false }
//        } else {
//            btnToggleMode?.text = resources.getString(R.string.definition)
//            listCards.forEach { it.isUnMark = true }
//        }
//        settingFragment.setIsFront(isFront)
//        adapterLearn.notifyDataSetChanged()
    }

    override fun handleClickShuffle() {
        isShuffle = isShuffle?.not() ?: true
        if (isShuffle == true) {
            listCards.shuffle()
        } else {
            listCards.clear()
            listCards.addAll(copiedArr)
        }
        adapterLearn.notifyDataSetChanged()
    }

    override fun handleClickPlayAudio() {
        isPlayAudio = isPlayAudio?.not() ?: false
    }

    private fun init() {
        binding.txtCount.text = "${1}/${listCards.size}"
        manager = CardStackLayoutManager(requireContext(), object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}
            override fun onCardSwiped(direction: Direction?) {
                currentPosition = manager.topPosition
                binding.txtCount.text =
                    "${if (currentPosition + 1 > listCards.size) currentPosition else currentPosition + 1}/${listCards.size}"
                if (manager.topPosition == listCards.size) {
                    binding.swipeStack.visibility = View.GONE
                    binding.layoutLearnedFull.visibility = View.VISIBLE
                    binding.learnBottomBtn.visibility = View.GONE
                    binding.toolbar.visibility = View.GONE
                }
            }

            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View?, position: Int) {}
            override fun onCardDisappeared(view: View?, position: Int) {}
        })
        manager.setVisibleCount(3)
        manager.setTranslationInterval(0.6f)
        manager.setScaleInterval(0.8f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setSwipeThreshold(0.3f)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(requireContext(), "Language not supported.", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "Initialization failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakOut(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        textToSpeech.shutdown()
        isFront = true
        isShuffle = false
        isAutoPlay = false
        isPlayAudio = false
        _binding = null
    }

    override fun handleClickAudio(term: String) {
        speakOut(term)
    }

    private fun startAutoPlay() {
        handler.postDelayed(autoPlayRunnable, autoPlayDelay)
    }

    private fun stopAutoPlay() {
        handler.removeCallbacks(autoPlayRunnable)
    }

    private val autoPlayRunnable = object : Runnable {
        override fun run() {
            if (isAutoPlay) {
                swipeNextCard()
                handler.postDelayed(this, autoPlayDelay)
            }
        }
    }


    private fun swipeNextCard() {
        val nextPosition = currentPosition + 1
        if (nextPosition < listCards.size) {
            manager.scrollToPosition(nextPosition)
            binding.txtCount.text = "${nextPosition + 1}/${listCards.size}"
        } else {
            isAutoPlay = false
            stopAutoPlay()
        }
    }

    override fun handleResetCard() {
        requireActivity().recreate()
//        settingFragment.dismiss()
    }
}