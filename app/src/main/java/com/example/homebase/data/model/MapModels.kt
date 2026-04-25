package com.example.homebase.data.model

import com.google.android.gms.maps.model.LatLng

data class CampusBuilding(
    val name: String,
    val distance: String,
    val position: LatLng
)

object CampusData {
    // Coordinates for UC3M Leganés
    val leganesPos = LatLng(40.3323, -3.7656)

    // Coordinates for UC3M Getafe
    val getafePos = LatLng(40.3045, -3.7258)

    val leganesBuildings = listOf(
        CampusBuilding("Building 2 - Leonardo da Vinci", "--", LatLng(40.3320, -3.7645)),
        CampusBuilding("Building 4 - Torres Quevedo", "--", LatLng(40.3330, -3.7665)),
        CampusBuilding("Building 7 - Juan Benet", "--", LatLng(40.3325, -3.7658)),
        CampusBuilding("Building 1 - Auditorium (Padre Soler)", "--", LatLng(40.3315, -3.7640))
    )

    val getafeBuildings = listOf(
        CampusBuilding("Library - María Moliner", "--", LatLng(40.3048, -3.7250)),
        CampusBuilding("Building 15 - López Aranguren", "--", LatLng(40.3040, -3.7260)),
        CampusBuilding("Building 14 - Concepción Arenal", "--", LatLng(40.3055, -3.7265)),
        CampusBuilding("Student Center - Sports Complex", "--", LatLng(40.3030, -3.7240))
    )
}