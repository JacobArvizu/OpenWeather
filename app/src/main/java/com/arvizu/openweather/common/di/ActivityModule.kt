package com.arvizu.openweather.common.di

import android.content.Context
import com.arvizu.openweather.common.util.helpers.FusedLocationHelper
import com.arvizu.openweather.common.util.helpers.FusedLocationHelperImpl
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@InstallIn(ActivityComponent::class)
@Module
object ActivityModule {
    @Provides
    fun provideFusedLocationHelper(@ActivityContext context: Context, fusedLocationProviderClient: FusedLocationProviderClient): FusedLocationHelper {
        return FusedLocationHelperImpl(context, fusedLocationProviderClient)
    }
}