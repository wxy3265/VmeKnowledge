package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val knowledgeList = ArrayList<Knowledge>()
    private val reviewInterval = intArrayOf(0, 1, 2, 4, 7, 15, 30, 90, 180)
    private val TAG = "MainActivity"
    private var remainToReview = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(MainToolbar)

        Log.d(TAG, "onCreate: " + System.currentTimeMillis() / 3600000)

        MainStudy.setOnClickListener {
            val intent = Intent(this, StudyActivity::class.java)
            startActivity(intent)
        }

        initKnowledges()
        val layoutManager = GridLayoutManager(this, 2)
        MainCardRecyclerview.layoutManager = layoutManager
        val adapter = KnowledgeAdapter(this, knowledgeList)
        MainCardRecyclerview.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.MainAdd -> startAdd()
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        initKnowledges()
        val layoutManager = GridLayoutManager(this, 2)
        MainCardRecyclerview.layoutManager = layoutManager
        val adapter = KnowledgeAdapter(this, knowledgeList)
        MainCardRecyclerview.adapter = adapter
    }

    @SuppressLint("Range")
    private fun initKnowledges() {
        knowledgeList.clear()
        val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1)
        val db = dbHelper.writableDatabase
        val cursor = db.query("Knowledge", null, null, null,
                                    null, null, null)
        remainToReview = 0
        if (cursor.moveToFirst()) {
            do {
                Log.d("cursor", "initKnowledges: suc")
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val createdate = cursor.getString(cursor.getColumnIndex("createdate"))
                val reviewdate = cursor.getString(cursor.getColumnIndex("reviewdate"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val studyTimes = cursor.getInt(cursor.getColumnIndex("studytimes"))
                val milliTime = cursor.getInt(cursor.getColumnIndex("milliTime"))
                if(id==0)continue
                if (studyTimes <= 8) {
                    Log.d(TAG, "onCreate: " + System.currentTimeMillis() / 1000 + "-" + milliTime
                            + "=" + (System.currentTimeMillis() / 1000 - milliTime)
                            + ">" + reviewInterval.get(studyTimes) * 86400)
                    if (System.currentTimeMillis() / 1000 - milliTime > reviewInterval[studyTimes] * 86400) {
                        remainToReview++
                    }
                }
                knowledgeList.add(Knowledge(content, createdate, reviewdate, id, studyTimes, milliTime))
            } while (cursor.moveToNext())
            cursor.close()
        }
        if (remainToReview != 0) {
            MainStudy.setText("开始学习 (" + remainToReview + "个待复习知识)")
            MainStudy.setBackgroundColor(Color.rgb(98, 0, 238))
            MainStudy.isClickable = true
        } else {
            MainStudy.setText("无需要复习内容")
            MainStudy.setBackgroundColor(Color.GRAY)
            MainStudy.isClickable = false
        }
    }

    private fun startAdd() {
        val intent = Intent(this, AddKnowledgeActivity::class.java)
        startActivity(intent)
    }

}