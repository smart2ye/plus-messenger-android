package com.anter.plusmessenger.data.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiError(val error: String? = null)
