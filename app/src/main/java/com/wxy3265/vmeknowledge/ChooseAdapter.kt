package com.wxy3265.vmeknowledge

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.log

class ChooseAdapter(val tagList: List<String>): RecyclerView.Adapter<ChooseAdapter.ViewHolder>() {

    private val TAG = "ChooseAdapter"
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val TagName: CheckBox = view.findViewById(R.id.tagChooseCheckBox)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, holder: ViewHolder)
    }
    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.tag_choose_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = tagList[position]
        holder.TagName.text = tag
        holder.TagName.setOnCheckedChangeListener { buttonView, isChecked -> mOnItemClickListener?.onItemClick(position, holder) }
    }

    override fun getItemCount() = tagList.size
}