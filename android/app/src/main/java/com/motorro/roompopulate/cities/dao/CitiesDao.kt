package com.motorro.roompopulate.cities.dao

import androidx.room.Dao
import androidx.room.Query
import com.motorro.roompopulate.cities.data.City

/**
 * Cities DAO
 */
@Dao
interface CitiesDao {
    /**
     * Searches city by [substring]
     */
    @Query("SELECT * FROM cities WHERE name LIKE :substring LIMIT :maxCount")
    fun searchByString(substring: String, maxCount: Int): List<City>
}