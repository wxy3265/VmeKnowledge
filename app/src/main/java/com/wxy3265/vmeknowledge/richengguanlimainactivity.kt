package com.wxy3265.vmeknowledge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_richengguanlimainactivity.*
import kotlinx.android.synthetic.main.menu.*

class richengguanlimainactivity : AppCompatActivity() {
    val richengList = ArrayList<richengguanliCard>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_richengguanlimainactivity)
        setSupportActionBar(richengguanliToolbar)

        menumain.setOnClickListener {
            val intent_main = Intent(this , MainActivity::class.java)
            startActivity(intent_main)
        }


    }

    override fun onResume() {
        super.onResume()
        init_richeng()
        val layoutManager = GridLayoutManager(this , 1)
        richengguanliRecyclerview.layoutManager = layoutManager
        val adapter = richengguanliAdapter(this , richengList)
        richengguanliRecyclerview.adapter = adapter
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar2,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.richengguanliAdd -> startAdd2()
        }
        return true
    }
    private fun startAdd2()
    {
        val intentstartAdd = Intent(this , richengtianjia::class.java)
        startActivity(intentstartAdd)
    }
    private  fun init_richeng(){
        val dbHelper = richengDatabaseHelper(this , "richeng.db", 1 )
        val db = dbHelper.writableDatabase
        val cursor = db.query("richeng" , null , null , null , null , null , null ,)
        if(cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val time = cursor.getString(cursor.getColumnIndex("time"))
            }while (cursor.moveToNext())
        }
        cursor.close()

    }
}