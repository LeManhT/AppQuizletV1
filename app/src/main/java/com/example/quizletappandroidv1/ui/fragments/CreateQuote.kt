package com.example.quizletappandroidv1.ui.fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentCreateQuoteBinding
import com.example.quizletappandroidv1.entity.QuoteEntity
import com.example.quizletappandroidv1.models.ModelLanguage
import com.example.quizletappandroidv1.utils.FileHelperUtils
import com.example.quizletappandroidv1.viewmodel.quote.QuoteViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.AndroidEntryPoint
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileInputStream
import java.util.Locale
@AndroidEntryPoint
class CreateQuote : Fragment(), TextToSpeech.OnInitListener {
    private lateinit var binding: FragmentCreateQuoteBinding
    private val REQUEST_CODE_SPEECH_INPUT = 150
    private val IMPORT_FILE = 200
    private var targetTitle: String = "Vietnamese"
    private var progressDialog: ProgressDialog? = null
    private var dualLanguageTranslator: Translator? = null
    private var sourceLanguage: String = "en"
    private var targetLanguage: String = "vi"
    private lateinit var textToSpeech: TextToSpeech
    private val listLanguages = arrayListOf<ModelLanguage>()
    private var dataTextType: String = ""

    private val myViewModel: QuoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateQuoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textToSpeech = TextToSpeech(requireContext(), this)
        loadAvailableLanguage()

        binding.imgMic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startSpeechRecognition()
            } else {
                requestSpeechRecognitionPermission()
            }
        }

        binding.txtTranslate.setOnClickListener {
            if (binding.txtTargetLanguage.text.toString().lowercase()
                    .trim() == resources.getString(R.string.target_language).lowercase().trim()
            ) {
                Snackbar.make(
                    binding.txtTranslate,
                    resources.getString(R.string.default_choose_target),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            validateData()
        }

        binding.txtTargetLanguage.setOnClickListener {
            getTargetLanguage()
        }

        binding.imgImport.setOnClickListener {
            showImportAlertDialog(requireContext())
        }


        binding.btnCreateQuote.setOnClickListener {
            val contentQuote = binding.edtTypeText.text.toString()
            val authorQuote = binding.edtAuthorText.text.toString()
            myViewModel.insertQuote(
                QuoteEntity(
                    0, "", contentQuote, authorQuote, "", 0, MyApplication.userId
                )
            )
            findNavController().navigate(R.id.action_createQuote_to_fragmentMyQuote)
        }


    }

    private fun validateData() {
        dataTextType = binding.edtTypeText.text?.trim().toString()

        if (dataTextType.isEmpty()) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.please_type_text_to_translate),
                Toast.LENGTH_LONG
            ).show()
        } else {
            translateText(dataTextType)
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestSpeechRecognitionPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE_SPEECH_INPUT
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSpeechRecognition()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            IMPORT_FILE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchFilePicker()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("requestCode", requestCode.toString())
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == RESULT_OK && data != null) {
                    val speechResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    if (!speechResults.isNullOrEmpty()) {
                        val spokenText = speechResults[0]
                        binding.edtTypeText.text =
                            Editable.Factory.getInstance().newEditable(spokenText)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No speech results found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("onActivityResult", "Position is not available in the intent")
                }
            }

            IMPORT_FILE -> {
                if (resultCode == RESULT_OK) {
                    val selectedFileUri = data?.data
                    Log.d("selectedFileUri", selectedFileUri.toString())
                    if (selectedFileUri != null) {
                        val filePath = FileHelperUtils.getPath(requireContext(), selectedFileUri)
                        if (filePath != null) {
                            if (filePath.endsWith(".docx")) {
                                importWordFile(filePath)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please select a valid Word file",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Snackbar.make(
                                binding.txtTranslate,
                                resources.getString(R.string.there_error_when_import_file),
                                Snackbar.LENGTH_LONG
                            ).setBackgroundTint(resources.getColor(R.color.my_red_snackbar)).show()
                        }
                    }

                }

            }
        }
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                startActivity(installIntent)
            }
        } else {
            Toast.makeText(requireContext(), "Initialization failed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        textToSpeech.shutdown()
        dualLanguageTranslator?.close()
        val modelManager = RemoteModelManager.getInstance()
        val targetModel = TranslateRemoteModel.Builder(targetLanguage).build()
        modelManager.isModelDownloaded(targetModel)
            .addOnSuccessListener { isDownloaded ->
                if (isDownloaded) {
                    modelManager.deleteDownloadedModel(targetModel)
                        .addOnCompleteListener {
                            // Xử lý sau khi mô hình đã được xóa
                        }
                        .addOnFailureListener { exception ->
                            // Xử lý khi xóa mô hình gặp lỗi
                            Log.e("DeleteModel", "Error deleting downloaded model: $exception")
                        }
                }
            }
    }

    private fun showImportAlertDialog(context: Context) {
        MaterialAlertDialogBuilder(context).setTitle(resources.getString(R.string.alert_import_word_title))
            .setMessage(resources.getString(R.string.alert_import_word_desc))

            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }.setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    val permission = Manifest.permission.READ_EXTERNAL_STORAGE
                    if (ContextCompat.checkSelfPermission(
                            requireContext(), permission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        launchFilePicker()
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(), arrayOf(permission), IMPORT_FILE
                        )
                    }
                } else {
                    val permissions = arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )

                    var allPermissionsGranted = true

                    for (permission in permissions) {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(), permission
                            ) == PackageManager.PERMISSION_DENIED
                        ) {
                            allPermissionsGranted = false
                            break
                        }
                    }

                    if (!allPermissionsGranted) {
                        ActivityCompat.requestPermissions(
                            requireActivity(), permissions, IMPORT_FILE
                        )
                    } else {
                        // Launch the file picker
                        launchFilePicker()
                    }
                }

            }.show()
    }

    private fun launchFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // MIME type for Word files
        startActivityForResult(intent, IMPORT_FILE)
    }

    private fun importWordFile(filePath: String): String {
        val document = XWPFDocument(FileInputStream(filePath))
        val paragraphs = document.paragraphs
        val result = StringBuilder()

        for (paragraph in paragraphs) {
            val text = paragraph.text
            Log.d("resultWord0", text.toString())
            result.append(text).append("\n")
        }
        binding.edtTypeText.text = Editable.Factory.getInstance().newEditable(result)
        Log.d("resultWord", result.toString())
        return result.toString()
    }

    override fun onStop() {
        super.onStop()
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    private fun detectLanguage(text: String) {
        val languageIdentifier = LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder().setConfidenceThreshold(0.1f).build()
        )
        languageIdentifier.identifyLanguage(text).addOnSuccessListener { languageCode ->
            if (languageCode == "und") {
                sourceLanguage = "und"
                Toast.makeText(requireContext(), "Can't identify language.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                sourceLanguage = languageCode.toString()
            }
        }.addOnFailureListener {

        }
    }

    private fun translateText(
        text: String
    ) {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.INTERNET
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                detectLanguage(text)
                if (sourceLanguage == "und") {
                    Snackbar.make(
                        binding.txtTranslate, R.string.cannot_indentify_lan, Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    val options = TranslatorOptions.Builder().setSourceLanguage(sourceLanguage)
                        .setTargetLanguage(targetLanguage).build()
                    dualLanguageTranslator = Translation.getClient(options)
                    val conditions = DownloadConditions.Builder().requireWifi().build()
                    showLoading(resources.getString(R.string.please_wait_to_load_model))
                    dualLanguageTranslator!!.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener {
                            dualLanguageTranslator!!.translate(text)
                                .addOnSuccessListener { translatedText ->
                                    progressDialog?.dismiss()
                                    binding.edtTypeText.text =
                                        Editable.Factory.getInstance().newEditable(translatedText)
                                }
                        }.addOnFailureListener {
                            progressDialog?.dismiss()
                        }
                    lifecycle.addObserver(dualLanguageTranslator!!)
                }
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            progressDialog?.dismiss()
            Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
        } finally {

        }
    }

    private fun loadAvailableLanguage() {
        val listLanguageCode = mutableListOf<String>()
        listLanguageCode.addAll(TranslateLanguage.getAllLanguages())
        listLanguageCode.map {
            val languageTitle = Locale(it).displayLanguage
            listLanguages.add(ModelLanguage(it, languageTitle))
        }
    }

    private fun getTargetLanguage() {
        val popupMenu = PopupMenu(requireContext(), binding.txtTargetLanguage)

        for ((index, value) in listLanguages.withIndex()) {
            popupMenu.menu.add(Menu.NONE, index, index, value.languageTitle)
        }
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            val position: Int = it.itemId
            targetLanguage = listLanguages[position].languageCode
            binding.txtTargetLanguage.text = listLanguages[position].languageTitle
            targetTitle = listLanguages[position].languageTitle
            false
        }
    }



}