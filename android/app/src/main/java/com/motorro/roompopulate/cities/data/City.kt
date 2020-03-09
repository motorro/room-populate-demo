package com.motorro.roompopulate.cities.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.neovisionaries.i18n.CountryCode

/**
 * City entity
 */
@Entity(
    tableName = "cities",
    indices = [
        Index(name = "city_name", value = ["name"])
    ]
)
data class City(
    @PrimaryKey
    val id: Int,
    val name: String,
    val state: String?,
    val country: CountryCode,
    @Embedded
    val coord: Coord
)

/**
 * Coordinates
 */
data class Coord(val lat: Double, val lon: Double)