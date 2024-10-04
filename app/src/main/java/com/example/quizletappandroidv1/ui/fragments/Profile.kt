package com.example.quizletappandroidv1.ui.fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.AdapterCustomDatePicker
import com.example.quizletappandroidv1.adapter.DayOfWeekAdapter
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.custom.EqualSpacingItemDecoration
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class Profile : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding!!
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

    val userId = MyApplication.userId
    val timeDetect = System.currentTimeMillis()

    override
    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )
        initViews()
        loadUserData()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        Log.d("AddFragment", "Profile onViewCreated called")
    }

    private fun initViews() {
        binding.imgAvatar.setOnClickListener { findNavController().navigate(R.id.action_profile_to_viewImage) }
        binding.linearLayoutSettings.setOnClickListener {
            findNavController().navigate(
                R.id.action_profile_to_settings2
            )
        }
        binding.txtUploadImage.setOnClickListener { requestStoragePermission() }
        binding.txtViewAchievement.setOnClickListener {
            findNavController().navigate(
                R.id.action_profile_to_achievements
            )
        }
//        Custom date
        val recyclerViewDayOfWeek: RecyclerView = binding.rvDayOfWeek
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
        val dayOfWeekAdapter = DayOfWeekAdapter(daysOfWeek)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.pad_20)
        recyclerViewDayOfWeek.addItemDecoration(EqualSpacingItemDecoration(spacingInPixels))
        recyclerViewDayOfWeek.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDayOfWeek.adapter = dayOfWeekAdapter

        // Dùng RecyclerView cho Ngày trong Tháng
        val recyclerViewDay: RecyclerView = binding.rvCustomDatePicker

        // Lấy ngày hiện tại
        val currentDate = LocalDate.now()

        // Lấy ngày Chủ Nhật của tuần trước
        val sundayOfLastWeek = currentDate.minusWeeks(1).with(DayOfWeek.SUNDAY)

        // Tạo danh sách 7 ngày từ Chủ Nhật đến Thứ Bảy
        val daysInWeek = (0 until 7).map { index ->
            sundayOfLastWeek.plusDays(index.toLong())
        }

        // Chuyển đổi danh sách ngày thành danh sách chuỗi
        val formattedDays = daysInWeek.map { day ->
            day.format(DateTimeFormatter.ofPattern("d")) // Định dạng là số ngày (1, 2, 3, ...)
        }
        // Lấy ngày hiện tại
        val today = LocalDate.now()
        val achievedDays = mutableListOf<String>()

        if (userId != null) {
            homeViewModel.getDataAchievement(userId, timeDetect)
        }


        homeViewModel.dataAchievement.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Log.d("currentStreakkk", it.streak.currentStreak.toString())
                updateStreakText(it.streak.currentStreak)
//                if (it.streak.currentStreak > sharedPreferences.getInt("countStreak", 0)
//                ) {
//                    saveCountStreak(it.streak.currentStreak)
//                }
                // Tính ngày bắt đầu streak hiện tại
                val startStreakDate = today.minusDays(it.streak.currentStreak.toLong())
                // In ngày bắt đầu và ngày kết thúc của streak hiện tại
                println("Ngày bắt đầu streak hiện tại: $startStreakDate")
                println("Ngày kết thúc streak hiện tại: $today")

                // Nếu bạn muốn lấy danh sách các ngày đã đạt được streak, bạn có thể sử dụng vòng lặp
                for (i in 0 until it.streak.currentStreak) {
                    achievedDays.add(
                        startStreakDate.plusDays(i.toLong())
                            .format(DateTimeFormatter.ofPattern("d"))
                    )
                }
                val formattedAchieveDays = achievedDays.map { day ->
                    day.format(DateTimeFormatter.ofPattern("d")) // Định dạng là số ngày (1, 2, 3, ...)
                }


                val dayAdapter = AdapterCustomDatePicker(formattedDays, formattedAchieveDays)
                recyclerViewDay.addItemDecoration(EqualSpacingItemDecoration(5))
                recyclerViewDay.layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.HORIZONTAL, false
                ) // Hiển thị 7 cột
                recyclerViewDay.adapter = dayAdapter
            }.onFailure {

            }
        }

    }

    private fun loadUserData() {
        userViewModel.userData.observe(viewLifecycleOwner) { result ->
            run {
                result.onSuccess {
                    binding.txtUsername.text =
                        it.loginName
                    // Load user avatar if necessary
                }.onFailure { }
            }
        }
        homeViewModel.rankResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { it ->
                if (currentPoint > 7000) {
                    binding.btnUpgrade.visibility =
                        View.GONE
                    binding.txtVerified.visibility = View.VISIBLE
                    binding.btnUpgrade.setOnClickListener {
                        context?.let { it1 ->
                            MaterialAlertDialogBuilder(it1).setTitle(resources.getString(R.string.premium_account))
                                .setMessage(resources.getString(R.string.premium_account_desc))
                                .setNegativeButton(resources.getString(R.string.close)) { dialog, which -> run { dialog.dismiss() } }
                                .show()
                        }
                    }
                } else {
                    binding.btnUpgrade.visibility =
                        View.VISIBLE
                    binding.txtVerified.visibility = View.GONE
                    binding.btnUpgrade.setOnClickListener {
                        findNavController().navigate(R.id.action_profile_to_quizletPlus)
                    }
                }
            }.onFailure { }
        }
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
                Glide.with(this@Profile).load(uri).into(binding.imgAvatar)
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
        return okhttp3.RequestBody.create("application/json".toMediaTypeOrNull(), json)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    "Permission to access storage is required to select an image.",
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

    private fun updateStreakText(streakCount: Int) {
        binding.txtCountStreak.text = "$streakCount-days streak"
    }
}