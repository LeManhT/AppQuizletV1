package com.example.quizletappandroidv1.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentStudySetThisBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class StudySetThisFragment : DialogFragment() {
    private lateinit var binding: FragmentStudySetThisBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudySetThisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        var userData = UserM.getUserData()
//        userData.observe(this, Observer { userResponse ->
//            val studySet = Helper.getAllStudySets(userResponse).find { listStudySets ->
//                listStudySets.id == setId
//            }
//            if (studySet != null) {
//                listCards.clear()
//                listCards.addAll(studySet.cards)
//            }
//
//            jsonList = Gson().toJson(listCards)
//
//
//        })

        binding.layoutLearn.setOnClickListener {
            val i = Intent(context, FlashcardLearn::class.java)
//            i.putExtra("listCard", jsonList)
//            jsonList?.let { it1 -> Log.d("ggg", it1) }
            startActivity(i)
        }
        binding.layoutTest.setOnClickListener {
            val i = Intent(context, ReviewLearn::class.java)
//            i.putExtra("listCardTest", jsonList)
//            jsonList?.let { it1 -> Log.d("ggg1", it1) }
            startActivity(i)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.fragment_study_set_this)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet =
                (dialogInterface as BottomSheetDialog).findViewById<View>(R.id.fragmentStudyThisSetBottomsheet)
            val closeIcon = dialog.findViewById<TextView>(R.id.txtStudyThisSetCloseIcon)
            closeIcon.setOnClickListener {
                dismiss()
            }
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels - 60
            bottomSheet?.minimumHeight = screenHeight
            val behavior = dialogInterface.behavior
            behavior.peekHeight = screenHeight
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }
}