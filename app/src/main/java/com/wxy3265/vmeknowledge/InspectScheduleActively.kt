package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.ArraySet
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import kotlinx.android.synthetic.main.activity_inspect_knowledge.*
import kotlinx.android.synthetic.main.activity_inspect_schedule.*

class InspectScheduleActively : AppCompatActivity() {
    private val tagList = ArrayList<String>()
    private val TAG = "InspectScheduleActively"
    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspect_schedule)
        Log.d(TAG, "onCreate: ")
        val dbHelper = MyDatabaseHelper(this, "Schedule.db", 1)
        val extraData = intent.getIntExtra("ID",-1)
        val db = dbHelper.writableDatabase
        val cursor = db.query("Schedule", null, "id=?",
            arrayOf(extraData.toString()), null, null, null)
        var ID = -1
        Log.d(TAG, "onCreate: "+extraData)
        Log.d(TAG, "onCreate: 1")
        if (cursor.moveToFirst()) {
            do {
                Log.d("cursor", "initSchedules: suc")
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val startTime = cursor.getString(cursor.getColumnIndex("startTime"))
                val endTime = cursor.getString(cursor.getColumnIndex("endTime"))
                val startMilliTime = cursor.getInt(cursor.getColumnIndex("startMilliTime"))
                val endMilliTime = cursor.getInt(cursor.getColumnIndex("endMilliTime"))
                val s_notification = cursor.getString(cursor.getColumnIndex("notification"))
                val tag = cursor.getString(cursor.getColumnIndex("tag"))
                Log.d(TAG, "onCreate: 2")
                ID = id
                showInspectContent(content)
                TimeViewer.text = "开始时间: " + startTime + '\n' + "结束时间: " + endTime
            } while (cursor.moveToNext())
        }
        cursor.close()
        InspectRecyclerscheduleView.visibility = View.GONE
        val InspectViewer = findViewById<TextView>(R.id.InspectScheduleViewer)
        InspectViewer.setOnClickListener {
            val intent = Intent(this, EditScheduleActivity::class.java)
            intent.putExtra("ID", ID)
            startActivity(intent)
            finish()
        }
        InspectscheduleButtonDelete.setOnClickListener {
            db.delete("Schedule", "id = ?", arrayOf(extraData.toString()))
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    private val TAG1 = "InspectScheduleActively"
    private fun showInspectContent(Content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            InspectScheduleViewer.setText(Html.fromHtml(Content, Html.FROM_HTML_MODE_COMPACT))
        } else {
            InspectScheduleViewer.setText(Html.fromHtml(Content))
        }
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this))
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
        InspectScheduleViewer.text = Html.fromHtml(Content,
            URLImageGetter(Content, this, InspectScheduleViewer, options),
            null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showTag(tagSet: ArraySet<String>) {
        Log.d(TAG, "showTag: 8")
        val layoutManager= LinearLayoutManager(this)
        layoutManager.orientation= LinearLayoutManager.HORIZONTAL
        InspectRecyclerView.layoutManager = layoutManager
        tagList.clear()
        for (tag in tagSet) {
            tagList.add(tag)
        }
        val tagAdapter = TagAdapter(tagList)
        InspectRecyclerView.adapter = tagAdapter
    }
}