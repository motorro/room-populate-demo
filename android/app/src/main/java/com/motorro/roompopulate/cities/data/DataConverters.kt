package com.motorro.roompopulate.cities.data

import androidx.room.TypeConverter
import com.neovisionaries.i18n.CountryCode
import java.util.*

class DataConverters {
    @TypeConverter
    fun countryCodeFromString(value: String?): CountryCode? = value?.let { CountryCode.getByAlpha2Code(it.toUpperCase(
        Locale.US)) }

    @TypeConverter
    fun countryCodeToString(value: CountryCode?): String? = value?.alpha2
}