package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.Intent
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
    private val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1)
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(MainToolbar)
        dbHelper.writableDatabase

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
        val db = dbHelper.writableDatabase
        val cursor = db.query("Knowledge", null, null, null,
                                    null, null, null)
        if (cursor.moveToFirst()) {
            do {
                Log.d("cursor", "initKnowledges: suc")
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val date = cursor.getString(cursor.getColumnIndex("reviewdate"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val studyTimes = cursor.getInt(cursor.getColumnIndex("studytimes"))
                val milliTime = cursor.getInt(cursor.getColumnIndex("milliTime"))
                knowledgeList.add(Knowledge(content, date, id, studyTimes, milliTime))
            } while (cursor.moveToNext())
            cursor.close()
        }
    }

    private fun startAdd() {
        val intent = Intent(this, AddKnowledgeActivity::class.java)
        startActivity(intent)
    }

}