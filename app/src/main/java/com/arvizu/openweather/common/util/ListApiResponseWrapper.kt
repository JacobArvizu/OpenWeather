package com.arvizu.openweather.common.util

import com.squareup.moshi.Json

data class ListApiResponseWrapper<T>(
    @Json(name = "list")
    val list: List<T>
)