package com.blanktheevil.inkmangareader.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

sealed class Either<T> {
    class Success<T>(val data: T) : Either<T>()
    class Error<T>(val error: Throwable) : Either<T>()
    class Null<T>: Either<T>()

    fun successOrNull(): T? = if (this is Success) data else null
    fun errorOrNull(): Throwable? = if (this is Error) error else null

    inline fun onSuccess(callback: (T) -> Unit) = apply {
        if (this is Success) callback(data)
    }

    inline fun onError(callback: (Throwable) -> Unit) = apply {
        if (this is Error) callback(error)
    }

    inline fun onNull(callback: () -> Unit) = apply {
        if (this is Null) callback()
    }
}

fun <T> success(data: T) = Either.Success(data)
fun <T> error(throwable: Throwable) = Either.Error<T>(throwable)
fun <T> T?.wrap(): Either<T> = if (this == null) Either.Null() else Either.Success(this)
fun <T> Either<out T?>.nullDataToNull(): Either<T> = if (this is Either.Success && this.data != null) {
    success(this.data)
} else {
    Either.Null()
}

fun <T> Flow<Either<T>>.onEitherError(
    onError: (Throwable) -> Unit = {}
): Flow<Either<T>> = this.onEach {
    it.onError { error -> onError(error) }
}

fun <T> Flow<Either<T>>.filterEitherSuccess(
    onError: (Throwable) -> Unit = {}
): Flow<T> = this
        .onEach {
            it.onError { error -> onError(error) }
        }
        .filterIsInstance<Either.Success<T>>()
        .map { it.data }