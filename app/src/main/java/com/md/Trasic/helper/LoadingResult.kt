package com.md.Trasic.helper

sealed class LoadingResult<out R> private constructor() {
    data class Success<out T>(val data: T) : LoadingResult<T>()
    data class Error(val error: String) : LoadingResult<Nothing>()
    object Loading : LoadingResult<Nothing>()
}