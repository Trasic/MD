package com.md.Trasic.data

import com.google.gson.annotations.SerializedName

data class PredictResponse(
	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("score")
	val score: Any? = null,

	@field:SerializedName("message")
	val message: String? = null
)
