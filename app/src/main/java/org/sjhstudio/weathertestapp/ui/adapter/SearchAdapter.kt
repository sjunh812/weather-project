package org.sjhstudio.weathertestapp.ui.adapter

import android.location.Address
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.sjhstudio.weathertestapp.R
import org.sjhstudio.weathertestapp.databinding.ItemSearchBinding

class SearchAdapter: RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var items = arrayListOf<Address>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = ItemSearchBinding.bind(itemView)

        fun setBind(data: Address) {
            val addr = "${data.adminArea ?: ""} ${data.subAdminArea ?: ""}" +
                    "${data.locality ?: ""} ${data.featureName ?: ""}"
            binding.searchItemTv.text = data.getAddressLine(0)
        }

    }

    fun setItems(items: ArrayList<Address>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        holder.setBind(items[position])
    }

    override fun getItemCount(): Int = items.size

}