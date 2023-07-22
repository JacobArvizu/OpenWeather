package com.arvizu.openweather.common.util.helpers

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * A helper class to encapsulate the interactions with the Fused LocationEntity Provider for obtaining the current locationEntity.
 *
 * Generally, we aim to separate business logic from views (Activities, Fragments). However, when dealing with functionalities like locationEntity services,
 * where the activity context is needed to check and request permissions.
 *
 * This class is a solution to this dilemma. It uses dependency injection to access activity context safely and uses this context to work with the
 * FusedLocationProviderClient. We can maintain the separation of concerns while still ensuring
 * that the activity context will be used safely and discarded when the activity is destroyed.
 */
@ActivityRetainedScoped
class FusedLocationHelperImpl (
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : FusedLocationHelper {
    override suspend fun getCurrentLocation(): Result<Pair<Double, Double>, Exception> {
        // Safely access the activity context to check for permissions
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context, ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return if (hasLocationPermission) {
            Timber.d("LocationEntity permission granted")
            val location = fusedLocationProviderClient.lastLocation.await()
            Ok(Pair(location.latitude, location.longitude))
        } else {
            Timber.d("LocationEntity permission not granted")
            Err(Exception("LocationEntity permission not granted"))
        }
    }
}
