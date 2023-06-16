package com.md.Trasic.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.md.Trasic.repository.TrasicRepository
import com.md.Trasic.view_model.TrasicViewModel

class TrasicViewModelFactory(private val repository: TrasicRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrasicViewModel::class.java))
            return TrasicViewModel(repository) as T
        throw IllegalArgumentException("${modelClass.name} is not supported by this factory")
    }
}