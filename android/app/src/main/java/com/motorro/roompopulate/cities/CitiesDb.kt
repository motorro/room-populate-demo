package com.motorro.roompopulate.cities

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.motorro.roompopulate.BuildConfig
import com.motorro.roompopulate.cities.dao.CitiesDao
import com.motorro.roompopulate.cities.data.City
import com.motorro.roompopulate.cities.data.DataConverters

/**
 * Cities database definition
 * Take a look at how the version is set - thus we have a new version every time we run import
 */
@Database(entities = [City::class], version = BuildConfig.CITIES_DB_VERSION)
@TypeConverters(DataConverters::class)
abstract class CitiesDb: RoomDatabase() {
    /**
     * Cities DAO
     */
    abstract fun citiesDao(): CitiesDao
}