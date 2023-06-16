package com.md.Trasic.network

import com.md.Trasic.data.PredictResponse
import com.md.Trasic.data.TrasicResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("predict")
    suspend fun predictTrasic(
        @Part file: MultipartBody.Part,
    ): PredictResponse

    @GET("trasic")
    suspend fun getTrasicList(): List<TrasicResponse>

    @GET("trasic/{id}")
    suspend fun getTrasicDetail(
        @Path("id") id: Int,
    ): TrasicResponse
}