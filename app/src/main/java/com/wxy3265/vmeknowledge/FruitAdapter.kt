package com.wxy3265.vmeknowledge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class FruitAdapter (val fruitList: List<Fruit>):RecyclerView.Adapter<FruitAdapter.ViewHolder>(){

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val fruitName:TextView=view.findViewById(R.id.fruitname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.fruit_item,parent,false)
        val viewHolder=ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position=viewHolder.adapterPosition
            val fruit=fruitList[position]
            Toast.makeText(parent.context,"you click ${fruit.name}",Toast.LENGTH_SHORT).show()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fruit=fruitList[position]
        holder.fruitName.text=fruit.name
    }

    override fun getItemCount()=fruitList.size
}