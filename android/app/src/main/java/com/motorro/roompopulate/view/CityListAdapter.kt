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

package com.motorro.roompopulate.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.motorro.roompopulate.cities.data.City
import com.motorro.roompopulate.cities.data.Coord
import com.motorro.roompopulate.databinding.ItemCityBinding
import kotlin.properties.Delegates

class CityListAdapter(private val selectionListener: (city: Coord) -> Unit): RecyclerView.Adapter<CityViewHolder>() {

    var cities: List<City> by Delegates.observable(emptyList()) { _, old, new ->
        DiffUtil.calculateDiff(CityDiff(old, new), false).dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCityBinding.inflate(inflater, parent, false)
        val viewHolder = CityViewHolder(binding)
        binding.root.setOnClickListener {
            selectionListener(viewHolder.coord)
        }
        return viewHolder
    }

    override fun getItemCount(): Int = cities.size

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities[position])
    }
}

private class CityDiff(private val old: List<City>, private val new: List<City>):DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        old[oldItemPosition].id == new[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = old[oldItemPosition]
        val newItem = new[newItemPosition]
        return oldItem.name == newItem.name && oldItem.country == newItem.country
    }

}

class CityViewHolder(private val binding: ItemCityBinding): RecyclerView.ViewHolder(binding.root) {
    /**
     * Coordinates for click listener
     */
    lateinit var coord: Coord

    fun bind(city: City) {
        binding.cityName.text = city.name
        binding.cityCountry.text = city.country.getName()
        coord = city.coord
    }
}