package com.wordmemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wordmemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            // 立即执行 Fragment 事务，确保 NavHostFragment 就绪后再设置 NavController
            supportFragmentManager.executePendingTransactions()
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            navHostFragment?.let {
                binding.navView.setupWithNavController(it.navController)
            }
        } catch (e: Exception) {
            setContentView(android.widget.TextView(this).apply {
                text = "启动失败，请重启应用\n\n${e.message}"
                setPadding(48, 48, 48, 48)
            })
        }
    }
}
