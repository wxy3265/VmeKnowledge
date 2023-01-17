package com.wxy3265.vmeknowledge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TagAdapter (val tagList: List<String>):RecyclerView.Adapter<TagAdapter.ViewHolder>(){

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val fruitName:TextView=view.findViewById(R.id.tagname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val tag = tagList[position]
            Toast.makeText(parent.context, "you click ${tag}", Toast.LENGTH_SHORT).show()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag=tagList[position]
        holder.fruitName.text=tag
    }

    override fun getItemCount()=tagList.size
}