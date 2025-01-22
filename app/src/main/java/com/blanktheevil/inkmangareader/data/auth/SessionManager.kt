package com.blanktheevil.inkmangareader.data.auth

import android.content.Context
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
    private val context: Context,
    private val authRepository: AuthRepository,
    moshi: Moshi,
) {
    private val moshiAdapter = moshi.adapter(Session::class.java)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _session = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = _session.asStateFlow()

    init {
        scope.launch {
            // get a session from shared prefs and attempt to refresh it. does not check if session is expired, just gets a new one
            localSession?.let {
                _session.value = authRepository.refresh(it.refresh).successOrNull()
            }
        }
    }

    fun login(username: String, password: String) = scope.launch {
        authRepository.login(username, password).successOrNull()?.let {
            _session.value = it
        }
    }

    private var localSession: Session?
        get() = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            .getString("session", null)
            ?.let { sessionJson -> moshiAdapter.fromJson(sessionJson) }
        set(value) = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("session", session.let { moshiAdapter.toJson(value) })
            .apply()
}
