package com.md.Trasic.view_model

import androidx.lifecycle.ViewModel
import com.md.Trasic.repository.TrasicRepository
import okhttp3.MultipartBody

class TrasicViewModel(private val repository: TrasicRepository) : ViewModel() {
    fun predictTrasic(file: MultipartBody.Part) = repository.predictTrasic(file)

    fun getTrasicList(
        limit: Int? = null,
        searchQuery: String? = null,
    ) = repository.getTrasicList(limit, searchQuery)

    fun getTrasicDetail(id: String) = repository.getTrasicDetail(id)
}