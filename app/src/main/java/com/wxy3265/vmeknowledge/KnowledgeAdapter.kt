package com.wxy3265.vmeknowledge

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ContextUtils.getActivity
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import io.github.angebagui.mediumtextview.MediumTextView


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
            Log.d(TAG, "onCreateViewHolder: position")
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
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context))
        val options = DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.ic_launcher)
            .showImageForEmptyUri(R.mipmap.ic_launcher)
            .showImageOnFail(R.mipmap.ic_launcher)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(FadeInBitmapDisplayer(300))
            .build()
        holder.KnowledgeCard.text = Html.fromHtml(knowledge.Content,
            URLImageGetter(knowledge.Content, context, holder.KnowledgeCard, options),
            null)
        holder.DateCard.text = knowledge.ReviewDate
    }

    override fun getItemCount() = knowledgeList.size

}