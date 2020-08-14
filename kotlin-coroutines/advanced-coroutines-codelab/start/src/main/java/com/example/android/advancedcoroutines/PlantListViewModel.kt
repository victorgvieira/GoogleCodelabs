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

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * The [ViewModel] for fetching a list of [Plant]s.
 */
class PlantListViewModel internal constructor(
    private val plantRepository: PlantRepository
) : ViewModel() {

    /**
     * Request a snackbar to display a string.
     *
     * This variable is private because we don't want to expose [MutableLiveData].
     *
     * MutableLiveData allows anyone to set a value, and [PlantListViewModel] is the only
     * class that should be setting values.
     */
    private val _snackbar = MutableLiveData<String?>()

    /**
     * Request a snackbar to display a string.
     */
    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _spinner = MutableLiveData<Boolean>(false)

    /**
     * Show a loading spinner if true
     */
    val spinner: LiveData<Boolean>
        get() = _spinner

    /**
     * The current growZone selection.
     */
    private val growZone = MutableLiveData<GrowZone>(NoGrowZone)

    /**
     * A list of plants that updates based on the current filter.
     */
    val plants: LiveData<List<Plant>> = growZone.switchMap { growZone ->
        if (growZone == NoGrowZone) {
            plantRepository.plants
        } else {
            plantRepository.getPlantsWithGrowZone(growZone)
        }
    }

    // DONE add new variable
    private val growZoneChannel = ConflatedBroadcastChannel<GrowZone>()

    val plantsUsingFlow: LiveData<List<Plant>> = growZoneChannel.asFlow()
        .flatMapLatest { growZone ->
            if (growZone == NoGrowZone) {
                plantRepository.plantsFlow
            } else {
                plantRepository.getPlantsWithGrowZoneFlow(growZone)
            }
        }.asLiveData()

    // DONE adding Flow
//    val plantsUsingFlow: LiveData<List<Plant>> = plantRepository.plantsFlow.asLiveData()

    init {
        // When creating a new ViewModel, clear the grow zone and perform any related udpates
        clearGrowZoneNumber()

        // DONE put the logic for making the network calls
        loadDataFor(growZoneChannel) { growZone ->
            if (growZone == NoGrowZone) {
                plantRepository.tryUpdateRecentPlantsCache()
            } else {
                plantRepository.tryUpdateRecentPlantsForGrowZoneCache(growZone)
            }
        }

        // DONE adding Flow: fetch the full plant list
//        launchDataLoad { plantRepository.tryUpdateRecentPlantsCache() }
    }

    /**
     * Filter the list to this grow zone.
     *
     * In the starter code version, this will also start a network request. After refactoring,
     * updating the grow zone will automatically kickoff a network request.
     */
    fun setGrowZoneNumber(num: Int) {
        growZone.value = GrowZone(num)
        // DONE Notify to the channel that the filter changed
        growZoneChannel.offer(GrowZone(num))

        // initial code version, will move during flow rewrite
//        launchDataLoad { plantRepository.tryUpdateRecentPlantsCache() }
    }

    /**
     * Clear the current filter of this plants list.
     *
     * In the starter code version, this will also start a network request. After refactoring,
     * updating the grow zone will automatically kickoff a network request.
     */
    fun clearGrowZoneNumber() {
        growZone.value = NoGrowZone
        // DONE Notify to the channel that the filter changed
        growZoneChannel.offer(NoGrowZone)

        // initial code version, will move during flow rewrite
//        launchDataLoad { plantRepository.tryUpdateRecentPlantsCache() }
    }

    /**
     * Return true iff the current list is filtered.
     */
    fun isFiltered() = growZone.value != NoGrowZone

    /**
     * Called immediately after the UI shows the snackbar.
     */
    fun onSnackbarShown() {
        _snackbar.value = null
    }

    /**
     * Helper function to call a data load function with a loading spinner; errors will trigger a
     * snackbar.
     *
     * By marking [block] as [suspend] this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param block lambda to actually load data. It is called in the viewModelScope. Before calling
     *              the lambda, the loading spinner will display. After completion or error, the
     *              loading spinner will stop.
     */
//    private fun launchDataLoad(block: suspend () -> Unit): Job {
//        return viewModelScope.launch {
//            try {
//                _spinner.value = true
//                block()
//            } catch (error: Throwable) {
//                _snackbar.value = error.message
//            } finally {
//                _spinner.value = false
//            }
//        }
//    }

    // DONE extra challenge
    fun <T> loadDataFor(source: ConflatedBroadcastChannel<T>, block: suspend (T) -> Unit) {
        source.asFlow()
            .mapLatest { value ->
                _spinner.value = true
                block(value)
                _spinner.value = false
            }.onCompletion {
                Log.d("Flow", "onCompletion never called")
                _spinner.value = false
            }.catch { throwable ->
                _snackbar.value = throwable.message
            }.launchIn(viewModelScope)
    }
}
