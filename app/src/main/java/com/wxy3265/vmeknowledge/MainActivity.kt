package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.ArraySet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.knowledge_item.*

class MainActivity : AppCompatActivity(){
    private val knowledgeList = ArrayList<Knowledge>()
    private val scheduleList = ArrayList<Schedule>()
    @RequiresApi(Build.VERSION_CODES.M)
    private val tagSet = ArraySet<String>()
    private val reviewInterval = intArrayOf(0 , 1, 2, 4, 7, 15, 30, 90, 180)
    private val TAG = "MainActivity"
    private var remainToReview = 0
    private val tagList = ArrayList<String>()
    private val KnowledgeManage = 1
    private val ScheduleManage = 2
    private var state = KnowledgeManage
    val chosenTag = Tag()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(MainToolbar)

        menuDate.setOnClickListener {
            state = ScheduleManage
            setState()
        }
        menuKnowledge.setOnClickListener {
            state = KnowledgeManage
            setState()
        }

        MainStudy.setOnClickListener {
            val intent = Intent(this, StudyActivity::class.java)
            startActivity(intent)
        }

        MainStudy.setOnLongClickListener {
            if (chosenTag.orstr == "") {
                Toast.makeText(this, "未选中标签", Toast.LENGTH_SHORT)
            } else {
                val intent = Intent(this, StudyTagActivity::class.java)
                intent.putExtra("ChosenTag", chosenTag.orstr)
                startActivity(intent)
            }
            true
        }
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        setState()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initTag() {
        for (tag in tagSet) {
            if (chosenTag.checkTag(tag)) chosenTag.removeTag(tag)
        }
        val layoutManager2=LinearLayoutManager(this)
        layoutManager2.orientation=LinearLayoutManager.HORIZONTAL
        MainTagRecyclerView.layoutManager=layoutManager2
        tagList.clear()
        for (tag in tagSet) {
            tagList.add(tag)
            if (chosenTag.checkTag(tag)) chosenTag.removeTag(tag)
        }
        val tagAdapter = TagAdapter(tagList)
        tagAdapter.setOnItemClickListener(object :TagAdapter.OnItemClickListener{
            var selected:Boolean = false
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onItemClick(position: Int) {
                val tag = tagList[position]
                if (chosenTag.checkTag(tag)) {
                    chosenTag.removeTag(tag)
                    selected = false
                } else {
                    chosenTag.addTag(tag)
                    selected = true
                }
                initKnowledges()
            }
            override fun getSelect(): Boolean {
                return selected
            }
        })
        MainTagRecyclerView.adapter = tagAdapter
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
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val createdate = cursor.getString(cursor.getColumnIndex("createdate"))
                val reviewdate = cursor.getString(cursor.getColumnIndex("reviewdate"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val studyTimes = cursor.getInt(cursor.getColumnIndex("studytimes"))
                val milliTime = cursor.getInt(cursor.getColumnIndex("milliTime"))
                val tag = cursor.getString(cursor.getColumnIndex("tag"))
                var ktag = Tag()
                var showable = false
                if (tag != null) ktag = Tag(tag)
                if (ktag.orstr != "") {
                    for (str in ktag.tSet!!) {
                        if (str != "") {
                            tagSet.add(str)
                            if (chosenTag.checkTag(str)) {
                                showable = true
                            }
                        }
                    }
                }
                if (studyTimes == -1) continue
                if (studyTimes <= 8) {
                    if (System.currentTimeMillis() / 1000 - milliTime > reviewInterval[studyTimes] * 86400) {
                        remainToReview++
                    }
                }
                if (showable || chosenTag.orstr == "") knowledgeList.add(Knowledge(content, createdate, reviewdate, id, studyTimes, milliTime, tag))

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
        val layoutManager = GridLayoutManager(this, 2)
        MainCardRecyclerview.layoutManager = layoutManager
        val adapter = KnowledgeAdapter(this, knowledgeList)
        MainCardRecyclerview.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("Range")
    private  fun initSchedule() {
        scheduleList.clear()
        val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1 )
        val db = dbHelper.writableDatabase
        val cursor = db.query("Schedule", null, null, null, null, null, null)
        if(cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val startTime = cursor.getString(cursor.getColumnIndex("startTime"))
                val endTime = cursor.getString(cursor.getColumnIndex("endTime"))
                val startMilliTime = cursor.getInt(cursor.getColumnIndex("startMilliTime"))
                val endMilliTime = cursor.getInt(cursor.getColumnIndex("endMilliTime"))
                val s_notification = cursor.getString(cursor.getColumnIndex("notification"))
                val tag = cursor.getString(cursor.getColumnIndex("tag"))
                scheduleList.add(Schedule(content, id, startTime, endTime, startMilliTime, endMilliTime, s_notification, tag))
            } while (cursor.moveToNext())
        }
        cursor.close()
        val layoutManager = GridLayoutManager(this, 2)
        MainCardRecyclerview.layoutManager = layoutManager
        val adapter = ScheduleAdapter(this, scheduleList)
        MainCardRecyclerview.adapter = adapter
    }

    private fun startAdd() {
        val intentAddKnowledgeActivity = Intent(this, AddKnowledgeActivity::class.java)
        val intentAddScheduleActivity = Intent(this , AddScheduleActivity::class.java)
        if (state == KnowledgeManage) startActivity(intentAddKnowledgeActivity)
        else if (state == ScheduleManage) startActivity(intentAddScheduleActivity)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setState() {
        if (state == KnowledgeManage) {
            menuDate.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline, null)
            menuKnowledge.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline_purple, null)
            MainStudy.visibility = View.VISIBLE
            MainTagRecyclerView.visibility = View.VISIBLE
            initKnowledges()
            initTag()
        } else {
            menuDate.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline_purple, null)
            menuKnowledge.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline, null)
            MainStudy.visibility = View.GONE
            MainTagRecyclerView.visibility = View.GONE
            Log.d(TAG, "setState: suc1")
            initSchedule()
            Log.d(TAG, "setState: suc2")
        }
    }

}


