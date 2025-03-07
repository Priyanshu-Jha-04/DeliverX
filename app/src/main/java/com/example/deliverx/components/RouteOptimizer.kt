package com.yourpackage.routeoptimizer

import android.util.Log
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.DistanceMatrixApi
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.*

class RouteOptimizer(
    private val apiKey: String,
    private val placesClient: PlacesClient
) {

    private val geoApiContext = GeoApiContext.Builder()
        .apiKey(apiKey)
        .build()

    suspend fun findOptimalRoute(
        locations: List<LocationData>,
        startLocationIndex: Int = 0
    ): List<LocationData> {
        if (locations.isEmpty()) return emptyList()
        if (locations.size == 1) return locations

        val locationsWithCoords = fetchCoordinatesIfNeeded(locations)

        val distanceMatrix = createDistanceMatrix(locationsWithCoords)

        val optimalRoute = findOptimalPath(distanceMatrix, startLocationIndex, locationsWithCoords.size)

        return optimalRoute.map { locationsWithCoords[it] }
    }

    private suspend fun fetchCoordinatesIfNeeded(locations: List<LocationData>): List<LocationData> = withContext(Dispatchers.IO) {
        val result = mutableListOf<LocationData>()

        for (location in locations) {
            if (location.latitude != 0.0 && location.longitude != 0.0) {
                result.add(location)
                continue
            }

            try {
                var updatedLocation = location

                if (location.placeId.isNotEmpty()) {
                    try {
                        val placeFields = listOf(Place.Field.LAT_LNG)
                        val request = FetchPlaceRequest.newInstance(location.placeId, placeFields)

                        val response = withContext(Dispatchers.Main) {
                            placesClient.fetchPlace(request).await()
                        }

                        val place = response.place
                        place.latLng?.let {
                            updatedLocation = location.copy(
                                latitude = it.latitude,
                                longitude = it.longitude
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("RouteOptimizer", "Error fetching place details: ${e.message}", e)
                    }
                }

                if (updatedLocation.latitude == 0.0 && updatedLocation.longitude == 0.0) {
                    try {
                        val geocodeResults = GeocodingApi.geocode(
                            geoApiContext,
                            updatedLocation.address
                        ).await()

                        if (geocodeResults.isNotEmpty()) {
                            val location = geocodeResults.first().geometry.location
                            updatedLocation = updatedLocation.copy(
                                latitude = location.lat,
                                longitude = location.lng
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("RouteOptimizer", "Error geocoding address: ${e.message}", e)
                    }
                }

                result.add(updatedLocation)
            } catch (e: Exception) {
                Log.e("RouteOptimizer", "Error processing location: ${e.message}", e)
                result.add(location)
            }
        }

        result
    }

    private suspend fun createDistanceMatrix(locations: List<LocationData>): Array<DoubleArray> = withContext(Dispatchers.IO) {
        val size = locations.size
        val matrix = Array(size) { DoubleArray(size) { 0.0 } }
        val batchSize = 10

        try {
            for (originBatch in 0 until size step batchSize) {
                val originEnd = minOf(originBatch + batchSize, size)
                val origins = locations.subList(originBatch, originEnd).map {
                    LatLng(it.latitude, it.longitude)
                }.toTypedArray()

                for (destBatch in 0 until size step batchSize) {
                    val destEnd = minOf(destBatch + batchSize, size)
                    val destinations = locations.subList(destBatch, destEnd).map {
                        LatLng(it.latitude, it.longitude)
                    }.toTypedArray()

                    val result = DistanceMatrixApi.newRequest(geoApiContext)
                        .origins(*origins)
                        .destinations(*destinations)
                        .mode(TravelMode.DRIVING)
                        .await()

                    for (i in originBatch until originEnd) {
                        for (j in destBatch until destEnd) {
                            if (i != j) {
                                matrix[i][j] = result.rows[i - originBatch].elements[j - destBatch].distance.inMeters.toDouble()
                            }
                        }
                    }

                    delay(500)
                }
            }
        } catch (e: Exception) {
            Log.e("RouteOptimizer", "Error with Distance Matrix API: ${e.message}", e)
        }

        matrix
    }

    private fun findOptimalPath(
        distanceMatrix: Array<DoubleArray>,
        startIndex: Int,
        numLocations: Int
    ): List<Int> {
        val path = mutableListOf(startIndex)
        val visited = BooleanArray(numLocations) { false }
        visited[startIndex] = true

        while (path.size < numLocations) {
            val current = path.last()
            val nearest = (0 until numLocations).filter { !visited[it] }
                .minByOrNull { distanceMatrix[current][it] } ?: break

            path.add(nearest)
            visited[nearest] = true
        }

        return path
    }
}

data class LocationData(
    val id: String,
    val name: String,
    val address: String,
    val placeId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
