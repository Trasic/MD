package com.md.Trasic

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.md.Trasic.data.TrasicResponse
import com.md.Trasic.databinding.ActivityDetailsBinding
import com.md.Trasic.factory.TrasicViewModelFactory
import com.md.Trasic.helper.IGeneralSetup
import com.md.Trasic.helper.LoadingResult
import com.md.Trasic.helper.Utils
import com.md.Trasic.repository.TrasicRepository
import com.md.Trasic.view_model.TrasicViewModel

class DetailsActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: TrasicViewModel

    private var trasicDetail: TrasicResponse? = null
    private var trasicId = "-1"
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = resources.getString(R.string.details_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setResult(DETAILS_RESULT_CODE)

        viewModel = ViewModelProvider(
            this, TrasicViewModelFactory(TrasicRepository.getTrasicRepository())
        )[TrasicViewModel::class.java]

        trasicId = intent.getStringExtra(TRASIC_ID_EXTRA).toString()
        if (trasicId == "-1") onBackPressed()

        setup()
        observerCall()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun setup() {
        onLoading()

        binding.refreshBtn.setOnClickListener { onLoading() }
        binding.listenBtn.setOnClickListener {
            if (mediaPlayer?.isPlaying != true && trasicDetail != null) {
                val musicSource = Uri.parse(trasicDetail?.soundUrl)
                mediaPlayer = MediaPlayer.create(this, musicSource)
                mediaPlayer?.isLooping = false
                mediaPlayer?.start()
            }
        }
    }

    override fun observerCall() {
        viewModel.getTrasicDetail(trasicId).observe(this) { detail ->
            when (detail) {
                is LoadingResult.Loading -> onLoading()
                is LoadingResult.Error -> {
                    binding.progressBar.isVisible = false
                    onError()
                }

                is LoadingResult.Success -> {
                    trasicDetail = detail.data
                    onSuccess()
                }
            }
        }
    }

    private fun onLoading() {
        viewModel.getTrasicDetail(trasicId)

        binding.progressBar.isVisible = true
        binding.mainView.isVisible = false
        binding.errorView.isVisible = false
    }

    private fun onError() {
        Utils.toastNetworkError(this)
        binding.mainView.isVisible = false
        binding.errorView.isVisible = true
    }

    private fun onSuccess() {
        Glide.with(this)
            .load(trasicDetail?.imageUrl)
            .placeholder(Utils.getCircularProgressDrawable(this))
            .into(binding.instrumentImage)
        binding.itemTitle.text = trasicDetail?.name
        binding.itemDesc.text = trasicDetail?.description

        binding.mainView.isVisible = true
        binding.errorView.isVisible = false
    }

    companion object {
        const val TRASIC_ID_EXTRA = "TRASIC_ID_EXTRA"
        const val DETAILS_RESULT_CODE = 180
    }
}