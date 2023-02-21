package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import kotlinx.android.synthetic.main.activity_inspect_knowledge.*
import kotlinx.android.synthetic.main.activity_study.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StudyActivity : AppCompatActivity() {

    private val TAG = "StudyActivity"

    private val reviewInterval = intArrayOf(0, 1, 2, 4, 7, 15, 30, 90, 180)
    private val reviewList = ArrayList<Knowledge>()
    private var currentKnowledge = 0

    @SuppressLint("Range", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        supportActionBar?.hide()


        val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1)
        val db = dbHelper.writableDatabase
        val cursor = db.query("Knowledge", null, null, null,
            null, null, null)
        if (cursor.moveToFirst()) {
            do {
                Log.d("cursor", "initKnowledges: suc")
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val createdate = cursor.getString(cursor.getColumnIndex("createdate"))
                val reviewdate = cursor.getString(cursor.getColumnIndex("reviewdate"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val studyTimes = cursor.getInt(cursor.getColumnIndex("studytimes"))
                val milliTime = cursor.getInt(cursor.getColumnIndex("milliTime"))
                val tag = cursor.getString(cursor.getColumnIndex("tag"))
                if(studyTimes == -1) continue
                if (studyTimes <= 8) {
                    Log.d(TAG, "onCreate: " + System.currentTimeMillis() / 1000 + "-" + milliTime
                            + "=" + (System.currentTimeMillis() / 1000 - milliTime)
                            + ">" + reviewInterval.get(studyTimes) * 86400)
                    if (System.currentTimeMillis() / 1000 - milliTime > reviewInterval[studyTimes] * 86400) {
                        reviewList.add(Knowledge(content, createdate, reviewdate, id, studyTimes, milliTime, tag))
                    }
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
        if (reviewList.size > 0) showContent(reviewList[currentKnowledge].Content)
        else {
            Toast.makeText(this, "无可复习知识", Toast.LENGTH_SHORT).show()
            finish()
        }

        StudyButtonRemember.setOnClickListener {

            val formatter = SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss")
            val curDate = Date(System.currentTimeMillis())
            val date: String = formatter.format(curDate)
            val value0 = ContentValues().apply{
                put("studytimes",-1)
            }
            val value = ContentValues().apply {
                put("content", reviewList[currentKnowledge].Content)
                put("studytimes", reviewList[currentKnowledge].StudyTimes + 1)
                put("createdate",reviewList[currentKnowledge].CreateDate)
                put("tag", reviewList[currentKnowledge].tag)
                put("reviewdate", date)
                put("milliTime", System.currentTimeMillis() / 1000)
            }
            db.update("Knowledge", value0, "id = ?",
                arrayOf(reviewList[currentKnowledge].Id.toString()))
            db.insert("Knowledge",null, value)
            currentKnowledge++

            if (currentKnowledge >= reviewList.size) {
                Toast.makeText(this, "复习完成", Toast.LENGTH_SHORT).show()
                finish()
            }
            else showContent(reviewList[currentKnowledge].Content)
        }
        StudyButtonForget.setOnClickListener {
            reviewList.add(reviewList[currentKnowledge])
            currentKnowledge++
            if (currentKnowledge >= reviewList.size) {
                Toast.makeText(this, "复习完成", Toast.LENGTH_SHORT).show()
                finish()
            }
            else showContent(reviewList[currentKnowledge].Content)
        }

        app.setText(""+currentKnowledge+"/"+ reviewList.size+"")
    }

    private fun showContent(Content: String) {
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
        StudyViewer.text = Html.fromHtml(Content,
            URLImageGetter(Content, this, StudyViewer, options),
            null)
    }

}