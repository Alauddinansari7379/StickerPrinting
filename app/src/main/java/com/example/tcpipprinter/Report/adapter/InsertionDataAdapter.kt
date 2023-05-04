package com.mazenrashed.example.Report.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcpipprinter.R
import com.mazenrashed.example.Report.model.InsertionData

class InsertionDataAdapter(val context: Context, val dataList: ArrayList<InsertionData>) :
    RecyclerView.Adapter<InsertionDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_insertion_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.packagingPartno.text = data.packagingPartno
        holder.invoicePartno.text = data.invoicePartno
        holder.packagingQty.text = data.packagingQty.toString()
        holder.invoiceQty.text = data.invoiceQty.toString()
        holder.confirmation.text = data.confirmation
        holder.date.text = data.date
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val packagingPartno: TextView = itemView.findViewById(R.id.packagingPartnoTextView)
        val invoicePartno: TextView = itemView.findViewById(R.id.invoicePartnoTextView)
        val packagingQty: TextView = itemView.findViewById(R.id.packagingQtyTextView)
        val invoiceQty: TextView = itemView.findViewById(R.id.invoiceQtyTextView)
        val confirmation: TextView = itemView.findViewById(R.id.confirmationTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
    }
}
