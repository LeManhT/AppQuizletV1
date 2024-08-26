package com.example.quizletappandroidv1.ui.activity

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController
    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.navHostAuthFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

//    override fun onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed()
//            finishAffinity()
//        } else {
//            this.doubleBackToExitPressedOnce = true
//            Toast.makeText(
//                this,
//                resources.getString(R.string.back_press_again_to_out_app),
//                Toast.LENGTH_SHORT
//            ).show()
//
//            Handler().postDelayed({
//                doubleBackToExitPressedOnce = false
//            }, 2000)
//        }
//    }

}