package com.example.quizletappandroidv1.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.appquizlet.model.SearchSetModel
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.AdapterCustomDatePicker
import com.example.quizletappandroidv1.adapter.DayOfWeekAdapter
import com.example.quizletappandroidv1.adapter.RVFolderItemAdapter
import com.example.quizletappandroidv1.adapter.RvStudySetItemAdapter
import com.example.quizletappandroidv1.adapter.SearchListAdapter
import com.example.quizletappandroidv1.custom.CustomDialog
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.custom.EqualSpacingItemDecoration
import com.example.quizletappandroidv1.customview.PodiumView
import com.example.quizletappandroidv1.databinding.FragmentHomeBinding
import com.example.quizletappandroidv1.listener.ItemTouchHelperAdapter
import com.example.quizletappandroidv1.listener.RVFolderItem
import com.example.quizletappandroidv1.models.FolderModel
import com.example.quizletappandroidv1.models.StudySetModel
import com.example.quizletappandroidv1.viewmodel.home.HomeViewModel
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
    private lateinit var adapterSearchSet: SearchListAdapter
    private var studysetLists: MutableList<SearchSetModel> = mutableListOf()


    private val documentViewModel: DocumentViewModel by viewModels()
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

        MyApplication.userId?.let { userViewModel.getUserData(it) }
        documentViewModel.getAllStudySets()

        binding.textView9.setOnClickListener {
            val successIcon = ContextCompat.getDrawable(requireContext(), R.drawable.vietnam_flag)
            val customDialog = CustomDialog(requireContext())

            // Hiển thị Success Dialog
            customDialog.showDialog(
                icon = successIcon!!,
                title = "SUCCESS!",
                message = "Thank you for your request. We are working hard to find the best service and deals for you.",
                buttonText = "Continue"
            ) {
                // Handle button click action (e.g., navigate, close, etc.)
            }
        }

        streakTextView = binding.txtCountStreak

        homeViewModel.rankResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { it ->
                val podiumView: PodiumView = view.findViewById(R.id.podiumView)
                podiumView.setPodiumData(
                    it, it, it,
                    item1 = "2" to "Avenger",
                    item2 = "1" to "Super hero",
                    item3 = "3" to "Hero"
                )

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

        binding.imgNotification.setOnClickListener {
            showDialogBottomSheet()
        }

        binding.btnOpenRankLeaderBoard.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome3_to_rankLeaderBoard)
        }
        binding.txtViewDetailLeaderBoard.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome3_to_rankLeaderBoard)
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
                    val action =
                        FragmentHomeDirections.actionFragmentHome3ToFolderDetail(folderItem.id)
                    findNavController().navigate(action)
                }
            })

        adapterHomeStudySet =
            RvStudySetItemAdapter(requireContext(), object : RVStudySetItem {
                override fun handleClickStudySetItem(setItem: StudySetModel, position: Int) {
                    val action =
                        FragmentHomeDirections.actionFragmentHome3ToStudySetDetail(setItem.id)
                    findNavController().navigate(action)
                }
            }, false)

        rvHomeFolder.adapter = adapterHomeFolder
        rvStudySet.adapter = adapterHomeStudySet


//PagerSnapHelper will provide the smooth swipe effect in the horizontal RecyclerView,
        val snapHelper = PagerSnapHelper()
        val snapHelperFolder = PagerSnapHelper()
        snapHelperFolder.attachToRecyclerView(binding.rvHomeFolders)
        snapHelper.attachToRecyclerView(binding.rvHomeStudySet)

        userViewModel.userData.observe(viewLifecycleOwner) { result ->

            result.onSuccess { userResponse ->
                if (userResponse.documents.folders.isEmpty()) {
                    binding.rvHomeFolders.visibility = View.GONE
                    binding.noDataHomeFolder.visibility = View.VISIBLE
                } else {
                    binding.rvHomeFolders.visibility = View.VISIBLE
                    binding.noDataHomeFolder.visibility = View.GONE
                    adapterHomeFolder.updateData(userResponse.documents.folders)
                }

                if (userResponse.documents.studySets.isEmpty()) {
                    binding.rvHomeStudySet.visibility = View.GONE
                    binding.noDataHomeSet.visibility = View.VISIBLE
                } else {
                    binding.rvHomeStudySet.visibility = View.VISIBLE
                    binding.noDataHomeSet.visibility = View.GONE
                    adapterHomeStudySet.updateData(userResponse.documents.studySets)
                }
                saveIdUser(
                    userResponse.id,
                    userResponse.userName,
                    userResponse.loginPassword,
                    true
                )
            }.onFailure { exception ->
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
                viewHolder.itemView.animate().translationY(0f).alpha(1f).setDuration(300).start()
            }

        })

        itemTouchHelper.attachToRecyclerView(rvHomeFolder)

//        rvHomeFolder.isScrollbarFadingEnabled = false

        val recyclerViewDayOfWeek: RecyclerView = binding.rvDayOfWeek
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
        val dayOfWeekAdapter = DayOfWeekAdapter(daysOfWeek)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.pad_20)
        recyclerViewDayOfWeek.addItemDecoration(EqualSpacingItemDecoration(spacingInPixels))
        recyclerViewDayOfWeek.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDayOfWeek.adapter = dayOfWeekAdapter

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

        lifecycleScope.launch {
            documentViewModel.allStudySets.observe(viewLifecycleOwner) { contacts ->
                run {
                    if (contacts.isEmpty()) {
                        binding.layoutNoData.visibility = View.VISIBLE
                        binding.rvSearchStudySet.visibility = View.GONE
                        binding.layoutNoDataSearch.visibility = View.VISIBLE
                    } else {
                        binding.layoutNoData.visibility = View.GONE
                        binding.rvSearchStudySet.visibility = View.VISIBLE
                        binding.layoutNoDataSearch.visibility = View.GONE
                    }
                    adapterSearchSet =
                        SearchListAdapter(object : SearchListAdapter.ISearchSetClick {
                            override fun handleSearchSetClick(studyset: SearchSetModel) {
                                val action =
                                    FragmentHomeDirections.actionFragmentHome3ToStudySetDetail(
                                        studyset.id
                                    )
                                findNavController().navigate(action)
                            }
                        })

                    binding.rvSearchStudySet.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    binding.rvSearchStudySet.adapter = adapterSearchSet

                    adapterSearchSet.updateData(contacts)
                    studysetLists.clear()
                    studysetLists.addAll(contacts)

                    binding.searchView.editText.addTextChangedListener(object :
                        TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?, start: Int, count: Int, after: Int
                        ) {
                            // Trước khi văn bản thay đổi
                        }

                        override fun onTextChanged(
                            s: CharSequence?, start: Int, before: Int, count: Int
                        ) {
                            if (s?.isEmpty() == true) {
                                studysetLists.clear()
                                studysetLists.addAll(contacts)
                                adapterSearchSet.updateData(studysetLists)
                            } else {
                                searchSets(s.toString())
                            }
                        }

                        override fun afterTextChanged(s: Editable?) {
                            // Sau khi văn bản thay đổi
                        }
                    })
                }
            }
        }

//        binding.searchView.editText.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
//                val action = FragmentHomeDirections.actionFragmentHome3ToSearchResultsFragment(
//                    binding.searchView.editText.text.toString()
//                )
//                findNavController().navigate(action)
//                true
//            } else {
//                false
//            }
//        }


        documentViewModel.studySetSearchResults.observe(viewLifecycleOwner) { filteredContacts ->
            if (filteredContacts.isEmpty()) {
                binding.rvSearchStudySet.visibility = View.GONE
                binding.layoutNoDataSearch.visibility = View.VISIBLE
            } else {
                binding.rvSearchStudySet.visibility = View.VISIBLE
                binding.layoutNoDataSearch.visibility = View.GONE
            }
            adapterSearchSet.updateData(filteredContacts)
        }

    }

    private fun searchSets(query: String) {
        lifecycleScope.launch {
            documentViewModel.findStudySet(query)
        }
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


    private fun saveIdUser(
        userId: String,
        userName: String,
        password: String,
        isLoggedIn: Boolean
    ) {
        sharedPreferences =
            requireActivity().getSharedPreferences("idUser", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("key_userid", userId)
        editor.putString("key_userPass", password)
        editor.putString("key_username", userName)
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

}