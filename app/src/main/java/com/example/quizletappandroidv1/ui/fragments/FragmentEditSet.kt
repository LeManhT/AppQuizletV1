package com.example.quizletappandroidv1.ui.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.CreateSetItemAdapter
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentEditSetBinding
import com.example.quizletappandroidv1.models.CreateSetRequest
import com.example.quizletappandroidv1.models.FlashCardModel
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Collections
import java.util.Locale

@AndroidEntryPoint
class FragmentEditSet : Fragment(), CreateSetItemAdapter.OnIconClickListener {
    private lateinit var binding: FragmentEditSetBinding
    private lateinit var progressDialog: ProgressDialog
    private var listSet = mutableListOf<FlashCardModel>() // Declare as a class-level property
    private lateinit var adapterCreateSet: CreateSetItemAdapter // Declare adapter as a class-level property
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private var speechRecognitionPosition: Int = -1
    private val documentViewModel: DocumentViewModel by viewModels()
    private val args: FragmentEditSetArgs by navArgs<FragmentEditSetArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditSetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        documentViewModel.getOneStudySet(args.editSetId)

        listSet = mutableListOf()
        adapterCreateSet = CreateSetItemAdapter()
        binding.RvCreateSets.layoutManager = LinearLayoutManager(requireContext())
        binding.RvCreateSets.adapter = adapterCreateSet
        adapterCreateSet.setOnIconClickListener(this)

        binding.addNewCard.setOnClickListener {
            val newItem = FlashCardModel()
            newItem.id = ""
            listSet.add(newItem)
            adapterCreateSet.notifyItemInserted(listSet.size - 1)

            binding.RvCreateSets.scrollToPosition(listSet.size - 1)

            binding.createSetScrollView.post {
                binding.createSetScrollView.smoothScrollTo(0, binding.RvCreateSets.bottom)
            }

        }
        documentViewModel.oneStudySet.observe(viewLifecycleOwner) {
//            val targetSet = it.find { set ->
//                set.id == args.editSetId
//            }
            Log.d("StudySetDetailEdit", "Target ID: $it")
                binding.txtSetName.text =
                    Editable.Factory.getInstance().newEditable(it.name)
                adapterCreateSet.updateListSet(it.cards)
                listSet.clear()
                listSet.addAll(it.cards)
            adapterCreateSet.notifyDataSetChanged()
        }
        binding.iconTick.setOnClickListener {
            val name = binding.txtSetName.text.toString()
            val desc = binding.txtDescription.toString()

            if (listSet.isNotEmpty()) {
                // Kiểm tra xem có bất kỳ item nào trong allNewCards có rỗng không
                val isEmptyItemExist =
                    listSet.any { it.term?.isEmpty() == true || it.definition?.isEmpty() == true }

                if (!isEmptyItemExist) {
                    if (name.isEmpty()) {
                        CustomToast(requireContext()).makeText(
                            requireContext(),
                            resources.getString(R.string.set_name_is_required),
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    } else {
                        updateStudySet(
                            name,
                            desc,
                            listSet
                        )
                    }
                } else {
                    CustomToast(requireContext()).makeText(
                        requireContext(),
                        "Please fill in all flashcards before updating.",
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                }
            } else {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    "Please add at least 4 flashcards.",
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }

        documentViewModel.studySetResponse.observe(viewLifecycleOwner) {
            if (it) {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    resources.getString(R.string.update_study_set_success),
                    CustomToast.LONG,
                    CustomToast.SUCCESS
                ).show()
            } else {
                Timber.e("Update study set failed")
            }
        }

    }


    private fun updateStudySet(
        studySetName: String,
        studySetDesc: String,
        dataSet: List<FlashCardModel>
    ) {
        lifecycleScope.launch {
            showLoading(resources.getString(R.string.updating_study_set))
            val body = CreateSetRequest(
                name = studySetName,
                description = studySetDesc,
                allNewCards = dataSet
            )
            MyApplication.userId?.let { documentViewModel.updateStudySet(it, args.editSetId, body) }
        }
    }

    private fun validateSetName(name: String): Boolean {
        var errMess: String? = null
        if (name.trim().isEmpty()) {
            errMess = resources.getString(R.string.studyset_name_required)
        }
        if (errMess != null) {
            binding.layoutSetName.apply {
                isErrorEnabled = true
                error = errMess
            }

        }
        return errMess == null
    }

    private fun setDragDropItem(list: MutableList<FlashCardModel>, recyclerView: RecyclerView) {
        val simpleCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    Collections.swap(list, fromPos, toPos)
                    recyclerView.adapter!!.notifyItemMoved(fromPos, toPos)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // Xóa mục khi vuốt sang trái hoặc phải
                    val swipedPosition = viewHolder.adapterPosition
                    list.removeAt(swipedPosition)
                    recyclerView.adapter!!.notifyItemRemoved(swipedPosition)
                }

                override fun onChildDraw(
                    c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    recyclerView.invalidate()
                }
            }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onIconClick(position: Int) {
        speechRecognitionPosition = position
        startSpeechRecognition(position)
    }

    override fun onDeleteClick(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            listSet.removeAt(position)
            adapterCreateSet.notifyItemRemoved(position)
            adapterCreateSet.notifyDataSetChanged()
        }
    }

    override fun onAddNewCard(position: Int) {
        val newItem = FlashCardModel()
        newItem.id = ""
        listSet.add(position + 1, newItem)
        adapterCreateSet.notifyItemInserted(position + 1)
        adapterCreateSet.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val matches: ArrayList<String>? = data?.getStringArrayListExtra(
            RecognizerIntent.EXTRA_RESULTS
        )


        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val position = speechRecognitionPosition

            if (position != -1) {
                val speechResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                if (!speechResults.isNullOrEmpty()) {
                    val spokenText = speechResults[0]
                    if (adapterCreateSet.getIsDefinition() == true) {
                        updateRecyclerViewItemDefinition(position, spokenText)
                    } else {
                        updateRecyclerViewItemTerm(position, spokenText)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No speech results found",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Log.e("onActivityResult", "Position is not available in the intent")
            }
            speechRecognitionPosition = -1
        }
    }


    private fun updateRecyclerViewItemTerm(position: Int, spokenText: String?) {
        if (position < listSet.size && spokenText != null) {
            val item = listSet[position]
            item.term = spokenText
            adapterCreateSet.notifyItemChanged(position)
        }
    }

    private fun updateRecyclerViewItemDefinition(position: Int, spokenText: String?) {
        if (position < listSet.size && spokenText != null) {
            val item = listSet[position]
            item.definition = spokenText
            adapterCreateSet.notifyItemChanged(position)
        }
    }

    private fun startSpeechRecognition(position: Int) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        intent.putExtra(RecognizerIntent.EXTRA_ORIGIN, position)


        Log.d("startSpeechRecognition", "Intent extras: ${intent.extras}")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
    }

    private fun btnShowDialogChooseTranslate(position: Int, text: String) {
        val languageIdentifier = LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder().setConfidenceThreshold(0.1f).build()
        )
        Log.d("termValue1", text.toString())
        languageIdentifier.identifyLanguage(text).addOnSuccessListener { languageCode ->
            Log.i("Language Code", languageCode)
            if (languageCode == "und") {
                Toast.makeText(requireContext(), "Can't identify language.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val builder = AlertDialog.Builder(requireContext())
                val itemsArray = when (languageCode) {
                    "en" -> arrayOf("Vietnamese", "Chinese")
                    "vi" -> arrayOf("English", "Chinese")
                    "zh" -> arrayOf("English", "Vietnamese")
                    else -> arrayOf("English", "Vietnamese", "Chinese")
                }
                builder.setTitle("Choose Language Format").setItems(itemsArray) { _, which ->
                    translateText(
                        position,
                        text,
                        languageCode,
                        getTranslateLanguageCode(itemsArray[which])
                    )
                }
                builder.create().show()

            }
        }.addOnFailureListener {

        }
    }

    private fun getTranslateLanguageCode(languageName: String): String {
        return when (languageName.toUpperCase()) {
            "ENGLISH" -> TranslateLanguage.ENGLISH
            "VIETNAMESE" -> TranslateLanguage.VIETNAMESE
            "CHINESE" -> TranslateLanguage.CHINESE
            else -> TranslateLanguage.ENGLISH // Handle the default case or unsupported language
        }
    }

    private fun translateText(
        position: Int, text: String, sourceLanguage: String, targetLanguage: String
    ) {
        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val options = TranslatorOptions.Builder().setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage).build()
            val dualLanguageTranslator = Translation.getClient(options)
            var conditions = DownloadConditions.Builder().requireWifi().build()
            dualLanguageTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener {
                dualLanguageTranslator.translate(text).addOnSuccessListener { translatedText ->
                    Log.i(
                        "detectL2", "translatedText: $translatedText"
                    )
                    if (adapterCreateSet.getIsDefinitionTranslate() == true) {
                        listSet[position].definition = translatedText
                        adapterCreateSet.notifyDataSetChanged()
                    } else {
                        listSet[position].term = translatedText
                        adapterCreateSet.notifyDataSetChanged()
                    }
                }.addOnFailureListener { exception ->
                    Log.i("detectL3", "translatedText: $exception")
                }
            }.addOnFailureListener { exception ->
                Log.i("exception", "exception: $exception")
            }
//        val modelManager = RemoteModelManager.getInstance()
//        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
//            .addOnSuccessListener { models ->
//                Log.d("modelLan", models.toString())
//            }
//            .addOnFailureListener {
//                // Error.
//            }
//
//        val targetModel = TranslateRemoteModel.Builder(targetLanguage).build()
//        val conditions = DownloadConditions.Builder()
//            .requireWifi()
//            .build()
//        modelManager.download(targetModel, conditions)
//            .addOnSuccessListener {
//                dualLanguageTranslator.translate(text)
//                    .addOnSuccessListener { translatedText ->
//                        Log.i(
//                            "detectL2",
//                            "translatedText: $translatedText"
//                        )
//                    }
//                    .addOnFailureListener { exception ->
//                        Log.i("detectL3", "translatedText: $exception")
//                    }
//            }
//            .addOnFailureListener { exception ->
//
//            }
//
//        modelManager.deleteDownloadedModel(targetModel)
//            .addOnSuccessListener {
//            }
//            .addOnFailureListener {
//                // Error.
//            }
//        lifecycle.addObserver(dualLanguageTranslator)
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onTranslateIconClick(position: Int, currentText: String) {
        if (adapterCreateSet.getIsDefinitionTranslate() == true) {
            adapterCreateSet.notifyDataSetChanged()
            btnShowDialogChooseTranslate(position, currentText)
        } else {
            Log.d("termValue", currentText)
            adapterCreateSet.notifyDataSetChanged()
            btnShowDialogChooseTranslate(position, currentText)
        }
    }

    override fun onChooseImage(position: Int) {
        TODO("Not yet implemented")
    }
}