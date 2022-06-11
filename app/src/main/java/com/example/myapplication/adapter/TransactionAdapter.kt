package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.entity.Transaction
import com.example.myapplication.listener.TransactionListener

class TransactionAdapter(val list: List<Transaction>): RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    private var listener: TransactionListener? = null

    fun setOnTransactionListener(listener: TransactionListener){
        this.listener = listener;
    }

    class TransactionViewHolder(view: View, private val listener: TransactionListener?):  RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.revenuelist_textview_name)
        val value: TextView = view.findViewById(R.id.revenuelist_textview_value)
        val date: TextView = view.findViewById(R.id.revenuelist_textview_date)

        init {
            view.setOnClickListener{
                listener?.setOnItemClickListener(it,adapterPosition)
            }
            view.setOnLongClickListener {
                listener?.setOnItemLongClickListener(it,adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionAdapter.TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.revenue_item, parent,false)
        return TransactionViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: TransactionAdapter.TransactionViewHolder, position: Int) {
        val transaction= list[position]
        holder.name.text = transaction.name
        holder.value.text = transaction.value
        holder.date.text = transaction.date
//        if (transaction.revenue) {
//            holder.value.setTextColor(0x85BB65);
//        } else {
//            holder.value.setTextColor(0xD23733);
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
