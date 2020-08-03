/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.advancedcoroutines

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.android.advancedcoroutines.util.CacheOnSuccess
import com.example.android.advancedcoroutines.utils.ComparablePair
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * Repository module for handling data operations.
 *
 * This PlantRepository exposes two UI-observable database queries [plants] and
 * [getPlantsWithGrowZone].
 *
 * To update the plants cache, call [tryUpdateRecentPlantsForGrowZoneCache] or
 * [tryUpdateRecentPlantsCache].
 */
class PlantRepository private constructor(
    private val plantDao: PlantDao,
    private val plantService: NetworkService,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    /**
     * Fetch a list of [Plant]s from the database.
     * Returns a LiveData-wrapped List of Plants.
     */
//    val plants = plantDao.getPlants()
    // DONE replace with liveData
    val plants: LiveData<List<Plant>> = liveData {
        val plantsLiveData = plantDao.getPlants()
        val customSortOrder = plantsListSortOrderCache.getOrAwait()
        emitSource(plantsLiveData.map { plantList ->
            plantList.applySort(customSortOrder)
        })
    }

    // DONE adding Flow
    val plantsFlow: Flow<List<Plant>>
        // NOTE: this is the declarative style to load sort order e apply to list - concurrently
        get() = plantDao.getPlantsFlow()
            // When the result of customSortFlow is available,
            // this will combine it with the latest value from
            // the flow above.  Thus, as long as both `plants`
            // and `sortOrder` are have an initial value (their
            // flow has emitted at least one value), any change
            // to either `plants` or `sortOrder`  will call
            // `plants.applySort(sortOrder)`.
            // DONE Add combine operator
            .combine(
                customSortFlow
                // Logging flow
//                .onEach {
//                    Log.d("Flow", "onEach customSortFlow")
//                    delay(2000)
//                }
//                .onStart { Log.d("Flow", "onStart customSortFlow") }
            ) { plants, sortOrder ->
                plants.applySort(sortOrder)
            }
            .flowOn(defaultDispatcher)
            .conflate()

    // DONE adding Flow
    fun getPlantsWithGrowZoneFlow(growZoneNumber: GrowZone): Flow<List<Plant>> {
        return plantDao.getPlantsWithGrowZoneNumberFlow(growZoneNumber = growZoneNumber.number)
            // DONE Add map transformation
            .map { plantList ->
                // NOTE: this is the imperative style to load sort order e apply to list - sequential
                val sortOrderFromNetwork = plantsListSortOrderCache.getOrAwait()
                val nextValue = plantList.applyMainSafeSort(sortOrderFromNetwork)
                nextValue
            }
    }

    /**
     * Fetch a list of [Plant]s from the database that matches a given [GrowZone].
     * Returns a LiveData-wrapped List of Plants.
     */
//    fun getPlantsWithGrowZone(growZone: GrowZone) =
//        plantDao.getPlantsWithGrowZoneNumber(growZone.number)
    // DONE replace with liveData
    fun getPlantsWithGrowZone(growZone: GrowZone) =
    // DONE Update the block to use a switchMap, which will let you point to a new LiveData every time a new value is received

//        liveData<List<Plant>> {
//        val plantsGrowZoneLiveData = plantDao.getPlantsWithGrowZoneNumber(growZone.number)
//        val customSortOrder = plantsListSortOrderCache.getOrAwait()
//        emitSource(plantsGrowZoneLiveData.map { plantList ->
//            plantList.applySort(customSortOrder)
//        })
//}
        // NOTE: the previous uses emitSource and this only emit
        plantDao.getPlantsWithGrowZoneNumber(growZone.number)
            .switchMap { plantList ->
                liveData {
                    val customSortOrder = plantsListSortOrderCache.getOrAwait()
                    emit(plantList.applyMainSafeSort(customSortOrder))
                }
            }

    // DONE: Adding the MainSafe method
    @AnyThread
    suspend fun List<Plant>.applyMainSafeSort(customSortOrder: List<String>) =
    // Change the execution to any Dispatcher(Main, IO, Default)
    // Note: if remove the 'suspend' from the method, it gives a compile error
        //  because withContext its from CoroutineContext
        withContext(defaultDispatcher) {
            this@applyMainSafeSort.applySort(customSortOrder)
        }


    /**
     * Returns true if we should make a network request.
     */
    private fun shouldUpdatePlantsCache(): Boolean {
        // suspending function, so you can e.g. check the status of the database here
        return true
    }

    /**
     * Fetch the custom sort order from the network and then cache in memory
     */
    private var plantsListSortOrderCache = CacheOnSuccess(onErrorFallback = { listOf<String>() }) {
        plantService.customPlantSortOrder()
    }

    // DONE new private flow
//    private val customSortFlow = flow { emit(plantsListSortOrderCache.getOrAwait()) }
    // or
    private val customSortFlow = plantsListSortOrderCache::getOrAwait.asFlow()
    // DONE example: emit twice with a substantial delay to see how the combine operator works
//    private val customSortFlow = suspend {
//        Log.d("Flow", "Calling first cache")
//        plantsListSortOrderCache.getOrAwait()
//    }.asFlow()
//        .onStart {
//            Log.d("Flow", "Emmitting List")
//            emit(listOf())
//            Log.d("Flow", "Emmit done")
//            delay(1500)
//            Log.d("Flow", "Delay done")
//        }

    /**
     * Rearrange the list, Placing [Plant] that are in the
     * customSortOrder at the front of the list
     */
    private fun List<Plant>.applySort(customSortOrder: List<String>): List<Plant> {
        return sortedBy { plant ->
            val positionForItem = customSortOrder.indexOf(plant.plantId).let { order ->
                if (order > -1)
                    order
                else Int.MAX_VALUE
            }
            ComparablePair(positionForItem, plant.name)
        }
    }

    /**
     * Update the plants cache.
     *
     * This function may decide to avoid making a network requests on every call based on a
     * cache-invalidation policy.
     */
    suspend fun tryUpdateRecentPlantsCache() {
        if (shouldUpdatePlantsCache()) fetchRecentPlants()
    }

    /**
     * Update the plants cache for a specific grow zone.
     *
     * This function may decide to avoid making a network requests on every call based on a
     * cache-invalidation policy.
     */
    suspend fun tryUpdateRecentPlantsForGrowZoneCache(growZoneNumber: GrowZone) {
        if (shouldUpdatePlantsCache()) fetchPlantsForGrowZone(growZoneNumber)
    }

    /**
     * Fetch a new list of plants from the network, and append them to [plantDao]
     */
    private suspend fun fetchRecentPlants() {
        val plants = plantService.allPlants()
        plantDao.insertAll(plants)
    }

    /**
     * Fetch a list of plants for a grow zone from the network, and append them to [plantDao]
     */
    private suspend fun fetchPlantsForGrowZone(growZone: GrowZone) {
        val plants = plantService.plantsByGrowZone(growZone)
        plantDao.insertAll(plants)
    }

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: PlantRepository? = null

        fun getInstance(plantDao: PlantDao, plantService: NetworkService) =
            instance ?: synchronized(this) {
                instance ?: PlantRepository(plantDao, plantService).also { instance = it }
            }
    }
}
