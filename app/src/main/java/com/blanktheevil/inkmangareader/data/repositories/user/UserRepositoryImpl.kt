package com.blanktheevil.inkmangareader.data.repositories.user

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.api.MangaDexApi
import com.blanktheevil.inkmangareader.data.auth.SessionManager
import com.blanktheevil.inkmangareader.data.models.User
import com.blanktheevil.inkmangareader.data.repositories.mappers.toUser
import com.blanktheevil.inkmangareader.data.repositories.makeAuthenticatedCall
import com.blanktheevil.inkmangareader.data.repositories.makeCall

class UserRepositoryImpl(
    private val mangaDexApi: MangaDexApi,
    private val sessionManager: SessionManager,
) : UserRepository {
    override suspend fun get(userId: String): Either<User> =
        makeCall {
            mangaDexApi.getUserInfo(id = userId).data.toUser()
        }

    override suspend fun getCurrent(): Either<User> =
        makeAuthenticatedCall(sessionManager) { auth ->
            mangaDexApi.getCurrentUserInfo(authorization = auth).data.toUser()
        }
}