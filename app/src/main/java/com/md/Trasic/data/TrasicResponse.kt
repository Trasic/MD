package com.md.Trasic.data

import com.google.gson.annotations.SerializedName

data class TrasicResponse(
    @field:SerializedName("trasic_id")
    val id: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("picture")
    val imageUrl: String? = null,

    @field:SerializedName("sound")
    val soundUrl: String? = null,
)