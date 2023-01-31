package com.wxy3265.vmeknowledge

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.wxy3265.vmeknowledge.MainActivity


class KnowledgeAdapter(val context: Context, val knowledgeList: List<Knowledge>) :
            RecyclerView.Adapter<KnowledgeAdapter.ViewHolder>(){
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val KnowledgeCard: TextView = view.findViewById(R.id.KnowledgeCard)
        val DateCard: TextView = view.findViewById(R.id.DateCard)
        val Card: LinearLayout = view.findViewById(R.id.Card)
    }

    private val TAG = "Adapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.knowledge_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            Log.d(TAG, "onCreateViewHolder: Clicked!")
            val position = viewHolder.adapterPosition
            val knowledge = knowledgeList[position]
            val intent = Intent(context, InspectKnowledgeActivity::class.java)
            Log.d(TAG, "onCreateViewHolder: Definated!")
            intent.putExtra("ID", knowledge.Id)
            Log.d(TAG, "onCreateViewHolder: puted")
            parent.context.startActivity(intent)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val knowledge = knowledgeList[position]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.KnowledgeCard.setText(Html.fromHtml(knowledge.Content, Html.FROM_HTML_MODE_COMPACT))
        } else {
            holder.KnowledgeCard.setText(Html.fromHtml(knowledge.Content))
        }
        holder.DateCard.text = knowledge.ReviewDate
    }

    override fun getItemCount() = knowledgeList.size

}