package org.sjhstudio.weathertestapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.sjhstudio.weathertestapp.R
import org.sjhstudio.weathertestapp.databinding.ItemWeatherBinding
import org.sjhstudio.weathertestapp.model.LocalWeather
import org.sjhstudio.weathertestapp.util.WeatherHelper

class WeatherAdapter: RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    private var items = arrayListOf<LocalWeather>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = ItemWeatherBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun setBind(data: LocalWeather) {
            binding.weatherImg.setImageResource(
                WeatherHelper.getWeatherResource(
                    data.weather!!,
                    isImage = true,
                    isSmallImage = true
                )
            )
            binding.timeTv.text = data.time
            binding.tempTv.text = data.temp
            binding.chanceOfShowerTv.text = "${data.chanceOfShower}%"

            if(data.chanceOfShower == "0") {    // 강수량 0
                binding.chanceOfShowerTv.visibility = View.INVISIBLE
            } else {
                binding.chanceOfShowerTv.visibility = View.VISIBLE
            }
        }

    }

    fun setItems(items: ArrayList<LocalWeather>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun clear() {
        this.items = arrayListOf()
        notifyItemRangeRemoved(0, items.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setBind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

}