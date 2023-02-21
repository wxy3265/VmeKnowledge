package com.wxy3265.vmeknowledge

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer

class ScheduleAdapter(val context: Context, val richengList: List<Schedule>):
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>(){
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val richengContent: TextView = view.findViewById(R.id.richengguanliContent)
        val richengDate: TextView = view.findViewById(R.id.richengguanliDate)
        val richengCard: LinearLayout = view.findViewById(R.id.richengguanli_mainCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.schedule_card,parent,false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val richeng = richengList[position]
            val intent = Intent(context , InspectScheduleActively::class.java)
            intent.putExtra("ID", richeng.id)
            parent.context.startActivity(intent)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val richeng = richengList[position]
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
        holder.richengContent.text = Html.fromHtml(richeng.content,
            URLImageGetter(richeng.content, context, holder.richengContent, options),
            null)
    }

    override fun getItemCount() = richengList.size
}

