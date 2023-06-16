package com.md.Trasic

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.md.Trasic.adapter.GridSpacingItemDecoration
import com.md.Trasic.data.TrasicResponse
import com.md.Trasic.databinding.ActivitySearchBinding
import com.md.Trasic.factory.TrasicViewModelFactory
import com.md.Trasic.helper.IGeneralSetup
import com.md.Trasic.helper.LoadingResult
import com.md.Trasic.helper.Utils
import com.md.Trasic.repository.TrasicRepository
import com.md.Trasic.view_model.TrasicViewModel


class SearchActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: TrasicViewModel

    private var searchView: SearchView? = null
    private var isViewAll = false
    private var listItem = ArrayList<TrasicResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = resources.getString(R.string.search)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = (menu.findItem(R.id.search)?.actionView as SearchView)
            .apply {
                queryHint = resources.getString(R.string.search)
                setSearchableInfo(searchManager.getSearchableInfo(componentName))
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            clearFocus()
                            search(query)
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean = false
                })
            }

        return true
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

    override fun setup() {
        binding.viewAllBtn.setOnClickListener { search() }

        if (intent.getBooleanExtra(VIEW_ALL_EXTRA, false)) {
            search()
        } else if (intent.getStringExtra(INITIAL_SEARCH_KEYWORD) != null) {
            search(intent.getStringExtra(INITIAL_SEARCH_KEYWORD))
        }
    }

    override fun enableControl(isEnabled: Boolean) {
        binding.viewAllBtn.visibility = if (!isEnabled) View.VISIBLE else View.INVISIBLE
        binding.foundText.text =
            if (isEnabled) resources.getString(R.string.all_item)
            else resources.getString(R.string.search_found, listItem.size)
    }

    private fun search(keyword: String? = null) {
        searchView?.apply {
            isIconified = true
            clearFocus()
        }

        viewModel.getTrasicList(searchQuery = keyword).observe(this) { list ->
            when (list) {
                is LoadingResult.Loading -> {
                    binding.searchIcon.isVisible = false
                    binding.searchPanel.isVisible = false
                    binding.progressBar.isVisible = true
                }

                is LoadingResult.Error -> {
                    onEndLoading()
                    Utils.toastNetworkError(this@SearchActivity)
                }

                is LoadingResult.Success -> {
                    listItem = list.data as ArrayList<TrasicResponse>
                    onEndLoading()
                }
            }
        }
    }

    private fun onEndLoading() {
        binding.searchPanel.isVisible = true
        binding.progressBar.isVisible = false
        enableControl(isViewAll)
        Utils.setupGridListView(
            this@SearchActivity,
            binding.gridRv,
            listItem,
            binding.errorView,
            binding.gridRv
        )
    }

    companion object {
        const val VIEW_ALL_EXTRA = "view_all_extra"
        const val INITIAL_SEARCH_KEYWORD = "initial_search_keyword"
    }
}