package com.dangiashish.bluetoothscanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dangiashish.bluetoothscanner.databinding.ItemSelectBluetoothBinding
import java.util.ArrayList


class BtAdapter(private val datas: ArrayList<Device>) :
    RecyclerView.Adapter<BtAdapter.RvHolder?>() {
    private var itemClickListener: ItemListener? = null

    fun setItemClick(itemClickListener: ItemListener?) {
        this.itemClickListener = itemClickListener
    }

    class RvHolder(binding: ItemSelectBluetoothBinding) : RecyclerView.ViewHolder(binding.getRoot()) {
        var bind: ItemSelectBluetoothBinding = binding
    }

    class Device(
        var isMatching: Boolean, var name: String, var mac: String, var rssi: Int, var icon : Int
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvHolder {
        val inflate: ItemSelectBluetoothBinding =
            ItemSelectBluetoothBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = RvHolder(inflate)
        inflate.getRoot().setOnClickListener { v ->
            if (itemClickListener != null) {
                itemClickListener!!.onClick(holder.getAdapterPosition())
            }
        }
        return holder
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: RvHolder, position: Int) {
        val data = datas[position]
        holder.bind.nameTv.text = data.name
        holder.bind.nameTv.isSelected = true
        holder.bind.tvMacAddress.text = data.mac
        holder.bind.connectedTv.visibility = if (data.isMatching) View.VISIBLE else View.GONE
        holder.bind.ivIcon.setImageResource(data.icon)
    }

    interface ItemListener {
        fun onClick(position: Int)
    }

    fun updateData(newList: ArrayList<Device>) {
        datas.clear()
        datas.addAll(newList)
        notifyDataSetChanged()
    }

    fun getData(): List<Device> = datas
}


