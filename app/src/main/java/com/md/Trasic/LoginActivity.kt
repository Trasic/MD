package com.md.Trasic

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.md.Trasic.databinding.ActivityLoginBinding
import com.md.Trasic.factory.TrasicViewModelFactory
import com.md.Trasic.helper.IGeneralSetup
import com.md.Trasic.repository.TrasicRepository
import com.md.Trasic.view_model.TrasicViewModel

class LoginActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: TrasicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        viewModel = ViewModelProvider(
            this, TrasicViewModelFactory(TrasicRepository.getTrasicRepository())
        )[TrasicViewModel::class.java]

        setup()
        observerCall()

        if (auth.currentUser != null) {
            onSuccess()
        }
    }

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun setup() {
        auth = Firebase.auth
        setupGoogleSignIn()
    }

    override fun enableControl(isEnabled: Boolean) {
        binding.progressBar.isVisible = !isEnabled
        binding.googleSignIn.isEnabled = isEnabled
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val gsc = GoogleSignIn.getClient(this, gso)

        binding.googleSignIn.setOnClickListener {
            enableControl(false)
            launchGoogleSignIn.launch(gsc.signInIntent)
        }
    }

    private val launchGoogleSignIn = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val response = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val user = response.getResult(ApiException::class.java)!!
                authWithGoogle(user.idToken!!)
            } catch (e: ApiException) {
                onFailed()
            }
        } else {
            onFailed()
        }
    }

    private fun authWithGoogle(token: String) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null))
            .addOnCompleteListener(this) { response ->
                if (response.isSuccessful) onSuccess()
                else onFailed()
            }.addOnFailureListener { onFailed() }.addOnCanceledListener { onFailed() }
    }

    private fun onSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun onFailed() {
        auth.signOut()

        enableControl(true)
        Toast.makeText(this, getString(R.string.sign_in_failed), Toast.LENGTH_SHORT).show()
    }
}