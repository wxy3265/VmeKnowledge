package com.wxy3265.vmeknowledge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class StudyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        supportActionBar?.hide()
    }
}