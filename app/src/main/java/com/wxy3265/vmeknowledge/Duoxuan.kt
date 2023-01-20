package com.wxy3265.vmeknowledge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

abstract class Duoxuan : AppCompatActivity(),View.OnLongClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duoxuan)
        MainCardRecyclerview.setOnLongClickListener(this)
        fun OnLongClick(v:View?) {
            TODO("Not yet implemented")
            when(v?.id){
                R.id.MainCardRecyclerview -> {
                    val intent = Intent(this,Duoxuan::class.java)
                }
            }
        }
        OnLongClick(MainCardRecyclerview)
    }
}