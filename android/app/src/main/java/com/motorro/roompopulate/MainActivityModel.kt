/*
 * The MIT License (MIT)
 *
 * Copyright (c)  2020. Nikolai Kotchetkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.motorro.roompopulate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.motorro.roompopulate.cities.CitiesDb
import com.motorro.roompopulate.cities.data.City
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Performs city search
 */
class MainActivityModel(application: Application): AndroidViewModel(application) {
    private var subscription: Disposable
    private val searchSubject = PublishSubject.create<String>()

    /**
     * Search
     */
    fun search(string: String) {
        searchSubject.onNext(string)
    }

    /**
     * Search results
     */
    val cities = MutableLiveData<List<City>>()

    init {
        // Database engine
        val db = Room
            .databaseBuilder(getApplication(), CitiesDb::class.java, "cities.db")
            // Set asset-file to copy database from
            .createFromAsset("databases/cities.db")
            // How the database gets copied over:
            // 1. Every time the import script is run - the database version increases in BuildConfig
            // 2. The local database (if already there) is verified to have the same version
            // 3. As we have a version greater the migration is performed
            // 4. We don't supply any migration (fallbackToDestructiveMigration)
            //    so the file gets copied over
            .fallbackToDestructiveMigration()
            .build()

        subscription = searchSubject
            .debounce(300L, TimeUnit.MILLISECONDS, Schedulers.computation())
            .filter { it.isNotBlank() }
            .switchMap {
                Observable
                    .fromCallable { db.citiesDao().searchByString("$it%", 30) }
                    .subscribeOn(Schedulers.computation())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { searchResult -> cities.value = searchResult },
                { error -> throw error }
            )
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }
}