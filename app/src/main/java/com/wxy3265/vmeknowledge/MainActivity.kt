package com.wxy3265.vmeknowledge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1)
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
    }
}