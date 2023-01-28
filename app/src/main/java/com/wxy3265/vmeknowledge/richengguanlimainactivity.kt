package com.wxy3265.vmeknowledge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.menu.*

class richengguanlimainactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_richengguanlimainactivity)
        menumain.setOnClickListener {
            val intent_main = Intent(this , MainActivity::class.java)
            startActivity(intent_main)
        }
    }
}