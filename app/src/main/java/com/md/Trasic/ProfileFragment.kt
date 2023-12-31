package com.md.Trasic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.md.Trasic.databinding.FragmentProfileBinding
import com.md.Trasic.helper.IGeneralSetup
import com.md.Trasic.helper.Utils

class ProfileFragment : Fragment(), IGeneralSetup {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        childFragmentManager.beginTransaction().replace(R.id.child_fragment, SettingFragment())
            .commit()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setup()
    }

    override fun setup() {
        auth = Firebase.auth
        binding.apply {
            auth.currentUser?.let {
                itemTitle.text = it.displayName
                itemSubtitle.text = it.email
                Glide.with(requireContext())
                    .load(it.photoUrl)
                    .placeholder(Utils.getCircularProgressDrawable(requireContext()))
                    .circleCrop()
                    .into(itemImage)
            }
        }
    }
}