package com.example.quizletappandroidv1.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.AdapterCustomDatePicker
import com.example.quizletappandroidv1.adapter.DayOfWeekAdapter
import com.example.quizletappandroidv1.adapter.RVFolderItemAdapter
import com.example.quizletappandroidv1.adapter.RvStudySetItemAdapter
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.custom.EqualSpacingItemDecoration
import com.example.quizletappandroidv1.customview.PodiumView
import com.example.quizletappandroidv1.databinding.FragmentHomeBinding
import com.example.quizletappandroidv1.listener.ItemTouchHelperAdapter
import com.example.quizletappandroidv1.listener.RVFolderItem
import com.example.quizletappandroidv1.models.FolderModel
import com.example.quizletappandroidv1.models.RankItemModel
import com.example.quizletappandroidv1.models.RankResultModel
import com.example.quizletappandroidv1.models.RankSystem
import com.example.quizletappandroidv1.models.StudySetModel
import com.example.quizletappandroidv1.viewmodel.home.HomeViewModel
import com.example.quizletappandroidv1.viewmodel.studyset.StudySetViewModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class FragmentHome : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var streakTextView: TextView
    private lateinit var adapterHomeStudySet: RvStudySetItemAdapter
    private lateinit var adapterHomeFolder: RVFolderItemAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesTheme: SharedPreferences
    private var apiCallsInProgress = false

    private val studySetViewModel: StudySetViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    val userId = MyApplication.userId
    val timeDetect = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences = context?.getSharedPreferences("currentStreak", Context.MODE_PRIVATE)!!
        sharedPreferencesTheme =
            context?.getSharedPreferences("changeTheme", Context.MODE_PRIVATE)!!
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetView = view.findViewById<View>(R.id.notification_bottomsheet)
        if (bottomSheetView != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
            bottomSheetView.let {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        streakTextView = binding.txtCountStreak

        homeViewModel.rankResult.observe(viewLifecycleOwner) { result ->

            result.onSuccess { it ->
                if (it.currentScore > 7000) {
                    binding.btnUpgradeFeature.visibility = View.GONE
                    binding.txtVerified.visibility = View.VISIBLE
                    binding.txtVerified.setOnClickListener {
                        MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.premium_account))
                            .setMessage(resources.getString(R.string.premium_account_desc))
                            .setNegativeButton(resources.getString(R.string.close)) { dialog, which ->
                                run {
                                    dialog.dismiss()
                                }
                            }.show()
                    }
                } else {
                    binding.btnUpgradeFeature.visibility = View.VISIBLE
                    binding.txtVerified.visibility = View.GONE
                    binding.btnUpgradeFeature.setOnClickListener {
                        findNavController().navigate(R.id.action_fragmentHome3_to_quizletPlus)
                    }
                }
            }.onFailure {

            }
        }

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.searchView.clearFocus()
                findNavController().navigate(R.id.action_fragmentHome3_to_fragmentSearch)
            }
        }

        binding.imgNotification.setOnClickListener {
            showDialogBottomSheet()
        }

        binding.btnOpenRankLeaderBoard.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome3_to_rankLeaderBoard)
        }
        binding.txtViewDetailLeaderBoard.setOnClickListener {
            val i = Intent(context, RankLeaderBoard::class.java)
            startActivity(i)
        }

        binding.txtViewAllQuote.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome3_to_quoteInLanguage)
        }

        binding.txtGoQuote.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome3_to_quoteInLanguage)
        }


        binding.txtFolderViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome3_to_fragmentLibrary2)
        }

        binding.txtStudySetViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome3_to_fragmentLibrary2)
        }
        val rvHomeFolder = binding.rvHomeFolders
        rvHomeFolder.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )

        val rvStudySet = binding.rvHomeStudySet
        rvStudySet.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        adapterHomeFolder =
            RVFolderItemAdapter(requireContext(), object : RVFolderItem {
                override fun handleClickFolderItem(folderItem: FolderModel, position: Int) {
//                    val i = Intent(context, FolderClickActivity::class.java)
//                    i.putExtra("idFolder", listFolderItems[position].id)
//                    startActivity(i)
                    findNavController().navigate(R.id.action_fragmentHome3_to_folderDetail)
                }
            })

        adapterHomeStudySet =
            RvStudySetItemAdapter(requireContext(), object : RVStudySetItem {
                override fun handleClickStudySetItem(setItem: StudySetModel, position: Int) {
                    val intent = Intent(requireContext(), StudySetDetail::class.java)
                    startActivity(intent)
                }
            }, false)

        // Access the RecyclerView through the binding
        rvHomeFolder.adapter = adapterHomeFolder
        rvStudySet.adapter = adapterHomeStudySet


//PagerSnapHelper will provide the smooth swipe effect in the horizontal RecyclerView,
        val snapHelper = PagerSnapHelper()
        val snapHelperFolder = PagerSnapHelper()
        snapHelperFolder.attachToRecyclerView(binding.rvHomeFolders)
        snapHelper.attachToRecyclerView(binding.rvHomeStudySet)

        userViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { userResponse ->

                Log.d("fd1", "Vap 6")


                if (userResponse.documents.folders.isEmpty()) {
                    binding.rvHomeFolders.visibility = View.GONE
                    binding.noDataHomeFolder.visibility = View.VISIBLE
                    Log.d("fd1", "Vap 0")
                } else {
                    binding.rvHomeFolders.visibility = View.VISIBLE
                    binding.noDataHomeFolder.visibility = View.GONE
                    Log.d("fd1", "Vap 1")
                    adapterHomeFolder.updateData(userResponse.documents.folders)
                }

                if (userResponse.documents.studySets.isEmpty()) {
                    binding.rvHomeStudySet.visibility = View.GONE
                    binding.noDataHomeSet.visibility = View.VISIBLE
                    Log.d("fd1", "Vap 3")
                } else {
                    binding.rvHomeStudySet.visibility = View.VISIBLE
                    binding.noDataHomeSet.visibility = View.GONE
                    adapterHomeStudySet.updateData(userResponse.documents.studySets)
                    Log.d("fd1", "Vap 4")
                }
            }.onFailure { exception ->
                Log.d("fd1", "Vap 5")
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    exception.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }

        binding.txtViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome3_to_achievements)
        }

        binding.txtViewAllTranslate.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome3_to_translate)
        }

        binding.rvCustomDatePicker.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome3_to_achievements)
        }

        binding.txtTranslatePararaph.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome3_to_translate)
        }


        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Gọi phương thức onItemMove từ Adapter khi item được di chuyển
                return (recyclerView.adapter as ItemTouchHelperAdapter).onItemMove(
                    viewHolder.adapterPosition, target.adapterPosition
                )
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Toast.makeText(context, "ffff", Toast.LENGTH_SHORT).show()
            }


            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.7f // Giảm độ mờ của item khi đang được kéo
                }
            }

            override fun clearView(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                // Đặt lại thuộc tính khi kéo kết thúc
                viewHolder.itemView.animate().translationY(0f).alpha(1f).setDuration(300).start()
            }

        })

        itemTouchHelper.attachToRecyclerView(rvHomeFolder)

//        rvHomeFolder.isScrollbarFadingEnabled = false


//        Custom date/
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
                if (it.streak.currentStreak > sharedPreferences.getInt("countStreak", 0)
                ) {
                    saveCountStreak(it.streak.currentStreak)
                }
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

        val podiumView: PodiumView = view.findViewById(R.id.podiumView)
        podiumView.setPodiumData(
            RankResultModel(
                100, 1,
                RankSystem(
                    listOf(
                        RankItemModel(
                            21,
                            1,
                            "lemanh",
                            "lemanh@gmail.com",
                            "05/09/2002",
                            1
                        )
                    )
                )
            ),
            RankResultModel(
                100, 2,
                RankSystem(
                    listOf(
                        RankItemModel(
                            21,
                            1,
                            "lemanh",
                            "lemanh@gmail.com",
                            "05/09/2002",
                            1
                        )
                    )
                )
            ),
            RankResultModel(
                100, 3,
                RankSystem(
                    listOf(
                        RankItemModel(
                            21,
                            1,
                            "lemanh",
                            "lemanh@gmail.com",
                            "05/09/2002",
                            1
                        )
                    )
                )
            ),
            item1 = "100" to "Avengers",
            item2 = "200" to "Teams",
            item3 = "4000" to "Walkers"
        )



    }

    private fun showDialogBottomSheet() {
        val notificationBottomSheet = Notification()
        //        parentFragmentManager được sử dụng để đảm bảo rằng Bottom Sheet Dialog được hiển thị trong phạm vi của Fragment.
//        notificationBottomSheet.show(parentFragmentManager, notificationBottomSheet.tag)
    }


    private fun updateStreakText(streakCount: Int) {
        streakTextView.text = "$streakCount-days streak"
    }


    private fun saveCountStreak(streak: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("countStreak", streak)
        editor.apply()
    }



}