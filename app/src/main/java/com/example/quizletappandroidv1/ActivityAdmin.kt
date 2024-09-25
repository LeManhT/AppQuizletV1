package com.example.quizletappandroidv1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.quizletappandroidv1.databinding.ActivityAdminBinding

class ActivityAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the NavHostFragment from the FragmentContainerView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentAdmin) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the BottomNavigationView with NavController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}