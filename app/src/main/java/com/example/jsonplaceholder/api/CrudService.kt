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

import retrofit2.Call

class CrudService<ID, T>(
    private val serviceImpl: Any,
    private val idParameterType: Class<ID> /* to avoid auto-boxing error long/java.lang.Long */
) {

    fun create(t: T): Call<T?> {
        return serviceImpl::class.java.getDeclaredMethod("create", t!!::class.java)
            .invoke(serviceImpl, t) as Call<T?>
    }

    fun read(id: ID): Call<T> {
        return serviceImpl::class.java.getDeclaredMethod("read", idParameterType)
            .invoke(serviceImpl, id) as Call<T>
    }

    fun readList(): Call<List<T>> {
        return serviceImpl::class.java.getDeclaredMethod("readList")
            .invoke(serviceImpl) as Call<List<T>>
    }

    fun update(id: ID, t: T): Call<T> {
        return serviceImpl::class.java.getDeclaredMethod(
            "update",
            idParameterType,
            t!!::class.java
        ).invoke(serviceImpl, id, t) as Call<T>
    }

    fun delete(id: ID): Call<Void> {
        return serviceImpl::class.java.getDeclaredMethod(
            "delete",
            idParameterType
        ).invoke(serviceImpl, id) as Call<Void>
    }
}