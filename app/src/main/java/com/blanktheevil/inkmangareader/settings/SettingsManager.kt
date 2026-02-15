package com.blanktheevil.inkmangareader.settings

import kotlinx.coroutines.flow.StateFlow

interface SettingsManager {
    val settingsState: StateFlow<SettingsState>
    fun updateState(update: SettingsState.() -> SettingsState)
}