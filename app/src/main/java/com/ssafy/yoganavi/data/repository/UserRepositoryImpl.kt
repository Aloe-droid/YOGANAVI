package com.ssafy.yoganavi.data.repository

import com.google.gson.Gson
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.data.source.user.UserDataSource
import com.ssafy.yoganavi.data.source.user.UserRequest
import com.ssafy.yoganavi.di.IoDispatcher
import com.ssafy.yoganavi.ui.utils.NO_RESPONSE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    override suspend fun logIn(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.logIn(userRequest) }
        return response.toListResponse()
    }

    override suspend fun signUp(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.signUp(userRequest) }
        return response.toListResponse()
    }

    override suspend fun registerEmail(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.registerEmail(userRequest) }
        return response.toListResponse()
    }

    override suspend fun checkAuthEmail(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.checkAuthEmail(userRequest) }
        return response.toListResponse()
    }

    override suspend fun findPasswordEmail(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.findPasswordEmail(userRequest) }
        return response.toListResponse()
    }

    override suspend fun checkAuthPassword(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.checkAuthPassword(userRequest) }
        return response.toListResponse()
    }

    override suspend fun registerPassword(userRequest: UserRequest): ListResponse<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.registerPassword(userRequest) }
        return response.toListResponse()
    }

    private fun Response<YogaResponse<Unit>>.toListResponse(): ListResponse<Unit> {
        body()?.let {
            if (isSuccessful) return ListResponse.Success(it.data, it.message)
            else return ListResponse.Error(it.data, it.message)
        }

        val gson = errorBody()?.let { Gson().fromJson(it.charStream(), YogaResponse::class.java) }
        val errorMessage = gson?.message
        return if (errorMessage.isNullOrBlank()) ListResponse.Error(message = NO_RESPONSE)
        else ListResponse.Error(message = errorMessage)
    }
}