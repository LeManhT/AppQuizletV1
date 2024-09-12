package com.example.quizletappandroidv1.ui.fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.DayOfWeekAdapter
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.FragmentProfileBinding
import com.example.quizletappandroidv1.models.UpdateUserResponse
import com.example.quizletappandroidv1.utils.URIPathHelper
import com.example.quizletappandroidv1.viewmodel.home.HomeViewModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.IndicatorType
import com.nguyenhoanglam.imagepicker.model.RootDirectory
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import com.squareup.okhttp.Response
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class Profile : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressDialog: ProgressDialog
    private var currentPoint: Int = 0

    private val REQUEST_CODE = 10
    private val launcher = registerImagePicker { images ->
        if (images.isNotEmpty()) {
            processSelectedImage(images[0].uri)
        }
    }

    private val userViewModel: UserViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private val imagePickerConfig = ImagePickerConfig(
        isFolderMode = false,
        isShowCamera = true,
        selectedIndicatorType = IndicatorType.NUMBER,
        rootDirectory = RootDirectory.DCIM,
        subDirectory = "Image Picker",
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        initViews()
        loadUserData()
        loadAchievements()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AddFragment", "Profile onViewCreated called")
    }


    private fun initViews() {
        binding.imgAvatar.setOnClickListener {
            startActivity(Intent(requireContext(), ViewImage::class.java))
        }

        binding.linearLayoutSettings.setOnClickListener {
            startActivity(Intent(requireContext(), Settings::class.java))
        }

        binding.txtUploadImage.setOnClickListener {
            requestStoragePermission()
        }

        binding.txtViewAchievement.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_achievements)
        }

        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
        binding.rvDayOfWeek.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = DayOfWeekAdapter(daysOfWeek)
        }
    }

    private fun loadUserData() {
//        UserM.getUserData().observe(viewLifecycleOwner) { userData ->
//            binding.txtUsername.text = userData.loginName
//            // Load user avatar if necessary
//        }
//
//        UserM.getDataRanking().observe(viewLifecycleOwner) {
//            currentPoint = it.currentScore
//            configureUserRank()
//        }


        userViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            run {
                result.onSuccess {
                    binding.txtUsername.text = it.loginName
                    // Load user avatar if necessary
                }.onFailure { }
            }
        }


        homeViewModel.rankResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { it ->
                if (currentPoint > 7000) {
                    binding.btnUpgrade.visibility = View.GONE
                    binding.txtVerified.visibility = View.VISIBLE
                    binding.btnUpgrade.setOnClickListener {
                        context?.let { it1 ->
                            MaterialAlertDialogBuilder(it1)
                                .setTitle(resources.getString(R.string.premium_account))
                                .setMessage(resources.getString(R.string.premium_account_desc))
                                .setNegativeButton(resources.getString(R.string.close)) { dialog, which ->
                                    run {
                                        dialog.dismiss()
                                    }
                                }.show()
                        }
                    }
                } else {
                    binding.btnUpgrade.visibility = View.VISIBLE
                    binding.txtVerified.visibility = View.GONE
                    binding.btnUpgrade.setOnClickListener {
                        val i = Intent(context, QuizletPlus::class.java)
                        startActivity(i)
                    }
                }
            }.onFailure {

            }
        }


    }

    private fun loadAchievements() {
        val today = java.time.LocalDate.now()
        val achievedDays = mutableListOf<String>()

//        UserM.getDataAchievements().observe(viewLifecycleOwner) {
//            binding.txtCountStreak.text = "${it.streak.currentStreak}-days streak"
//
//            val startStreakDate = today.minusDays(it.streak.currentStreak.toLong())
//            for (i in 0 until it.streak.currentStreak) {
//                achievedDays.add(startStreakDate.plusDays(i.toLong()).format(DateTimeFormatter.ofPattern("d")))
//            }
//
//            val sundayOfLastWeek = today.minusWeeks(1).with(DayOfWeek.SUNDAY)
//            val formattedDays = (0 until 7).map {
//                sundayOfLastWeek.plusDays(it.toLong()).format(DateTimeFormatter.ofPattern("d"))
//            }
//
//            val dayAdapter = AdapterCustomDatePicker(formattedDays, achievedDays)
//            binding.rvCustomDatePicker.layoutManager =
//                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            binding.rvCustomDatePicker.adapter = dayAdapter
//        }
    }

    private fun configureUserRank() {
        if (currentPoint > 7000) {
            binding.btnUpgrade.visibility = View.GONE
            binding.txtVerified.visibility = View.VISIBLE
        } else {
            binding.btnUpgrade.visibility = View.VISIBLE
            binding.txtVerified.visibility = View.GONE
            binding.btnUpgrade.setOnClickListener {
                context?.let { context ->
                    MaterialAlertDialogBuilder(context)
                        .setTitle(resources.getString(R.string.premium_account))
                        .setMessage(resources.getString(R.string.premium_account_desc))
                        .setNegativeButton(resources.getString(R.string.close)) { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
        }
    }

    private fun requestStoragePermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
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
        lifecycleScope.launch {
            context?.let { showLoading(it, resources.getString(R.string.upload_avatar_loading)) }
            try {
                val base64String = filePath?.let { convertFileToBase64(it) }
                val requestBody = createAvatarRequestBody(base64String)
//                val result = apiService.updateUserInfo(Helper.getDataUserId(requireContext()), requestBody)

//                handleApiResponse(result, uri)
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

    private fun createAvatarRequestBody(base64String: String?): okhttp3.RequestBody {
        val json = Gson().toJson(base64String?.let { UpdateUserResponse(avatar = it) })
        return RequestBody.create("application/json".toMediaTypeOrNull(), json)
    }

    private fun handleApiResponse(result: okhttp3.ResponseBody<UpdateUserResponse>, uri: Uri) {
        if (result.isSuccessful) {
            context?.let {
                CustomToast(it).makeText(
                    it,
                    resources.getString(R.string.upload_avatar),
                    CustomToast.LONG,
                    CustomToast.SUCCESS
                ).show()
                Glide.with(this).load(uri).into(binding.imgAvatar)
            }
        } else {
            context?.let {
                CustomToast(it).makeText(
                    it,
                    resources.getString(R.string.upload_avatar_err),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }
    }

    private fun showLoading(context: Context, msg: String) {
        progressDialog = ProgressDialog.show(context, null, msg)
    }

    private fun convertFileToBase64(filePath: String): String {
        return try {
            val file = File(filePath)
            val bytes = ByteArray(file.length().toInt())
            FileInputStream(file).use { it.read(bytes) }
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

}