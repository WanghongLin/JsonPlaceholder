/*
 * Copyright 2020 Wanghong Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jsonplaceholder.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.jsonplaceholder.AppExecutor
import com.example.jsonplaceholder.dao.CrudDao
import com.example.jsonplaceholder.model.AbsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.net.ssl.HttpsURLConnection

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}

data class Resource<T>(val status: Status, val data: T?, val message: String) {
    companion object {
        fun <T> loading(data: T?) = Resource(Status.LOADING, data, "")
        fun <T> success(data: T) = Resource(Status.SUCCESS, data, "")
        fun <T> error(data: T?, message: String?) =
            Resource(Status.ERROR, data, message ?: "Unknown error")
    }
}

abstract class CrudResource<ID, ResultType : AbsModel<ID>>(
    private val crudDao: CrudDao<ID, ResultType>,
    private val crudService: CrudService<ID, ResultType>,
    private val appExecutor: AppExecutor
) : Crud<ID, ResultType> {

    private val createResult = MediatorLiveData<Resource<ResultType>>()
    private val readResult = MediatorLiveData<Resource<ResultType>>()
    private val readListResult = MediatorLiveData<Resource<List<ResultType>>>()
    private val updateResult = MediatorLiveData<Resource<ResultType>>()
    private val deleteResult = MediatorLiveData<Resource<ResultType>>()

    private fun setCreateResult(value: Resource<ResultType>) = createResult.postValue(value)
    private fun setReadResult(value: Resource<ResultType>) = readResult.postValue(value)
    private fun setReadListResult(value: Resource<List<ResultType>>) =
        readListResult.postValue(value)

    private fun setUpdateResult(value: Resource<ResultType>) = updateResult.postValue(value)
    private fun setDeleteResult(value: Resource<ResultType>) = deleteResult.postValue(value)

    protected open fun shouldCreate(resultType: ResultType): Boolean = true
    protected open fun shouldRead(resultType: ResultType?): Boolean = true
    protected open fun shouldReadList(resultType: List<ResultType>): Boolean = true
    protected open fun shouldUpdate(resultType: ResultType?): Boolean = true
    protected open fun shouldDelete(resultType: ResultType): Boolean = true

    override fun create(t: ResultType): LiveData<Resource<ResultType>> {
        setCreateResult(Resource.loading(null))
        doCreate(t)
        return createResult
    }

    override fun read(id: ID): LiveData<Resource<ResultType>> {
        setReadResult(Resource.loading(null))
        val dbSource = crudDao.query(id)
        readResult.addSource(dbSource) { newData ->
            readResult.removeSource(dbSource)
            if (shouldRead(newData)) {
                doRead(id, dbSource)
            } else {
                readResult.addSource(dbSource) { data ->
                    setCreateResult(Resource.success(data))
                }
            }
        }
        return readResult
    }

    override fun readList(): LiveData<Resource<List<ResultType>>> {
        setReadListResult(Resource.loading(null))
        val dbSource = crudDao.query()
        readListResult.addSource(dbSource) { newData ->
            readListResult.removeSource(dbSource)
            if (shouldReadList(newData)) {
                doReadList(dbSource)
            } else {
                readListResult.addSource(dbSource) { data ->
                    setReadListResult(Resource.success(data))
                }
            }
        }
        return readListResult
    }

    override fun update(id: ID, t: ResultType): LiveData<Resource<ResultType>> {
        setUpdateResult(Resource.loading(t))
        doUpdate(id, t)
        return updateResult
    }

    override fun delete(t: ResultType): LiveData<Resource<ResultType>> {
        setDeleteResult(Resource.loading(null))
        doDelete(t)
        return deleteResult
    }

    private fun onCreate(data: ResultType): Call<ResultType?> = crudService.create(data)
    private fun onRead(id: ID): Call<ResultType> = crudService.read(id)
    private fun onReadList(): Call<List<ResultType>> = crudService.readList()
    private fun onUpdate(id: ID, resultType: ResultType): Call<ResultType> = crudService.update(id, resultType)
    private fun onDelete(id: ID): Call<Void> = crudService.delete(id)

    open fun onNetworkFailure(throwable: Throwable) {}

    private fun doCreate(resultType: ResultType) {
        onCreate(resultType).enqueue(object : Callback<ResultType?> {
            override fun onResponse(call: Call<ResultType?>, response: Response<ResultType?>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (response.code() == HttpsURLConnection.HTTP_OK && body != null) {
                        appExecutor.diskIO.execute {
                            crudDao.insert(resultType)
                            appExecutor.mainLooper.execute {
                                setCreateResult(Resource.success(body))
                            }
                        }
                        return
                    }

                    // get id from header
                    val locationName = response.headers().names()
                        .firstOrNull { it.toLowerCase(Locale.getDefault()) == "location" }
                        ?: throw RuntimeException("restful POST response not contain Location header")
                    val locationValue = response.headers()[locationName]!!
                    val id = locationValue.substring(locationValue.lastIndexOf('/') + 1)
                    resultType.id = when (resultType.id) {
                        is Int -> id.toInt() as ID
                        is Long -> id.toLong() as ID
                        is String -> id as ID
                        else -> throw RuntimeException("ID must be Int/Long/String")
                    }
                    appExecutor.diskIO.execute {
                        crudDao.insert(resultType)
                        appExecutor.mainLooper.execute {
                            setCreateResult(Resource.success(resultType))
                        }
                    }
                } else {
                    setCreateResult(
                        Resource.error(
                            resultType,
                            response.message() ?: response.code().toString()
                        )
                    )
                }
            }

            override fun onFailure(call: Call<ResultType?>, t: Throwable) {
                onNetworkFailure(t)
                setCreateResult(Resource.error(null, t.message))
            }
        })
    }

    private fun doRead(id: ID, db: LiveData<out ResultType>) {
        readResult.addSource(db) { newData ->
            setReadResult(Resource.loading(newData))
        }

        onRead(id).enqueue(object : Callback<ResultType> {
            override fun onResponse(call: Call<ResultType>, response: Response<ResultType>) {
                readResult.removeSource(db)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    appExecutor.diskIO.execute {
                        crudDao.insert(body)
                        appExecutor.mainLooper.execute {
                            readResult.addSource(crudDao.query(id)) { newData ->
                                setReadResult(Resource.success(newData))
                            }
                        }
                    }
                } else {
                    readResult.addSource(crudDao.query(id)) { newData ->
                        setReadResult(
                            if (response.isSuccessful)
                                Resource.success(newData) else
                                Resource.error(
                                    newData,
                                    response.errorBody()?.string() ?: response.code().toString()
                                )
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ResultType>, t: Throwable) {
                onNetworkFailure(t)
                readResult.removeSource(db)
                readResult.addSource(crudDao.query(id)) { newData ->
                    setReadResult(Resource.error(newData, t.message))
                }
            }
        })
    }

    private fun doReadList(db: LiveData<out List<ResultType>>) {
        readListResult.addSource(db) { newData ->
            setReadListResult(Resource.loading(newData))
        }

        onReadList().enqueue(object : Callback<List<ResultType>> {
            override fun onResponse(
                call: Call<List<ResultType>>,
                response: Response<List<ResultType>>
            ) {
                readListResult.removeSource(db)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    appExecutor.diskIO.execute {
                        crudDao.insert(body)
                        appExecutor.mainLooper.execute {
                            readListResult.addSource(crudDao.query()) { newData ->
                                setReadListResult(Resource.success(newData))
                            }
                        }
                    }
                } else {
                    readListResult.addSource(crudDao.query()) { newData ->
                        setReadListResult(
                            if (response.isSuccessful)
                                Resource.success(newData) else
                                Resource.error(
                                    newData,
                                    response.errorBody()?.string() ?: response.code().toString()
                                )
                        )
                    }
                }
            }

            override fun onFailure(call: Call<List<ResultType>>, t: Throwable) {
                onNetworkFailure(t)
                readListResult.removeSource(db)
                readListResult.addSource(crudDao.query()) { newData ->
                    setReadListResult(Resource.error(newData, t.message))
                }
            }
        })
    }

    private fun doUpdate(id: ID, resultType: ResultType) {
        onUpdate(id, resultType).enqueue(object : Callback<ResultType> {
            override fun onResponse(call: Call<ResultType>, response: Response<ResultType>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    appExecutor.diskIO.execute {
                        crudDao.update(body)
                        appExecutor.mainLooper.execute {
                            setUpdateResult(Resource.success(body))
                        }
                    }
                } else {
                    setUpdateResult(
                        Resource.error(
                            body,
                            response.message() ?: response.code().toString()
                        )
                    )
                }
            }

            override fun onFailure(call: Call<ResultType>, t: Throwable) {
                onNetworkFailure(t)
                setUpdateResult(Resource.error(null, t.message))
            }
        })
    }

    private fun doDelete(resultType: ResultType) {
        onDelete(resultType.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    appExecutor.diskIO.execute {
                        crudDao.delete(resultType)
                        appExecutor.mainLooper.execute {
                            setDeleteResult(Resource.success(resultType))
                        }
                    }
                } else {
                    setDeleteResult(
                        Resource.error(
                            null,
                            response.errorBody()?.string() ?: response.code().toString()
                        )
                    )
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onNetworkFailure(t)
                setDeleteResult(Resource.error(null, t.message))
            }
        })
    }
}