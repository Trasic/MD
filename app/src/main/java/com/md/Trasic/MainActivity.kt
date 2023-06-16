package com.md.Trasic

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.md.Trasic.databinding.ActivityMainBinding
import com.md.Trasic.helper.IGeneralSetup

class MainActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setup()
    }

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.bottomNav.apply {
            if (selectedItemId == R.id.scan_fragment) {
                selectedItemId = R.id.home_fragment
            }
        }
    }

    override fun setup() {
        val navCtrl = findNavController(R.id.nav_host_fragment)
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_fragment -> navCtrl.navigate(R.id.home_fragment)
                R.id.scan_fragment -> checkCameraPermission()
                R.id.setting_fragment -> navCtrl.navigate(R.id.setting_fragment)
            }
            true
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            launchCameraPermission.launch(Manifest.permission.CAMERA)
        } else openCamera()
    }

    private val launchCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) openCamera()
        else {
            Toast.makeText(
                this,
                getString(R.string.permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openCamera() {
        startActivity(Intent(this, CameraActivity::class.java))
    }
}