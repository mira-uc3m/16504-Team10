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
        CampusBuilding("Edificio 1 (Sabatini)", "50 m", LatLng(40.3331, -3.7653)),
        CampusBuilding("Edificio 2 (Betancourt)", "120 m", LatLng(40.3327, -3.7648)),
        CampusBuilding("Edificio 4 (Agustín de Betancourt)", "80 m", LatLng(40.3321, -3.7654)),
        CampusBuilding("Edificio 7 (Juan Benet)", "70 m", LatLng(40.3324, -3.7667)),
        CampusBuilding("Edificio 10 (Juan de la Cierva)", "150 m", LatLng(40.3322, -3.7674)),
        CampusBuilding("Biblioteca Rey Pastor", "100 m", LatLng(40.3328, -3.7656))
    )

    val getafeBuildings = listOf(
        CampusBuilding("Edificio 1 (Concepción Arenal)", "100 m", LatLng(40.3042, -3.7248)),
        CampusBuilding("Edificio 10 (Carmen Martín Gaite)", "250 m", LatLng(40.3033, -3.7266)),
        CampusBuilding("Edificio 15 (López Aranguren)", "200 m", LatLng(40.3038, -3.7259)),
        CampusBuilding("Edificio 18 (Ortega y Gasset)", "180 m", LatLng(40.3051, -3.7255)),
        CampusBuilding("Biblioteca de Ciencias Sociales", "120 m", LatLng(40.3048, -3.7250))
    )
}