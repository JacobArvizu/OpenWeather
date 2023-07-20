package com.arvizu.openweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class NetworkViewModel : ViewModel() {

    protected val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    protected val _errorObs = MutableLiveData<Throwable>()
    val errorObs: LiveData<Throwable> get() = _errorObs
    /**
     * Safely launches a coroutine within the [viewModelScope] with error handling and supervision.
     * Using viewModel scope ensures that the coroutine will adhere to the lifecycle of the ViewModel
     * and all exceptions will be propagated to the [handleException] method.
     * @param coroutineName An optional name for the coroutine to be used in logging and debugging.
     * @param coroutine The suspendable coroutine block to be executed.
     */
    protected fun launchSafe(
        coroutineName: String = "",
        coroutine: suspend CoroutineScope.() -> Unit
    ) {
        val context = if (coroutineName.isNotEmpty()) {
            defaultHandler + CoroutineName(coroutineName)
        } else {
            defaultHandler
        }
        // Launch the coroutine within the viewModelScope
        viewModelScope.launch(context) {
            Timber.d("Launching coroutine with Context: $coroutineContext")
            coroutine(this)
        }
    }

    protected open fun handleException(throwable: Throwable) {
        Timber.e(throwable)
        _errorObs.value = throwable
    }

    protected open val defaultHandler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }
}