package com.blanktheevil.inkmangareader.settings

import com.blanktheevil.inkmangareader.data.room.dao.SettingsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsManagerImpl(
    private val settingsDao: SettingsDao,
) : SettingsManager {
    private val _settingsState = MutableStateFlow(SettingsState())
    override val settingsState = _settingsState.asStateFlow()

    private val settingsScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        settingsScope.launch {
            settingsDao.get()?.data?.let {
                updateState { it }
            }
        }
    }

    override fun updateState(update: SettingsState.() -> SettingsState) {
        _settingsState.value = update(_settingsState.value)
    }
}