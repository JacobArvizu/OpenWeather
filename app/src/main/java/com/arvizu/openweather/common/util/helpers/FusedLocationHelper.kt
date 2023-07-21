package com.arvizu.openweather.common.util.helpers

import com.github.michaelbull.result.Result

interface FusedLocationHelper {

    suspend fun getCurrentLocation(): Result<Pair<Double, Double>, Exception>
}
