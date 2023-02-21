package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.ArraySet
import android.util.Log
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
                val Recursor = db.query("Knowledge", null, "createdate=?",
                    arrayOf(startTime), null, null, null)
                if(Recursor.moveToFirst()){
                    var outText = ""
                    var timeth = -1
                    do{
                        timeth++
                        val reviewdate = Recursor.getString(Recursor.getColumnIndex("reviewdate"))
                        if(timeth == 0)continue
                        outText = outText + reviewdate + "第" + timeth.toString() + "次学习\n"
                    }while (Recursor.moveToNext())
                    Recursor.close()
                    val kTag = Tag(tag)
                    val tSet = ArraySet<String>()
                    for (str in kTag.tSet!!) {
                        if (str != "") tSet.add(str)
                    }
                    showTag(tSet)
                    outText = outText + startTime+ "创建,复习" +endTime.toString() + "次"
                    showReviewContent(outText)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        Log.d(TAG, "onCreate: 3")
        val InspectViewer = findViewById<TextView>(R.id.InspectscheduleViewer)
        InspectViewer.setOnClickListener {
            val intent = Intent(this, EditScheduleActivity::class.java)
            intent.putExtra("ID", ID)
            startActivity(intent)
            finish()


        }
        Log.d(TAG, "onCreate: 4")
        InspectscheduleButtonDelete.setOnClickListener {
            db.delete("Schedule", "id = ?", arrayOf(extraData.toString()))
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
            finish()
        }
        Log.d(TAG, "onCreate: 5")
    }
    private val TAG1 = "InspectScheduleActively"
    private fun showInspectContent(Content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            InspectViewer.setText(Html.fromHtml(Content, Html.FROM_HTML_MODE_COMPACT))
        } else {
            InspectViewer.setText(Html.fromHtml(Content))
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
        InspectViewer.text = Html.fromHtml(Content,
            URLImageGetter(Content, this, InspectViewer, options),
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
    private val TAG2 = "InspectScheduleActively"
    private fun showReviewContent(Content: String) {
        Log.d(TAG, "showReviewContent: 9")
        val ReviewViewer = findViewById<TextView>(R.id.ReviewViewer)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ReviewViewer.setText(Html.fromHtml(Content, Html.FROM_HTML_MODE_COMPACT))
        } else {
            ReviewViewer.setText(Html.fromHtml(Content))
        }
        Log.d(TAG, "showReviewContent: 10")
    }
}