package com.md.Trasic

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.md.Trasic.data.AppPreference
import com.md.Trasic.helper.IGeneralSetup

class SettingFragment : PreferenceFragmentCompat(), IGeneralSetup {
    private lateinit var auth: FirebaseAuth
    private lateinit var signOutPref: Preference
    private lateinit var signInPref: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preference, rootKey)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setup()
    }

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun setup() {
        auth = Firebase.auth

        val langPref =
            findPreference<Preference>(resources.getString(R.string.language)) as Preference
        val aboutPref =
            findPreference<Preference>(resources.getString(R.string.about)) as Preference

        signInPref = findPreference<Preference>(resources.getString(R.string.sign_in)) as Preference
        signOutPref =
            findPreference<Preference>(resources.getString(R.string.sign_out)) as Preference

        langPref.setOnPreferenceClickListener {
            changeLanguage()
            true
        }

        aboutPref.setOnPreferenceClickListener {
            toggleAbout()
            true
        }

        signInPref.setOnPreferenceClickListener {
            resignIn()
            true
        }

        signOutPref.setOnPreferenceClickListener {
            promptSignOutDialog()
            true
        }

        toggleSignStatus()
    }

    private fun changeLanguage() = startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))

    private fun toggleAbout() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(resources.getString(R.string.about))
            setMessage(resources.getString(R.string.about_app))
        }.show()
    }

    private fun toggleSignStatus() {
        val hasUser = auth.currentUser != null && !auth.currentUser?.isAnonymous!!

        signOutPref.isVisible = hasUser
        signInPref.isVisible = !hasUser
    }

    private fun resignIn() {
        signOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    private fun signOut() {
        auth.signOut()
        AppPreference(requireContext()).clearToken()
    }

    private fun promptSignOutDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(resources.getString(R.string.sign_out))
            setMessage(resources.getString(R.string.confirm_sign_out))
            setNegativeButton(getString(R.string.no), null)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                resignIn()
            }
        }.show()
    }
}