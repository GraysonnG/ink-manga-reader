package com.blanktheevil.inkmangareader.data.auth

import android.content.Context
import android.util.Log
import com.blanktheevil.inkmangareader.data.Session
import com.blanktheevil.inkmangareader.data.repositories.auth.AuthRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val SHARED_PREFERENCE_NAME = "bte_ink_unencrypted"

class SessionManager(
    context: Context,
    private val authRepository: AuthRepository,
    moshi: Moshi,
) {
    private val tag = this::class.java.simpleName
    private val moshiAdapter = moshi.adapter(Session::class.java)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _session = MutableStateFlow<Session?>(null)
    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    val session: StateFlow<Session?> = _session.asStateFlow()

    init {
        // get a session from shared prefs and attempt to refresh it. does not check if session is expired, just gets a new one
        Log.d(tag, "Local Session: ${localSession != null}")
        localSession?.let {
            _session.value = it
            scope.launch {
                val refresh = authRepository.refresh(it.refresh).successOrNull()
                Log.d(tag,"Startup Refresh Session: ${refresh?.expires}")
                _session.value = refresh
                localSession = refresh
            }
        }
    }

    suspend fun login(username: String, password: String) =
        authRepository.login(username, password).onSuccess {
            _session.value = it
            localSession = it
        }

    suspend fun refresh() {
        Log.d(tag, "Start Refresh")
        localSession?.let {
            val refresh = authRepository.refresh(it.refresh).successOrNull()
            Log.d(tag, "Refresh Session: ${refresh?.expires}")
            _session.value = refresh
            localSession = refresh
        }
    }

    private var localSession: Session?
        get() = sharedPrefs
            .getString("session", null)
            ?.let { sessionJson -> moshiAdapter.fromJson(sessionJson) }
        set(value) = sharedPrefs
            .edit()
            .putString("session", session.let { moshiAdapter.toJson(value) })
            .apply()
}
