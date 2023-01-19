package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.activity_study_tag.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StudyTagActivity : AppCompatActivity() {
    private val TAG = "StTagActivity"
    private val reviewList = ArrayList<Knowledge>()
    private var currentKnowledge = 0

    @SuppressLint("Range", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_tag)
        supportActionBar?.hide()

        val extraData = intent.getStringExtra("ChosenTag")
        if (extraData == null) {
            Toast.makeText(this, "打开错误", Toast.LENGTH_SHORT).show()
        }

        val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1)
        val db = dbHelper.writableDatabase
        val cursor = db.query("Knowledge", null, null, null,
            null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val createdate = cursor.getString(cursor.getColumnIndex("createdate"))
                val reviewdate = cursor.getString(cursor.getColumnIndex("reviewdate"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val studyTimes = cursor.getInt(cursor.getColumnIndex("studytimes"))
                val milliTime = cursor.getInt(cursor.getColumnIndex("milliTime"))
                val tag = cursor.getString(cursor.getColumnIndex("tag"))
                Log.d(TAG, "onCreate: knowledge: " + content)
                val ktag = Tag(tag)
                if (extraData?.let { ktag.checkTag(it) } == true && studyTimes != -1) {
                    reviewList.add(Knowledge(content, createdate, reviewdate, id, studyTimes, milliTime, tag))
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
        Log.d(TAG, "onCreate: over")
        if (reviewList.size > 0) showContent(reviewList[currentKnowledge].Content)
        else {
            Toast.makeText(this, "无可复习知识", Toast.LENGTH_SHORT).show()
            finish()
        }

        StudyTagButtonRemember.setOnClickListener {
            currentKnowledge++
            if (currentKnowledge >= reviewList.size) {
                Toast.makeText(this, "复习完成", Toast.LENGTH_SHORT).show()
                finish()
            }
            else showContent(reviewList[currentKnowledge].Content)
        }
        StudyTagButtonForget.setOnClickListener {
            reviewList.add(reviewList[currentKnowledge])
            currentKnowledge++
            if (currentKnowledge >= reviewList.size) {
                Toast.makeText(this, "复习完成", Toast.LENGTH_SHORT).show()
                finish()
            }
            else showContent(reviewList[currentKnowledge].Content)
        }

    }

    private fun showContent(Content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StudyTagViewer.setText(Html.fromHtml(Content, Html.FROM_HTML_MODE_COMPACT))
        } else {
            StudyTagViewer.setText(Html.fromHtml(Content))
        }
    }
}