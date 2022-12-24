package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val knowledgeList = ArrayList<Knowledge>()
    private val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper.writableDatabase

        //该段代码用于启动其他活动作调试使用，后续编写请删除 by wxy3265
        MainAdd.setOnClickListener {
            val intent = Intent(this, AddKnowledgeActivity::class.java)
            startActivity(intent)
        }
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
                knowledgeList.add(Knowledge(content, date, id))
            } while (cursor.moveToNext())
            cursor.close()
        }
    }

}