package com.example.quizletappandroidv1.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.quizletappandroidv1.ActivityAdmin
import com.example.quizletappandroidv1.databinding.ActivityFragmentAdminDashboardBinding

class ActivityAdminDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityFragmentAdminDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFragmentAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetStarted.setOnClickListener {
            val intent = Intent(this, ActivityAdmin::class.java)
            startActivity(intent)
        }

    }
}