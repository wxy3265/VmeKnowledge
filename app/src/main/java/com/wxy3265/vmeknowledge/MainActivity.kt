package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.ArraySet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val knowledgeList = ArrayList<Knowledge>()
    @RequiresApi(Build.VERSION_CODES.M)
    private val tagSet = ArraySet<String>()
    private val reviewInterval = intArrayOf(0, 1, 2, 4, 7, 15, 30, 90, 180)
    private val TAG = "MainActivity"
    private var remainToReview = 0
    private val tagList = ArrayList<String>()
     val chosenTag = Tag()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(MainToolbar)

        Log.d(TAG, "onCreate: " + System.currentTimeMillis() / 3600000)

        MainStudy.setOnClickListener {
            val intent = Intent(this, StudyActivity::class.java)
            startActivity(intent)
        }
        MainStudy.setOnLongClickListener {

            val intent = Intent(this, StudyActivity::class.java)
            startActivity(intent)
            true
        }

        initKnowledges()
        val layoutManager = GridLayoutManager(this, 2)
        MainCardRecyclerview.layoutManager = layoutManager
        val adapter = KnowledgeAdapter(this, knowledgeList)
        MainCardRecyclerview.adapter = adapter
        val layoutManager2=LinearLayoutManager(this)
        layoutManager2.orientation=LinearLayoutManager.HORIZONTAL
        MainTagRecyclerView.layoutManager=layoutManager2
        tagList.clear()
        for (tag in tagSet) {
            tagList.add(tag)
        }
        val adapter2=TagAdapter(tagList)
        MainTagRecyclerView.adapter=adapter2
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        initKnowledges()
        val layoutManager = GridLayoutManager(this, 2)
        MainCardRecyclerview.layoutManager = layoutManager
        val adapter = KnowledgeAdapter(this, knowledgeList)
        MainCardRecyclerview.adapter = adapter
        val layoutManager2=LinearLayoutManager(this)
        layoutManager2.orientation=LinearLayoutManager.HORIZONTAL
        MainTagRecyclerView.layoutManager=layoutManager2
        tagList.clear()
        for (tag in tagSet) {
            tagList.add(tag)
        }
        val adapter2=TagAdapter(tagList)
        MainTagRecyclerView.adapter=adapter2
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("Range")
    private fun initKnowledges() {
        tagSet.clear()
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
                val tag = cursor.getString(cursor.getColumnIndex("tag"))
                val ktag = Tag(tag)
                if (ktag.orstr != "") {
                    Log.d(TAG, "initKnowledges: orstr:" + ktag.orstr)
                    for (str in ktag.tSet!!) {
                        tagSet.add(str)
                    }
                }
                if(studyTimes == -1)continue
                if (studyTimes <= 8) {
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

    fun addChosenTag(tag: String) {
        chosenTag.addTag(tag)
    }

}