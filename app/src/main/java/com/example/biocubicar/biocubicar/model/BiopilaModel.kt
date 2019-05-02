package com.example.biocubicar.biocubicar.model

data class BiopilaModel(
        var id: Int,
        var title: String,
        var description: String,
        var latitude: Double,
        var longitude: Double,
        var volume: Double,
        var htp: Double,
        var temperature: Double,
        var moisture: Double
)