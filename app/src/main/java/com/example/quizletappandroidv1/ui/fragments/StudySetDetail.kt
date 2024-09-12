package com.example.quizletappandroidv1.ui.fragments

import CustomPopUpWindow
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
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
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.appquizlet.adapter.StudySetItemAdapter
import com.example.quizletappandroidv1.MainActivity
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.FlashcardItemAdapter
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentStudySetDetailBinding
import com.example.quizletappandroidv1.models.FlashCardModel
import com.example.quizletappandroidv1.receiver.DownloadSuccessReceiver
import com.example.quizletappandroidv1.utils.Helper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

class StudySetDetail : Fragment(), TextToSpeech.OnInitListener,
    FlashcardItemAdapter.OnFlashcardItemClickListener,
//    FragmentSortTerm.SortTermListener,
    StudySetItemAdapter.ClickZoomListener {

    private lateinit var binding: FragmentStudySetDetailBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var setId: String
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var adapterStudySet: StudySetItemAdapter
    private lateinit var adapterFlashcardDetail: FlashcardItemAdapter
    private var listFlashcardDetails: MutableList<FlashCardModel> = mutableListOf()
    private var originalList: MutableList<FlashCardModel> = mutableListOf()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesDetect: SharedPreferences
    private var isPublic: Boolean? = false
    private val listCards = mutableListOf<FlashCardModel>()
    private val downloadCompleteReceiver = DownloadSuccessReceiver()
    private var nameSet: String = ""
    private var currentPoint: Int = 0


    private val STORAGE_CODE = 1001


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudySetDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
//        val notificationManager =
//            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val notificationId = 1
//        val channelId = "download_channel"
//        val channelName = "Download Channel"
//
//        val channel =
//            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
//        notificationManager.createNotificationChannel(channel)
//
//        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
//            .setContentTitle("Downloading DOCX")
//            .setContentText("Download in progress")
//            .setSmallIcon(R.drawable.icons8_download_24)
//            .setPriority(NotificationCompat.PRIORITY_LOW)
//            .setProgress(100, 0, true)
//            .setOngoing(true)
//
//        notificationManager.notify(notificationId, notificationBuilder.build())
//
//        withContext(Dispatchers.IO) {
//            try {
//                val document = XWPFDocument()
//
//                for (progress in 1..100) {
//                    // Update the notification progress
//                    notificationBuilder.setProgress(100, progress, false)
//                    notificationManager.notify(notificationId, notificationBuilder.build())
//                    // Simulate some work being done
//                    withContext(Dispatchers.IO) {
//                        Thread.sleep(50)
//                    }
//                }
//
//                for (flashCard in listCard) {
//                    val term = flashCard.term ?: ""
//                    val definition = flashCard.definition ?: ""
//                    val content = "$term : $definition"
//
//                    val paragraph = document.createParagraph()
//                    val run = paragraph.createRun()
//                    run.setText(content)
//                }
//
//                // Specify the directory and filename for saving the DOCX file
//                val mDirectory = Environment.DIRECTORY_DOWNLOADS
//                val mFilename = SimpleDateFormat(
//                    "yyyyMMdd_HHmmss",
//                    Locale.getDefault()
//                ).format(System.currentTimeMillis())
//                val mFilePath = Environment.getExternalStoragePublicDirectory(mDirectory)
//                    .toString() + "/" + mFilename + ".docx"
//
//                // Save the DOCX document to a file
//                val outputStream = FileOutputStream(mFilePath)
//                document.write(outputStream)
//                outputStream.close()
//
//                runOnUiThread {
//                    Toast.makeText(
//                        requireContext()@StudySetDetail,
//                        "$mFilename.docx is created",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                // Send broadcast when download is complete
//                runOnUiThread {
//                    val downloadCompleteIntent = Intent("PDF_DOWNLOAD_COMPLETE")
//                    downloadCompleteIntent.putExtra("file_path", mFilePath)
//                    requireContext()@StudySetDetail.sendBroadcast(downloadCompleteIntent)
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext()@StudySetDetail, e.message.toString(), Toast.LENGTH_SHORT)
//                    .show()
//            } finally {
//                // Remove the ongoing notification when the download is complete
//                notificationManager.cancel(notificationId)
//            }
//        }
    }

    // Import necessary classes
    private suspend fun saveExcel(listCard: MutableList<FlashCardModel>) {
//        val notificationManager =
//            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val notificationId = 1
//        val channelId = "download_channel"
//        val channelName = "Download Channel"
//
//        val channel =
//            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
//        notificationManager.createNotificationChannel(channel)
//
//        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
//            .setContentTitle(resources.getString(R.string.download_excel))
//            .setContentText(resources.getString(R.string.download_in_progess))
//            .setSmallIcon(R.drawable.icons8_download_24)
//            .setPriority(NotificationCompat.PRIORITY_LOW)
//            .setProgress(100, 0, true)
//            .setOngoing(true)
//
//        notificationManager.notify(notificationId, notificationBuilder.build())
//        withContext(Dispatchers.IO) {
//            try {
//                val workbook = HSSFWorkbook()
//                val sheet = workbook.createSheet(nameSet)
//
//                for (progress in 1..100) {
//                    // Update the notification progress
//                    notificationBuilder.setProgress(100, progress, false)
//                    notificationManager.notify(notificationId, notificationBuilder.build())
//                    // Simulate some work being done
//                    Thread.sleep(50)
//                }
//
//                for ((rowIndex, flashCard) in listCard.withIndex()) {
//                    val row = sheet.createRow(rowIndex)
//                    val termCell = row.createCell(0)
//                    val definitionCell = row.createCell(1)
//
//                    termCell.setCellValue(flashCard.term)
//                    definitionCell.setCellValue(flashCard.definition)
//                }
//
//                // Specify the directory and filename for saving the Excel file
//                val mDirectory = Environment.DIRECTORY_DOWNLOADS
//                val mFilename = SimpleDateFormat(
//                    "yyyyMMdd_HHmmss",
//                    Locale.getDefault()
//                ).format(System.currentTimeMillis())
//                val mFilePath = Environment.getExternalStoragePublicDirectory(mDirectory)
//                    .toString() + "/" + mFilename + ".xlsx"
//
//                // Write the workbook to a file
//                val fileOutputStream = FileOutputStream(mFilePath)
//                workbook.write(fileOutputStream)
//                fileOutputStream.close()
//
//                // Display a toast message
//                runOnUiThread {
//                    Toast.makeText(
//                        requireContext()@StudySetDetail,
//                        "$mFilename.xlsx is created",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                // Send broadcast when download is complete
//                runOnUiThread {
//                    val downloadCompleteIntent = Intent("PDF_DOWNLOAD_COMPLETE")
//                    downloadCompleteIntent.putExtra("file_path", mFilePath)
//                    requireContext()@StudySetDetail.sendBroadcast(downloadCompleteIntent)
//                }
//
//                Log.d("saveExcel1", "error 1")
//            } catch (e: Exception) {
//                // Handle exceptions
//                Log.d("saveExcel", e.message.toString())
//                Toast.makeText(requireContext()@StudySetDetail, e.message.toString(), Toast.LENGTH_SHORT).show()
//            } finally {
//                // Remove the ongoing notification when the download is complete
//                notificationManager.cancel(notificationId)
//            }
//        }
    }


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

//    override fun onSortTermSelected(sortType: String) {
//        when (sortType) {
//            "OriginalSort" -> {
//                listFlashcardDetails.clear()
//                listFlashcardDetails.addAll(originalList)
//                with(sharedPreferences.edit()) {
//                    putString("selectedT", sortType)
//                    apply()
//                }
//            }
//
//            "AlphabeticalSort" -> {
//                listFlashcardDetails.sortBy { it.term }
//                with(sharedPreferences.edit()) {
//                    putString("selectedT", sortType)
//                    apply()
//                }
//
//            }
//        }
//
//        adapterFlashcardDetail.notifyDataSetChanged()
//    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.menu_study_set, menu)
//        val publicMenuItem: MenuItem? = menu.findItem(R.id.option_public)
//        publicMenuItem?.title = if (isPublic == true) {
//            resources.getString(R.string.disable_public_set)
//        } else {
//            resources.getString(R.string.public_set)
//        }
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().finish()
                return true
            }

            R.id.option_add_to_folder -> {
//                val i = Intent(requireContext(), FlashcardAddSetToFolder::class.java)
//                i.putExtra("addSetId", setId)
//                startActivity(i)
            }

            R.id.option_share -> {
                shareDialog(Helper.getDataUserId(requireContext()), setId)
            }

            R.id.option_edit -> {
//                val i = Intent(requireContext(), EditStudySet::class.java)
//                i.putExtra("editSetId", setId)
//                startActivity(i)
            }

            R.id.option_public -> {
                Log.d("isPu", isPublic.toString())
                if (isPublic == true) {
                    disablePublicSet(Helper.getDataUserId(requireContext()), setId)
                    item.title = resources.getString(R.string.public_set)
                } else {
                    enablePublicSet(Helper.getDataUserId(requireContext()), setId)
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
        lifecycleScope.launch {
            showLoading(resources.getString(R.string.publishing_set))
            try {
//                apiService.enablePublicSet(userId, setId)
                isPublic = true
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    resources.getString(R.string.set_is_public),
                    CustomToast.LONG,
                    CustomToast.SUCCESS
                ).show()
            } catch (e: Exception) {
                CustomToast(requireContext()).makeText(
                    requireContext(), e.message.toString(), CustomToast.LONG, CustomToast.ERROR
                ).show()
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    private fun disablePublicSet(userId: String, setId: String) {
//        lifecycleScope.launch {
//            try {
//                showLoading(resources.getString(R.string.privating_set))
//                apiService.disablePublicSet(userId, setId)
//                isPublic = false
//                CustomToast(requireContext()).makeText(
//                    requireContext(),
//                    resources.getString(R.string.set_is_private),
//                    CustomToast.LONG,
//                    CustomToast.SUCCESS
//                ).show()
//            } catch (e: Exception) {
//                CustomToast(requireContext()).makeText(
//                    requireContext(), e.message.toString(), CustomToast.LONG, CustomToast.ERROR
//                ).show()
//            } finally {
//                progressDialog.dismiss()
//            }
//        }
    }


//    private fun showStudyThisSetBottomsheet(setId: String) {
//        val addCourseBottomSheet = StudyThisSetFragment()
//        // Tạo một Bundle để truyền dữ liệu
//        val bundle = Bundle()
//        bundle.putString("setIdTo", setId)
//
//        // Đặt Bundle vào Fragment
//        addCourseBottomSheet.arguments = bundle
//        addCourseBottomSheet.show(supportFragmentManager, "")
//    }
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
            deleteStudySet(Helper.getDataUserId(requireContext()), setId)
            dialog.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun deleteStudySet(userId: String, setId: String) {
//        lifecycleScope.launch {
//            showLoading(resources.getString(R.string.deleteFolderLoading))
//            try {
//                val result = apiService.deleteStudySet(userId, setId)
//                if (result.isSuccessful) {
//
//                    result.body().let {
//                        if (it != null) {
//                            CustomToast(requireContext()@StudySetDetail).makeText(
//                                requireContext()@StudySetDetail,
//                                resources.getString(R.string.deleteSetSuccessful),
//                                CustomToast.LONG,
//                                CustomToast.SUCCESS
//                            ).show()
//                            UserM.setUserData(it)
//                        }
//                    }
//                    val i = Intent(requireContext(), MainActivity::class.java)
//                    i.putExtra("selectedFragment", "Library")
//                    i.putExtra("createMethod", "createSet")
//                    startActivity(i)
//                } else {
//                    CustomToast(requireContext()@StudySetDetail).makeText(
//                        requireContext(),
//                        resources.getString(R.string.deleteSetErr),
//                        CustomToast.LONG,
//                        CustomToast.ERROR
//                    ).show()
//
//                }
//            } catch (e: Exception) {
//                CustomToast(requireContext()).makeText(
//                    requireContext(), e.message.toString(), CustomToast.LONG, CustomToast.ERROR
//                ).show()
//            } finally {
//                progressDialog.dismiss()
//            }
//        }
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
//        unregisterReceiver(downloadCompleteReceiver)
        super.onDestroy()
    }

    override fun onFlashcardItemClick(term: String) {
        speakOut(term)
    }

    override fun onClickZoomBtn() {
        val jsonList = Gson().toJson(listCards)
        val i = Intent(requireContext(), FlashcardLearn::class.java)
        i.putExtra("listCard", jsonList)
        startActivity(i)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayCheckedDates() {
        val unixTime = Instant.now().epochSecond
        detectContinueStudy(Helper.getDataUserId(requireContext()), unixTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun detectContinueStudy(userId: String, timeDetect: Long) {
//        lifecycleScope.launch {
//            try {
//                val result = apiService.detectContinueStudy(userId, timeDetect)
//                if (result.isSuccessful) {
//                    result.body()?.let {
//                        val editor = sharedPreferencesDetect.edit()
//                        editor.putInt("countLearn", 1)
//                        editor.apply()
////                        UserM.setDataAchievements(it)
//                        val congratulationsPopup =
//                            CustomPopUpWindow(requireContext(), it.streak.currentStreak)
//                        congratulationsPopup.showCongratulationsPopup()
//                    }
//                } else {
//                    Log.d("error", result.errorBody().toString())
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

}