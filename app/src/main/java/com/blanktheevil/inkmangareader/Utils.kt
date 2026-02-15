package com.blanktheevil.inkmangareader

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

val Context.hasActiveInternetConnection: Boolean
    get() {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

fun CoroutineScope.launchAsUnit(
    block: suspend CoroutineScope.() -> Unit
) { launch(block = block) }

fun Boolean?.orFalse(): Boolean {
    return this == true
}

fun Boolean?.orTrue(): Boolean {
    return this != false
}

inline fun <reified T> T.log(
    message: String,
    logFunction: (String, String) -> Int = Log::d,
    tag: String? = null,
) {
    logFunction(tag ?: T::class.java.simpleName, message)
}

fun <T1, T2> combine(flow1: Flow<T1>, flow2: Flow<T2>) = combine(
    flow1, flow2, ::Pair
)

fun <T1, T2, T3> combine(flow1: Flow<T1>, flow2: Flow<T2>, flow3: Flow<T3>) = combine(
    flow1, flow2, flow3, ::Triple
)
