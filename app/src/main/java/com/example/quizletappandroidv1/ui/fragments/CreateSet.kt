package com.example.quizletappandroidv1.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.CreateSetItemAdapter
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentCreateSetBinding
import com.example.quizletappandroidv1.models.CreateSetRequest
import com.example.quizletappandroidv1.models.FlashCardModel
import com.example.quizletappandroidv1.utils.FileHelperUtils
import com.example.quizletappandroidv1.utils.Helper
import com.example.quizletappandroidv1.utils.URIPathHelper
import com.example.quizletappandroidv1.viewmodel.home.HomeViewModel
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.IndicatorType
import com.nguyenhoanglam.imagepicker.model.RootDirectory
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import timber.log.Timber
import java.io.FileInputStream
import java.util.Locale

@AndroidEntryPoint
class CreateSet : Fragment(), CreateSetItemAdapter.OnIconClickListener {
    private lateinit var binding: FragmentCreateSetBinding
    private lateinit var progressDialog: ProgressDialog
    private val homeViewModel by viewModels<HomeViewModel>()
    private val documentViewModel by activityViewModels<DocumentViewModel>()

    //    private lateinit var apiService: ApiService
    private var listSet = mutableListOf<FlashCardModel>()
    private lateinit var adapterCreateSet: CreateSetItemAdapter
    private val REQUEST_CODE_SPEECH_INPUT = 150
    private var speechRecognitionPosition: Int = -1
    private val REQUEST_CAMERA_CODE = 2404
    private var uri: Uri? = null
    private val IMPORT_EXCEL_REQUEST_CODE = 100
    private val STORAGE_CODE = 1001
    private var currentPoint: Int = 0
    private var dualLanguageTranslator: com.google.mlkit.nl.translate.Translator? = null
    private lateinit var speechInputLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var excelPickerLauncher: ActivityResultLauncher<Intent>
    private val storageActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }


    private var currentPosition: Int = -1

    private val REQUEST_CODE = 101

    private val launcher = registerImagePicker { images ->
        val image = images[0]
        // Hoặc tiếp tục xử lý ảnh theo nhu cầu của bạn trên main thread
        val uriPathHelper = URIPathHelper()
        val filePath = context?.let { uriPathHelper.getPath(requireContext(), image.uri) }
        if (images.isNotEmpty()) {
            processSelectedImage(images[0].uri)
        }
    }
    private val imagePickerConfig = ImagePickerConfig(
        isFolderMode = false,
        isShowCamera = true,
        selectedIndicatorType = IndicatorType.NUMBER,
        rootDirectory = RootDirectory.DCIM,
        subDirectory = "Image Picker",
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateSetBinding.inflate(
            layoutInflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent()
                    intent.action =
                        android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    val uri = Uri.fromParts(
                        "package", requireContext().packageName, null
                    )
                    intent.data = uri
                    storageActivityLauncher.launch(intent)
                } catch (e: Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    storageActivityLauncher.launch(
                        intent
                    )
                }
                }
        } else {
            if (requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(permission), STORAGE_CODE
                )

            }
            }
        setupRecyclerView()
        setupUIListeners()

        homeViewModel.rankResult.observe(viewLifecycleOwner) { data ->
            data.onSuccess {
                currentPoint = it.currentScore
            }.onFailure {
                Timber.d("Error: ${it.message}")
            }
        }

        documentViewModel.studySetResponse.observe(viewLifecycleOwner) { data ->
            if (data) {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    resources.getString(R.string.create_set_success),
                    CustomToast.LONG,
                    CustomToast.SUCCESS
                ).show()
                findNavController().navigate(R.id.action_createSet_to_fragmentLibrary2)
            } else {
                Timber.d("Error: $data")
            }
        }

// Check for camera permission
        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_CODE
            )
        }
    }

    private fun setupRecyclerView() {
        listSet.add(FlashCardModel())
        listSet.add(FlashCardModel())
        listSet.add(FlashCardModel())
        listSet.add(FlashCardModel())
        adapterCreateSet = CreateSetItemAdapter()
        adapterCreateSet.updateListSet(listSet)
        adapterCreateSet.setOnIconClickListener(this)
        binding.RvCreateSets.layoutManager = LinearLayoutManager(requireContext())
        binding.RvCreateSets.adapter = adapterCreateSet
    }

    private fun setupUIListeners() {
        binding.addNewCard.setOnClickListener {
            listSet.add(FlashCardModel())
            adapterCreateSet.notifyItemInserted(listSet.size - 1)
            binding.RvCreateSets.scrollToPosition(listSet.size - 1)
            binding.createSetScrollView.post {
                binding.createSetScrollView.smoothScrollTo(
                    0, binding.RvCreateSets.bottom
                )
            }
        }
        binding.iconTickCreateSet.setOnClickListener {
            val name = binding.txtNameStudySet.text.toString()
            val desc = binding.txtDescription.toString()
            val userId = Helper.getDataUserId(requireContext())
            val updatedList = adapterCreateSet.getListSet()
            val isEmptyItemExist =
                updatedList.any { it.term?.isEmpty() == true || it.definition?.isEmpty() == true }
            if (isEmptyItemExist) {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    "Please fill in all flashcards before updating.",
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } else if (updatedList.isNotEmpty()) {
                if (updatedList.size < 4) {
                    CustomToast(requireContext()).makeText(
                        requireContext(),
                        "Please add at least 4 flashcards.",
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                } else {
                    if (name.isEmpty()) {
                        CustomToast(requireContext()).makeText(
                            requireContext(),
                            resources.getString(R.string.set_name_is_required),
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    } else {
                        createNewStudySet(userId, name, desc, updatedList)
                    }
                }
            }
        }
        binding.iconImportFile.setOnClickListener {
            if (currentPoint > 30) {
                showImportAlertDialog(requireContext())
            } else {
                findNavController().navigate(R.id.action_createSet_to_quizletPlus)
            }
        }

        speechInputLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val position = speechRecognitionPosition
                    val speechResults =
                        result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (!speechResults.isNullOrEmpty()) {
                        val spokenText = speechResults[0]
                        if (adapterCreateSet.getIsDefinition() == true) {
                            updateRecyclerViewItemDefinition(position, spokenText)
                        } else {
                            updateRecyclerViewItemTerm(position, spokenText)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(), "No speech results found", Toast.LENGTH_SHORT
                        ).show()
                    }
                    speechRecognitionPosition = -1
                }
            }
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    uri = result.data!!.data!!
                    recognizeText()
                } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(
                        requireContext(), ImagePicker.getError(result.data), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        excelPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                Log.d("ImportExcel12222", "registerForActivityResult Import Excel Request Code")

                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedFileUri = result.data?.data
                    Log.d("ImportExcel12222", "Vào resultCode ${selectedFileUri}")
                    if (selectedFileUri != null) {
                        val filePath = FileHelperUtils.getPath(
                            requireContext(), selectedFileUri
                        )
                        Log.d("ImportExcel12222", "Vào resultCode  filePath ${filePath}")
                        if (filePath != null && filePath.endsWith(".xlsx")) {
                            importExcelFile(filePath)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please select a valid Excel file",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            binding.iconImportFile,
                            resources.getString(R.string.there_error_when_import_excel_file),
                            Snackbar.LENGTH_SHORT
                        ).setBackgroundTint(resources.getColor(R.color.my_red_snackbar)).show()
                    }
                }
            }
    }

    private fun createNewStudySet(
        userId: String, studySetName: String, studySetDesc: String, dataSet: List<FlashCardModel>
    ) {
        val body = CreateSetRequest(
            name = studySetName, description = studySetDesc, allNewCards = dataSet
        )
        documentViewModel.createNewStudySet(userId, body)
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
    }

    override fun onIconClick(position: Int) {
        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            speechRecognitionPosition = position
            startSpeechRecognition(position)
        } else {
            requestSpeechRecognitionPermission()
        }
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

    private fun startSpeechRecognition(position: Int) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        intent.putExtra(RecognizerIntent.EXTRA_ORIGIN, position)
        try {
            startActivityForResult(
                intent, REQUEST_CODE_SPEECH_INPUT
            )
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(), "Error RecognizerIntent: ${e.message}", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun requestSpeechRecognitionPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            REQUEST_CODE_SPEECH_INPUT
        )
    }

    // Launch the file picker
    private fun launchFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        excelPickerLauncher.launch(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )
        when (requestCode) {
            IMPORT_EXCEL_REQUEST_CODE -> {
                Log.d("ImportExcel", "Import Excel Request Code")
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchFilePicker()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_CODE_SPEECH_INPUT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSpeechRecognition(speechRecognitionPosition)
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun importExcelFile(filePath: String) {
        Log.d("ImportExcel", "Importing Excel file: $filePath")
        val inputStream = FileInputStream(filePath)
        try {
            val workbook: Workbook = WorkbookFactory.create(inputStream)
            if (filePath.endsWith(".xlsx")) {
                HSSFWorkbook(inputStream)
            }  // For XSSF (Excel 2007+ XML) format //
            else if (filePath.endsWith(".xls")) {
                HSSFWorkbook(inputStream)
                // For HSSF (Excel 97-2003) format //
            } else {
                Log.e("ImportExcel", "Unsupported file type")
            }
            val sheet = workbook.getSheetAt(0)
            listSet.clear()
            for (rowIndex in 0 until sheet.physicalNumberOfRows) {
                val row = sheet.getRow(rowIndex)
                if (row != null) {
                    val termCell = row.getCell(0)
                    val definitionCell = row.getCell(1)
                    val term = termCell?.let { getCellValue(it) } ?: ""
                    val definition = definitionCell?.let { getCellValue(it) } ?: ""
                    Log.d("ImportExcel10", Gson().toJson(term))
                    if (term.isNotEmpty() && definition.isNotEmpty()) {
                        val flashCard = FlashCardModel(
                            term = term, definition = definition
                        )
                        listSet.add(flashCard)
                        adapterCreateSet.updateListSet(listSet)
                    }
                }
            }
            Log.d("ImportExcel1", Gson().toJson(listSet))
            workbook.close()
        } catch (e: Exception) {
            Log.e("ImportExcel", "Error importing Excel file: ${e.message}")
        } finally {             // Close the input stream in the finally block to ensure it is closed
            inputStream.close()
        }
    }

    private fun getCellValue(cell: Cell): String {
        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> cell.numericCellValue.toString()
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            else -> ""
        }
    }

    private fun showImportAlertDialog(context: Context) {
        MaterialAlertDialogBuilder(context).setTitle(resources.getString(R.string.alert_import_title))
            .setMessage(resources.getString(R.string.alert_import_desc))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    val permission = Manifest.permission.READ_EXTERNAL_STORAGE
                    if (ContextCompat.checkSelfPermission(
                            requireContext(), permission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        launchFilePicker()
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(), arrayOf(permission), IMPORT_EXCEL_REQUEST_CODE
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
                            requireActivity(), permissions, IMPORT_EXCEL_REQUEST_CODE
                        )
                    } else {
                        // Launch the file picker
                        launchFilePicker()
                    }
                }
            }.show()
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
        btnShowDialogChooseTranslate(position, "This is test language")
    }

    private fun btnShowDialogChooseTranslate(position: Int, text: String) {
        val languageIdentifier = LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder().setConfidenceThreshold(0.1f).build()
        )
        languageIdentifier.identifyLanguage(text).addOnSuccessListener { languageCode ->
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
                        position, text, languageCode, getTranslateLanguageCode(itemsArray[which])
                    )
                }
                builder.create().show()
            }
        }.addOnFailureListener { }
    }

    private fun getTranslateLanguageCode(languageName: String): String {
        return when (languageName.toUpperCase()) {
            "ENGLISH" -> TranslateLanguage.ENGLISH
            "VIETNAMESE" -> TranslateLanguage.VIETNAMESE
            "CHINESE" -> TranslateLanguage.CHINESE
            else -> TranslateLanguage.ENGLISH // Handle the default case or unsupported language         }
        }
    }

    private fun translateText(
        position: Int, text: String, sourceLanguage: String, targetLanguage: String
    ) {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val options = TranslatorOptions.Builder().setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage).build()
            dualLanguageTranslator = Translation.getClient(options)
            var conditions = DownloadConditions.Builder().requireWifi().build()
            showLoading(resources.getString(R.string.please_wait_to_load_model))
            dualLanguageTranslator!!.downloadModelIfNeeded(conditions).addOnSuccessListener {
                dualLanguageTranslator!!.translate(
                    text
                ).addOnSuccessListener { translatedText ->
                    progressDialog.dismiss()
                    if (adapterCreateSet.getIsDefinitionTranslate() == true) {
                        listSet[position].definition = translatedText
                        adapterCreateSet.notifyDataSetChanged()
                    } else {
                        listSet[position].term = translatedText
                        adapterCreateSet.notifyDataSetChanged()
                    }
                }.addOnFailureListener { exception ->
                    Log.i("detectL3", "translatedText: $exception")
                    progressDialog.dismiss()
                }
            }.addOnFailureListener { exception ->
                Log.i("exception", "exception: $exception")
                progressDialog.dismiss()
            }
            lifecycle.addObserver(dualLanguageTranslator!!)
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dualLanguageTranslator?.close()
    }

    private fun recognizeText() {
        if (uri !== null) {
            val inputImage = InputImage.fromFilePath(requireContext(), uri!!)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            listSet.clear()
            val result = recognizer.process(inputImage).addOnSuccessListener { visionText ->
                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        val lineText =
                            line.text                         // Split the line text into term and definition
                        val parts =
                            lineText.split(":")
                        Log.d("Part", "Term: $parts")
                        if (parts.size == 2) {
                            val term = parts[0].trim()
                            val definition = parts[1].trim()
                            val flashCard = FlashCardModel(
                                term = term, definition = definition
                            )
                            listSet.add(flashCard)
                            adapterCreateSet.notifyDataSetChanged()
                        }
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("recognizeText", "Error recognizing text: ${e.message}")
            }
        }
    }

    private fun updateRecyclerViewItemDefinition(position: Int, spokenText: String?) {
        if (position < listSet.size && spokenText != null) {
            val item =
                listSet[position]
            item.definition = spokenText
            adapterCreateSet.notifyItemChanged(
                position
            )
        }
    }

    private fun updateRecyclerViewItemTerm(position: Int, spokenText: String?) {
        if (position < listSet.size && spokenText != null) {
            val item =
                listSet[position]
            item.term = spokenText
            adapterCreateSet.notifyItemChanged(
                position
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onChooseImage(position: Int) {
        currentPosition = position
        requestStoragePermission()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestStoragePermission() {
        val permission = Manifest.permission.READ_MEDIA_IMAGES
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), REQUEST_CODE)
        }
    }


    private fun openGallery() {
        launcher.launch(imagePickerConfig)
    }

    private fun processSelectedImage(uri: Uri) {
        val uriPathHelper = URIPathHelper()
        val filePath = context?.let { uriPathHelper.getPath(it, uri) }
        context?.let { showLoading(resources.getString(R.string.upload_avatar_loading)) }
        lifecycleScope.launch {
            try {
                adapterCreateSet.updateImageUri(currentPosition, uri)
            } catch (e: Exception) {
                context?.let {
                    CustomToast(it).makeText(
                        it,
                        e.message.toString(),
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                }
            } finally {
                progressDialog.dismiss()
            }
        }
    }

}
