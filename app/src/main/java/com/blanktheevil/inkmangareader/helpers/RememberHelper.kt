package com.blanktheevil.inkmangareader.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

fun mutableStateOfFalse(): MutableState<Boolean> =
    mutableStateOf(false)

fun mutableStateOfTrue(): MutableState<Boolean> =
    mutableStateOf(true)

@Composable
fun rememberFalseState(): MutableState<Boolean> =
    remember { mutableStateOfFalse() }

@Composable
fun rememberTrueState(): MutableState<Boolean> =
    remember { mutableStateOfTrue() }