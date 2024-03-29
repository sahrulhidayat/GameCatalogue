package com.sahrulhidayat.core.data.source

import com.sahrulhidayat.core.data.source.local.LocalDataSource
import com.sahrulhidayat.core.data.source.remote.RemoteDataSource
import com.sahrulhidayat.core.data.source.remote.network.ApiResponse
import com.sahrulhidayat.core.data.source.remote.response.GameDetailsResponse
import com.sahrulhidayat.core.data.source.remote.response.GameResults
import com.sahrulhidayat.core.domain.interfaces.IGameRepository
import com.sahrulhidayat.core.domain.model.GameModel
import com.sahrulhidayat.core.utils.DataMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GameRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : IGameRepository {
    override fun getGameList(sort: String): Flow<Resource<List<GameModel>>> {
        return object : NetworkBoundResource<List<GameModel>, List<GameResults>>() {
            override fun loadFromDB(): Flow<List<GameModel>> {
                return localDataSource.getGameList(sort).map {
                    DataMapper.mapEntitiesToDomain(it)
                }
            }

            override fun shouldFetch(data: List<GameModel>?): Boolean {
                return data.isNullOrEmpty()
            }

            override suspend fun createCall(): Flow<ApiResponse<List<GameResults>>> {
                return remoteDataSource.getGameList()
            }

            override suspend fun saveCallResult(data: List<GameResults>) {
                val gameList = DataMapper.mapGameListResponseToEntities(data)
                CoroutineScope(ioDispatcher).launch {
                    localDataSource.insertGame(gameList)
                }
            }
        }.asFlow()
    }

    override fun getGameDetails(id: Int): Flow<Resource<GameModel>> {
        return object : NetworkBoundResource<GameModel, GameDetailsResponse>() {
            override fun loadFromDB(): Flow<GameModel> {
                return localDataSource.getGameDetails(id).map {
                    DataMapper.mapDetailsEntityToDomain(it)
                }
            }

            override fun shouldFetch(data: GameModel?): Boolean {
                return data?.description == null
            }

            override suspend fun createCall(): Flow<ApiResponse<GameDetailsResponse>> {
                return remoteDataSource.getGameDetails(id)
            }

            override suspend fun saveCallResult(data: GameDetailsResponse) {
                val gameDetails = DataMapper.mapGameDetailsResponseToEntities(data)
                CoroutineScope(ioDispatcher).launch {
                    localDataSource.updateGame(gameDetails)
                }
            }
        }.asFlow()
    }

    override fun getAllFavoriteGames(): Flow<List<GameModel>> {
        return localDataSource.getAllFavoriteGames().map {
            DataMapper.mapEntitiesToDomain(it)
        }
    }

    override fun setFavoriteGame(game: GameModel, state: Boolean) {
        val gameEntity = DataMapper.mapDomainToEntity(game)
        CoroutineScope(ioDispatcher).launch {
            localDataSource.setFavoriteGame(gameEntity, state)
        }
    }
}