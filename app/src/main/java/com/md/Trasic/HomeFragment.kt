package com.md.Trasic

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.md.Trasic.adapter.GridSpacingItemDecoration
import com.md.Trasic.databinding.FragmentHomeBinding
import com.md.Trasic.factory.TrasicViewModelFactory
import com.md.Trasic.helper.IGeneralSetup
import com.md.Trasic.helper.LoadingResult
import com.md.Trasic.helper.Utils
import com.md.Trasic.repository.TrasicRepository
import com.md.Trasic.view_model.TrasicViewModel


class HomeFragment : Fragment(), IGeneralSetup {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: TrasicViewModel

    private val trasicListLimit = 4

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        viewModel = ViewModelProvider(
            this, TrasicViewModelFactory(TrasicRepository.getTrasicRepository())
        )[TrasicViewModel::class.java]

        binding.gridRv.apply {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(GridSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small)))
            setItemViewCacheSize(Utils.MAX_VIEW_CACHE)
        }

        setup()
        observerCall()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                startActivity(Intent(requireContext(), SearchActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setup() {
        binding.viewAllBtn.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    SearchActivity::class.java
                ).apply { putExtra(SearchActivity.VIEW_ALL_EXTRA, true) }
            )
        }

        binding.refreshBtn.setOnClickListener { refresh() }
    }

    override fun observerCall() {
        viewModel.getTrasicList(limit = trasicListLimit).observe(this) { list ->
            when (list) {
                is LoadingResult.Loading -> binding.progressBar.isVisible = true
                is LoadingResult.Error -> {
                    binding.progressBar.isVisible = false
                    binding.errorView.isVisible = true
                    binding.gridRv.isVisible = false
                    Utils.toastNetworkError(requireContext())
                }

                is LoadingResult.Success -> {
                    binding.progressBar.isVisible = false
                    Utils.setupGridListView(
                        requireContext(),
                        binding.gridRv,
                        list.data,
                        binding.errorView,
                        binding.gridRv
                    )
                }
            }
        }
    }

    override fun refresh() {
        observerCall()
        binding.gridRv.isVisible = false
        binding.errorView.isVisible = false
        binding.progressBar.isVisible = true
    }
}