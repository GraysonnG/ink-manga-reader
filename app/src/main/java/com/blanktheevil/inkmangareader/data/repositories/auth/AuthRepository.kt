package com.blanktheevil.inkmangareader.data.repositories.auth

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.Session

interface AuthRepository {
    suspend fun login(username: String, password: String): Either<Session>
    suspend fun refresh(refreshToken: String): Either<Session>
}