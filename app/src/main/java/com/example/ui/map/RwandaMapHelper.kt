package com.example.ui.map

import com.example.data.model.GeoPoint
import com.example.data.model.NavigationRoute
import com.example.data.model.RouteStep
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object RwandaMapHelper {

    // Center of Kigali
    val KIGALI_CENTER = GeoPoint(-1.9441, 30.0619, "Kigali Downtown")

    // Rwanda bounding box
    const val MIN_LAT = -2.85
    const val MAX_LAT = -1.05
    const val MIN_LON = 28.85
    const val MAX_LON = 30.90

    // Kigali city bounding box for zoomed in urban view
    const val KIGALI_MIN_LAT = -1.99
    const val KIGALI_MAX_LAT = -1.90
    const val KIGALI_MIN_LON = 30.01
    const val KIGALI_MAX_LON = 30.17

    /**
     * Calculate Haversine distance in kilometers
     */
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c).toFloat()
    }

    /**
     * Generate turn-by-turn navigation route from origin to destination
     */
    fun buildRoute(origin: GeoPoint, destination: GeoPoint, clinicName: String): NavigationRoute {
        val distanceKm = calculateDistanceKm(origin.latitude, origin.longitude, destination.latitude, destination.longitude)
        val roundedKm = (Math.round(distanceKm * 10.0) / 10.0).toFloat().coerceAtLeast(0.4f)
        val estimatedMins = ((roundedKm / 35.0f) * 60).toInt().coerceAtLeast(4)

        val steps = mutableListOf<RouteStep>()
        steps.add(
            RouteStep(
                instruction = "Head out towards the main arterial road",
                distanceMeters = 200,
                streetName = "Local Access Road"
            )
        )

        if (roundedKm > 1.5f) {
            steps.add(
                RouteStep(
                    instruction = "Turn right onto KN 3 Rd / Boulevard de l'OUA",
                    distanceMeters = 800,
                    streetName = "KN 3 Rd"
                )
            )
            steps.add(
                RouteStep(
                    instruction = "At the roundabout, take the 2nd exit onto KG 11 Ave",
                    distanceMeters = 1200,
                    streetName = "KG 11 Ave"
                )
            )
        }

        steps.add(
            RouteStep(
                instruction = "Continue straight towards $clinicName entrance",
                distanceMeters = 400,
                streetName = "Clinic Access Drive"
            )
        )
        steps.add(
            RouteStep(
                instruction = "Arrive at $clinicName on your right. Parking & mobile reception available.",
                distanceMeters = 50,
                streetName = "Arrival Destination"
            )
        )

        return NavigationRoute(
            origin = origin,
            destination = destination,
            totalDistanceKm = roundedKm,
            estimatedMinutes = estimatedMins,
            steps = steps
        )
    }

    /**
     * Major landmark cities in Rwanda for geography rendering
     */
    val RWANDA_LANDMARKS = listOf(
        GeoPoint(-1.9441, 30.0619, "Kigali"),
        GeoPoint(-1.5002, 29.6349, "Musanze"),
        GeoPoint(-2.6006, 29.7424, "Huye"),
        GeoPoint(-1.6883, 29.2558, "Rubavu"),
        GeoPoint(-1.9487, 30.4347, "Rwamagana"),
        GeoPoint(-2.0789, 29.7562, "Muhanga"),
        GeoPoint(-1.2982, 30.3243, "Nyagatare"),
        GeoPoint(-2.4842, 28.9077, "Rusizi")
    )
}
