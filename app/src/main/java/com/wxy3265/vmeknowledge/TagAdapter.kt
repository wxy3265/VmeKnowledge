package com.wxy3265.vmeknowledge

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TagAdapter (val tagList: List<String>):RecyclerView.Adapter<TagAdapter.ViewHolder>() {

    private val TAG = "TagAdapter"

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tagName: TextView = view.findViewById(R.id.tagname)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun getSelect(): Boolean
    }
    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag=tagList[position]
        holder.tagName.text = tag
        holder.itemView.setOnClickListener {
            mOnItemClickListener?.onItemClick(position)
            if (mOnItemClickListener?.getSelect() == true) holder.tagName.setTextColor(Color.RED)
            else holder.tagName.setTextColor(Color.BLACK)
            Log.d(TAG, "onBindViewHolder: Click" + position.toString() + "selected:" + mOnItemClickListener?.getSelect().toString())
        }
    }

    override fun getItemCount() = tagList.size
}