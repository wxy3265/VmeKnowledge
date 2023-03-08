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
import de.tobiasschuerg.weekview.data.Event
import de.tobiasschuerg.weekview.data.EventConfig
import de.tobiasschuerg.weekview.util.TimeSpan
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.knowledge_item.*
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit

class MainActivity : AppCompatActivity(){
    private val knowledgeList = ArrayList<Knowledge>()
    private val scheduleList = ArrayList<Schedule>()
    @RequiresApi(Build.VERSION_CODES.N)
    private val tagSet = ArraySet<String>()
    private val reviewInterval = intArrayOf(0 , 1, 2, 4, 7, 15, 30, 90, 180)
    private val TAG = "MainActivity"
    private var remainToReview = 0
    private val tagList = ArrayList<String>()
    private val KnowledgeManage = 1
    private val ScheduleManage = 2
    private val ScheduleView = 3
    private var state = KnowledgeManage
    val chosenTag = Tag()
    @RequiresApi(Build.VERSION_CODES.N)
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
        menuCalendar.setOnClickListener {
            state = ScheduleView
            setState()
            //val intent = Intent(this, MainWeekViewActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            //startActivity(intent)
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

    @RequiresApi(Build.VERSION_CODES.N)
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
        val dbHelper = MyDatabaseHelper(this, "Schedule.db", 1 )
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
                Log.d(TAG, "initSchedule: stop1")
                scheduleList.add(Schedule(content, id, startTime, endTime, startMilliTime, endMilliTime, s_notification, tag))
                Log.d(TAG, "initSchedule: stop2")
            } while (cursor.moveToNext())
        }
        cursor.close()
        val layoutManager = GridLayoutManager(this, 1)
        MainCardRecyclerview.layoutManager = layoutManager
        val adapter = ScheduleAdapter(this, scheduleList)
        MainCardRecyclerview.adapter = adapter
    }

    private  fun initMainWeekView() {
        val config = EventConfig(showSubtitle = false, showTimeEnd = false)
        MainWeekView.eventConfig = config
        MainWeekView.setShowNowIndicator(true)

        // set up the MainWeekView with the data
        MainWeekView.addEvents(EventCreator.weekData)

        val nowEvent = Event.Single(
            id = 1337,
            date = LocalDate.now(),
            title = "Current hour",
            shortTitle = "Now",
            timeSpan = TimeSpan.of(LocalTime.now().truncatedTo(ChronoUnit.HOURS), Duration.ofHours(1)),
            backgroundColor = Color.RED,
            textColor = Color.WHITE
        )
        MainWeekView.addEvent(nowEvent)

        // optional: add an onClickListener for each event
        MainWeekView.setEventClickListener {
            //Toast.makeText(applicationContext, "Removing " + it.event.title, Toast.LENGTH_SHORT).show()
            //MainWeekView.removeView(it)
        }

        // optional: register a context menu to each event
        registerForContextMenu(MainWeekView)

        MainWeekView.setOnTouchListener { v, event ->
            when (event.pointerCount) {
                1 -> {
                    Log.d("Scroll", "1-pointer touch")
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
                2 -> {
                    Log.d("Zoom", "2-pointer touch")
                    v.parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            false
        }
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
            menuCalendar.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline, null)
            menuMindmap.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline, null)
            menuKnowledge.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline_purple, null)
            MainStudy.visibility = View.VISIBLE
            MainTagRecyclerView.visibility = View.VISIBLE
            MainCardRecyclerview.visibility = View.VISIBLE
            MainWeekScrollView.visibility = View.GONE
            initKnowledges()
            initTag()
        } else if (state == ScheduleManage) {
            menuDate.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline_purple, null)
            menuCalendar.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline, null)
            menuMindmap.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline, null)
            menuKnowledge.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline, null)
            MainStudy.visibility = View.GONE
            MainTagRecyclerView.visibility = View.GONE
            MainCardRecyclerview.visibility = View.VISIBLE
            MainWeekScrollView.visibility = View.GONE
            initSchedule()
        } else if (state == ScheduleView) {
            MainCardRecyclerview.visibility = View.GONE
            menuDate.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline, null)
            menuCalendar.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline_purple, null)
            menuMindmap.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline, null)
            menuKnowledge.background = ResourcesCompat.getDrawable(resources, R.drawable.borderline, null)
            MainStudy.visibility = View.GONE
            MainTagRecyclerView.visibility = View.GONE
            MainWeekScrollView.visibility = View.VISIBLE
            initMainWeekView()
        }
    }
}
