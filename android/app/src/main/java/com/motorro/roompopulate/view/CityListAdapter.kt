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
        val binding = ItemCityBinding.inflate(inflater)
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