package com.wxy3265.vmeknowledge

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KnowledgeAdapter(val context: Context, val knowledgeList: List<Knowledge>) :
            RecyclerView.Adapter<KnowledgeAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val KnowledgeCard: TextView = view.findViewById(R.id.KnowledgeCard)
        val DateCard: TextView = view.findViewById(R.id.DateCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.knowledge_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val knowledge = knowledgeList[position]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.KnowledgeCard.setText(Html.fromHtml(knowledge.Content, Html.FROM_HTML_MODE_COMPACT))
        }else {
            holder.KnowledgeCard.setText(Html.fromHtml(knowledge.Content))
        }
        holder.DateCard.text = knowledge.Date
    }

    override fun getItemCount() = knowledgeList.size

}