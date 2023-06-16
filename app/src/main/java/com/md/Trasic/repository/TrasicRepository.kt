package com.md.Trasic.repository

import androidx.lifecycle.liveData
import com.md.Trasic.helper.LoadingResult
import com.md.Trasic.network.ApiConfig
import com.md.Trasic.network.ApiService
import okhttp3.MultipartBody

class TrasicRepository(private val apiService: ApiService) {
    fun predictTrasic(file: MultipartBody.Part) = liveData {
        emit(LoadingResult.Loading)
        try {
            val prediction = apiService.predictTrasic(file)
            emit(LoadingResult.Success(prediction))
        } catch (e: Exception) {
            emit(LoadingResult.Error(e.message.toString()))
        }
    }

    fun getTrasicList(
        limit: Int? = null,
        searchQuery: String? = null,
    ) = liveData {
        emit(LoadingResult.Loading)
        try {
            var trasicList = apiService.getTrasicList()

            if (searchQuery != null) {
                trasicList = trasicList.filter {
                    it.name?.contains(searchQuery, ignoreCase = true) ?: false
                }
            }
            if (limit != null) {
                trasicList = trasicList.shuffled().take(limit)
            }

            emit(LoadingResult.Success(trasicList))
        } catch (e: Exception) {
            emit(LoadingResult.Error(e.message.toString()))
        }
    }

    fun getTrasicDetail(id: String) = liveData {
        emit(LoadingResult.Loading)
        try {
            val trasicDetail = apiService.getTrasicDetail(id.toInt())
            emit(LoadingResult.Success(trasicDetail))
        } catch (e: Exception) {
            emit(LoadingResult.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TrasicRepository? = null

        @Volatile
        private var AI_INSTANCE: TrasicRepository? = null

        @JvmStatic
        fun getTrasicRepository(): TrasicRepository {
            if (INSTANCE == null) INSTANCE = TrasicRepository(ApiConfig.getApiService())
            return INSTANCE as TrasicRepository
        }

        @JvmStatic
        fun getAITrasicRepository(): TrasicRepository {
            if (AI_INSTANCE == null) AI_INSTANCE = TrasicRepository(ApiConfig.getAIApiService())
            return AI_INSTANCE as TrasicRepository
        }
    }
}