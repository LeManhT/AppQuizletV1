package com.example.quizletappandroidv1.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizletappandroidv1.adapter.admin.ManageUserAdapter
import com.example.quizletappandroidv1.databinding.FragmentManageUserBinding
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.viewmodel.admin.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FragmentManageUser : Fragment() {
    private lateinit var binding: FragmentManageUserBinding
    private val adminViewModel: AdminViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageUserBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listUserAdapter = ManageUserAdapter(object : ManageUserAdapter.IUserAdminClick {
            override fun handleDeleteClick(user: UserResponse) {

            }

            override fun handleEditClick(user: UserResponse) {

            }
        })

        adminViewModel.getListUserAdmin(0, 10)

        binding.recyclerViewUsers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewUsers.adapter = listUserAdapter

        adminViewModel.allUser.observe(viewLifecycleOwner) { result ->
            run {
                result.onSuccess {
                    if (it.isEmpty()) {
                        binding.recyclerViewUsers.visibility = View.GONE
                        binding.layoutNoData.visibility = View.VISIBLE
                    } else {
                        binding.recyclerViewUsers.visibility = View.VISIBLE
                        binding.layoutNoData.visibility = View.GONE
                    }
                    listUserAdapter.updateData(it)
                }.onFailure {
                    Timber.log(1, it.message.toString())
                }
            }
        }

    }

}