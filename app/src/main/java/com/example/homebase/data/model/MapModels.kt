package com.example.homebase.data.model

import com.google.android.gms.maps.model.LatLng

data class CampusBuilding(
    val name: String,
    val distance: String,
    val position: LatLng
)

object CampusData {
    // Coordinates for Av. de la Universidad, 30, Leganés
    val leganesPos = LatLng(40.3323, -3.7656)

    // Coordinates for C. Madrid, 126, Getafe
    val getafePos = LatLng(40.3045, -3.7258)

    val leganesBuildings = listOf(
        CampusBuilding("Building 7 - Juan Benet", "50 m", LatLng(40.3325, -3.7658)),
        CampusBuilding("Bank Secaucus", "1,2 km", LatLng(40.3330, -3.7640)),
        CampusBuilding("Bank 1657 Riverside Drive", "5,3 km", LatLng(40.3310, -3.7680)),
        CampusBuilding("Bank Rutherford", "70 m", LatLng(40.3320, -3.7650))
    )

    val getafeBuildings = listOf(
        CampusBuilding("Library - Getafe", "100 m", LatLng(40.3048, -3.7250)),
        CampusBuilding("Building 15 - Lopez Aranguren", "200 m", LatLng(40.3040, -3.7260)),
        CampusBuilding("Student Center", "350 m", LatLng(40.3035, -3.7270))
    )
}