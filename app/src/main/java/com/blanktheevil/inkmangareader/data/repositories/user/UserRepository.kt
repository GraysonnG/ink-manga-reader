package com.blanktheevil.inkmangareader.data.repositories.user

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.models.User

interface UserRepository {
    suspend fun get(userId: String): Either<User>
    suspend fun getCurrent(): Either<User>
}