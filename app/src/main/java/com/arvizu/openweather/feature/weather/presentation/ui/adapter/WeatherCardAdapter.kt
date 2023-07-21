package com.arvizu.openweather.feature.weather.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.arvizu.openweather.databinding.WeatherCardItemBinding
import com.arvizu.openweather.feature.weather.presentation.ui.adapter.model.WeatherCard

class WeatherCardAdapter :
    ListAdapter<WeatherCard, WeatherCardAdapter.WeatherViewHolder>(WeatherDiffCallback) {

    // Higher order function to handle item click
    var onItemClick : ((item : WeatherCard) -> Unit)? = null

    inner class WeatherViewHolder(val binding: WeatherCardItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = WeatherCardItemBinding.inflate(layoutInflater, parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weatherCard = getItem(position)
        with(holder.binding) {
            weatherLogoImageView.load(weatherCard.iconUrl)
            dateTextView.text = weatherCard.date
            temperatureTextView.text = weatherCard.temperature
            windSpeedTextView.text = weatherCard.windSpeed
            humidityTextView.text = weatherCard.humidity
            cloudinessTextView.text = weatherCard.cloudiness
            descriptionTextView.text = weatherCard.description
            timeOfDayTextView.text = weatherCard.timeOfDay
            root.setOnClickListener {
                onItemClick?.invoke(weatherCard)
            }
        }
    }

    object WeatherDiffCallback : DiffUtil.ItemCallback<WeatherCard>() {
        override fun areItemsTheSame(oldItem: WeatherCard, newItem: WeatherCard): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: WeatherCard, newItem: WeatherCard): Boolean {
            return oldItem == newItem
        }
    }
}