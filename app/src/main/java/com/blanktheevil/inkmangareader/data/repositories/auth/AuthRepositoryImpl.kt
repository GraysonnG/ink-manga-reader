package com.blanktheevil.inkmangareader.data.repositories.auth

import com.blanktheevil.inkmangareader.data.Either
import com.blanktheevil.inkmangareader.data.Session
import com.blanktheevil.inkmangareader.data.api.MangaDexApi
import com.blanktheevil.inkmangareader.data.dto.responses.AuthData
import com.blanktheevil.inkmangareader.data.dto.responses.Refresh
import com.blanktheevil.inkmangareader.data.repositories.makeCall

class AuthRepositoryImpl(
    private val mangaDexApi: MangaDexApi,
): AuthRepository {
    override suspend fun login(username: String, password: String): Either<Session?> = makeCall {
        mangaDexApi.authLogin(AuthData(username = username, password = password)).let {
            Session(
                token = it.token.session,
                refresh = it.token.refresh,
                expires = System.currentTimeMillis() + Session.EXPIRE_TIME
            )
        }
    }

    override suspend fun refresh(refreshToken: String): Either<Session?> = makeCall {
        mangaDexApi.authRefresh(Refresh(refreshToken)).let {
            Session(
                token = it.token.session,
                refresh = it.token.refresh,
                expires = System.currentTimeMillis() + Session.EXPIRE_TIME
            )
        }
    }
}