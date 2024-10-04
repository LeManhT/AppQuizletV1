package com.example.quizletappandroidv1.ui.fragments

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RECEIVER_NOT_EXPORTED
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.StudySetItemAdapter
import com.example.appquizlet.interfaceFolder.RvFlashCard
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.FlashcardItemAdapter
import com.example.quizletappandroidv1.databinding.FragmentStudySetDetailBinding
import com.example.quizletappandroidv1.models.FlashCardModel
import com.example.quizletappandroidv1.receiver.DownloadSuccessReceiver
import com.example.quizletappandroidv1.utils.Helper
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

@AndroidEntryPoint
class StudySetDetail : Fragment(), TextToSpeech.OnInitListener,
    FlashcardItemAdapter.OnFlashcardItemClickListener,
    FragmentSortTerm.SortTermListener,
    StudySetItemAdapter.ClickZoomListener {

    private lateinit var binding: FragmentStudySetDetailBinding
    private lateinit var progressDialog: ProgressDialog

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var adapterStudySet: StudySetItemAdapter
    private lateinit var adapterFlashcardDetail: FlashcardItemAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesDetect: SharedPreferences
    private var isPublic: Boolean? = false
    private val listCards = mutableListOf<FlashCardModel>()
    private val downloadCompleteReceiver = DownloadSuccessReceiver()
    private var nameSet: String = ""
    private var currentPoint: Int = 0

    private val documentViewModel: DocumentViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private val args: StudySetDetailArgs by navArgs()


    private val STORAGE_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().registerReceiver(
            downloadCompleteReceiver,
            IntentFilter("PDF_DOWNLOAD_COMPLETE"),
            RECEIVER_NOT_EXPORTED
        );
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudySetDetailBinding.inflate(inflater, container, false)

        sharedPreferences =
            requireContext().getSharedPreferences("TypeSelected", Context.MODE_PRIVATE)
        sharedPreferencesDetect =
            requireContext().getSharedPreferences("countDetect", Context.MODE_PRIVATE)

        // Khởi tạo TextToSpeech
        textToSpeech = TextToSpeech(requireContext(), this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val toolbar =
            binding.toolbar
        (requireActivity() as AppCompatActivity)
            .setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )
        MyApplication.userId?.let { userViewModel.getUserData(it) }

        adapterStudySet = StudySetItemAdapter(object : RvFlashCard {
            override fun handleClickFLashCard(flashcardItem: FlashCardModel) {
                flashcardItem.isUnMark = flashcardItem.isUnMark?.not() ?: true
                adapterStudySet.notifyDataSetChanged()
            }
        }, this)

        adapterFlashcardDetail = FlashcardItemAdapter().apply {
            setOnFlashcardItemClickListener(this@StudySetDetail)
        }

        binding.layoutDownload.setOnClickListener {
            showSaveFormatDialog()
        }

        userViewModel.userData.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                val targetId = args.setId
                val studySetTarget = it.documents.studySets.find { studySet ->
                    studySet.id == targetId
                }
                if (studySetTarget != null) {
                    listCards.clear()
                    adapterStudySet.updateData(studySetTarget.cards)
                    adapterFlashcardDetail.updateData(studySetTarget.cards)
                    listCards.addAll(studySetTarget.cards)
                }

                isPublic = studySetTarget?.isPublic
                if (studySetTarget != null) {
                    binding.txtSetName.text = studySetTarget.name
                }
                if (studySetTarget != null) {
                    nameSet = studySetTarget.name
                }

                if (studySetTarget != null) {
                    binding.layoutNoData.visibility =
                        if (studySetTarget.cards.isEmpty()) View.VISIBLE else View.GONE
                }
                if (studySetTarget != null) {
                    binding.layoutHasData.visibility =
                        if (studySetTarget.cards.isEmpty()) View.GONE else View.VISIBLE
                }

                binding.txtLearnOther.setOnClickListener {
                    requireActivity().finish()
                }
            }.onFailure { }
        }

        userViewModel.userData.observe(viewLifecycleOwner) { userResponse ->
            userResponse.onSuccess {
                binding.txtStudySetDetailUsername.text = it.loginName
            }
        }

        val indicators = binding.circleIndicator3
        indicators.setViewPager(binding.viewPagerStudySet)

        binding.viewPagerStudySet.adapter = adapterStudySet
        adapterFlashcardDetail.setOnFlashcardItemClickListener(this)
        binding.rvAllFlashCards.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAllFlashCards.adapter = adapterFlashcardDetail

        binding.layoutFlashcardLearn.setOnClickListener {
            val action = StudySetDetailDirections.actionStudySetDetailToFlashcardLearn(args.setId)
            findNavController().navigate(action)
        }

        binding.layoutFlashcardTest.setOnClickListener {
            val action =
                StudySetDetailDirections.actionStudySetDetailToFragmentWelcomeToLearn(args.setId)
            findNavController().navigate(action)
        }

    }


    private fun showSaveFormatDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose Save Format")
            .setItems(arrayOf("Excel (.xlsx)", "Word (.docx)")) { _, which ->
                when (which) {
                    0 -> lifecycleScope.launch { saveExcel(listCards) }
                    1 -> lifecycleScope.launch { saveDocx(listCards) }
                }
            }
        builder.create().show()
    }

    private suspend fun saveDocx(listCard: MutableList<FlashCardModel>) {
        val context = requireContext() // Access the Fragment's context
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "download_channel"
        val channelName = "Download Channel"

        // Create a notification channel if it doesn't exist
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Downloading DOCX")
            .setContentText("Download in progress")
            .setSmallIcon(R.drawable.icons8_download_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(100, 0, true)
            .setOngoing(true)

        // Show the initial notification
        notificationManager.notify(notificationId, notificationBuilder.build())

        withContext(Dispatchers.IO) {
            try {
                val document = XWPFDocument()

                // Simulate progress in the notification
                for (progress in 1..100) {
                    notificationBuilder.setProgress(100, progress, false)
                    notificationManager.notify(notificationId, notificationBuilder.build())
                    withContext(Dispatchers.IO) {
                        Thread.sleep(50)
                    }
                }

                // Write flashcard content to the DOCX document
                for (flashCard in listCard) {
                    val term = flashCard.term ?: ""
                    val definition = flashCard.definition ?: ""
                    val content = "$term : $definition"

                    val paragraph = document.createParagraph()
                    val run = paragraph.createRun()
                    run.setText(content)
                }

                // Specify the directory and filename for saving the DOCX file
                val mDirectory = Environment.DIRECTORY_DOWNLOADS
                val mFilename = SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    Locale.getDefault()
                ).format(System.currentTimeMillis())
                val mFilePath = Environment.getExternalStoragePublicDirectory(mDirectory)
                    .toString() + "/" + mFilename + ".docx"

                // Save the DOCX document to a file
                val outputStream = FileOutputStream(mFilePath)
                document.write(outputStream)
                outputStream.close()

                // Update the UI when the file is saved
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "$mFilename.docx is created",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Send a broadcast when download is complete
                    val downloadCompleteIntent = Intent("DOCX_DOWNLOAD_COMPLETE")
                    downloadCompleteIntent.putExtra("file_path", mFilePath)
                    context.sendBroadcast(downloadCompleteIntent)
                }

            } catch (e: Exception) {
                // Handle errors on the main thread for UI updates
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            } finally {
                // Remove the ongoing notification when the download is complete
                notificationManager.cancel(notificationId)
            }
        }
    }

    // Import necessary classes
    private suspend fun saveExcel(listCard: MutableList<FlashCardModel>) {
        val context = requireContext() // Get Fragment's context
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "download_channel"
        val channelName = "Download Channel"

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(getString(R.string.download_excel)) // Use getString directly in Fragment
            .setContentText(getString(R.string.download_in_progess))
            .setSmallIcon(R.drawable.icons8_download_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(100, 0, true)
            .setOngoing(true)

        notificationManager.notify(notificationId, notificationBuilder.build())

        withContext(Dispatchers.IO) {
            try {
                val workbook = HSSFWorkbook()
                val sheet = workbook.createSheet(nameSet)

                for (progress in 1..100) {
                    notificationBuilder.setProgress(100, progress, false)
                    notificationManager.notify(notificationId, notificationBuilder.build())
                    Thread.sleep(50)
                }

                for ((rowIndex, flashCard) in listCard.withIndex()) {
                    val row = sheet.createRow(rowIndex)
                    val termCell = row.createCell(0)
                    val definitionCell = row.createCell(1)

                    termCell.setCellValue(flashCard.term)
                    definitionCell.setCellValue(flashCard.definition)
                }

                // Specify the directory and filename for saving the Excel file
                val mDirectory = Environment.DIRECTORY_DOWNLOADS
                val mFilename = SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    Locale.getDefault()
                ).format(System.currentTimeMillis())
                val mFilePath = Environment.getExternalStoragePublicDirectory(mDirectory)
                    .toString() + "/" + mFilename + ".xlsx"

                // Write the workbook to a file
                val fileOutputStream = FileOutputStream(mFilePath)
                workbook.write(fileOutputStream)
                fileOutputStream.close()

                // Display a toast message on the UI thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "$mFilename.xlsx is created",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // Send broadcast when download is complete
                withContext(Dispatchers.Main) {
                    val downloadCompleteIntent = Intent("PDF_DOWNLOAD_COMPLETE")
                    downloadCompleteIntent.putExtra("file_path", mFilePath)
                    context.sendBroadcast(downloadCompleteIntent)
                }

            } catch (e: Exception) {
                // Handle exceptions on the main thread for proper UI updates
                withContext(Dispatchers.Main) {
                    Log.d("saveExcel", e.message.toString())
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            } finally {
                // Remove the ongoing notification when the download is complete
                notificationManager.cancel(notificationId)
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showSaveFormatDialog()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSortTermSelected(sortType: String) {
        when (sortType) {
            "OriginalSort" -> {
//                    listFlashcardDetails.clear()
//                    listFlashcardDetails.addAll(originalList)
                with(sharedPreferences.edit()) {
                    putString("selectedT", sortType)
                    apply()
                }
            }

            "AlphabeticalSort" -> {
//                    listFlashcardDetails.sortBy { it.term }
                with(sharedPreferences.edit()) {
                    putString("selectedT", sortType)
                    apply()
                }

            }
        }

        adapterFlashcardDetail.notifyDataSetChanged()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_study_set, menu)

        // Quan sát trạng thái isPublic
        documentViewModel.isPublic.observe(viewLifecycleOwner) { isPublic ->
            val publicMenuItem: MenuItem? = menu.findItem(R.id.option_public)
            publicMenuItem?.title = if (isPublic) {
                resources.getString(R.string.disable_public_set)
            } else {
                resources.getString(R.string.public_set)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().finish()
                return true
            }

            R.id.option_add_to_folder_studyset -> {
                val action =
                    StudySetDetailDirections.actionStudySetDetailToAddSetToFolder(args.setId)
                findNavController().navigate(action)
            }

            R.id.option_share -> {
                shareDialog(Helper.getDataUserId(requireContext()), args.setId)
            }

            R.id.option_edit_studyset -> {
                val action =
                    StudySetDetailDirections.actionStudySetDetailToFragmentEditSet(args.setId)
                findNavController().navigate(action)
            }

            R.id.option_public -> {
                Log.d("isPu", isPublic.toString())
                if (isPublic == true) {
                    disablePublicSet(Helper.getDataUserId(requireContext()), args.setId)
                    item.title = resources.getString(R.string.public_set)
                } else {
                    enablePublicSet(Helper.getDataUserId(requireContext()), args.setId)
                    item.title = resources.getString(R.string.disable_public_set)
                }
            }

            R.id.option_delete -> {
                showDeleteDialog(resources.getString(R.string.delete_text))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enablePublicSet(userId: String, setId: String) {
            showLoading(resources.getString(R.string.publishing_set))
        documentViewModel.enablePublicSet(userId, setId)
    }

    private fun disablePublicSet(userId: String, setId: String) {
        documentViewModel.disablePublicSet(userId, setId)
    }

    //
    //    private fun showDialogBottomSheet() {
    //        val addBottomSheet = FragmentSortTerm()
    //        addBottomSheet.sortTermListener = requireContext()
    //
    //        if (!addBottomSheet.isAdded) {
    //            val transaction = supportFragmentManager.beginTransaction()
    //            transaction.add(addBottomSheet, FragmentSortTerm.TAG)
    //            transaction.commitAllowingStateLoss()
    //        }
    //    }


    private fun shareDialog(userId: String, setId: String) {
        val deepLinkBaseUrl = "www.ttcs_quizlet.com/studyset"
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "$deepLinkBaseUrl/$userId/$setId")
        val packageNames =
            arrayOf("com.facebook.katana", "com.facebook.orca", "com.google.android.gm")
        val chooserIntent = Intent.createChooser(sharingIntent, "Share via")
        chooserIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, packageNames)
        startActivity(chooserIntent)
    }

    private fun showDeleteDialog(desc: String) {
        val builder = AlertDialog.Builder(requireContext())
        //        builder.setTitle(title)
        builder.setMessage(desc)
        builder.setPositiveButton(resources.getString(R.string.delete)) { dialog, _ ->
            deleteStudySet(Helper.getDataUserId(requireContext()), args.setId)
            dialog.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun deleteStudySet(userId: String, setId: String) {
        documentViewModel.deleteStudySet(userId, setId)
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
            Log.e("TTSpeech2", "Initialization failed with status: $status")
            Toast.makeText(requireContext(), "Initialization failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakOut(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        textToSpeech.shutdown()
        requireActivity().unregisterReceiver(downloadCompleteReceiver)
        super.onDestroy()
    }

    override fun onFlashcardItemClick(term: String) {
        speakOut(term)
    }

    override fun onClickZoomBtn() {
        val action = StudySetDetailDirections.actionStudySetDetailToFlashcardLearn(args.setId)
        findNavController().navigate(action)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayCheckedDates() {
        val unixTime = Instant.now().epochSecond
        detectContinueStudy(Helper.getDataUserId(requireContext()), unixTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun detectContinueStudy(userId: String, timeDetect: Long) {
        //        documentViewModel.detectContinueStudy(userId, timeDetect)
    }

}