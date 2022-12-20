package com.wxy3265.vmeknowledge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(true) {
            val intent = Intent(this, StudyActivity::class.java)
            startActivity(intent)
        } //该段代码用于启动StudyActivity作调试使用，将if后括号内改为false以禁用 by wxy3265
    }
}