package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_edit_knowledge.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_study.*
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_knowledge.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.content.Context
import android.util.ArraySet
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_inspect_knowledge.*


class InspectKnowledgeActivity : AppCompatActivity() {

    private val tagList = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspect_knowledge)

        val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1)
        val extraData = intent.getIntExtra("ID",-1)
        val db = dbHelper.writableDatabase
        val cursor = db.query("Knowledge", null, "id=?",
            arrayOf(extraData.toString()), null, null, null)
        var ID = -1
        if (cursor.moveToFirst()) {
            do {
                Log.d("cursor", "initKnowledges: suc")
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val createdate = cursor.getString(cursor.getColumnIndex("createdate"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val studyTimes = cursor.getInt(cursor.getColumnIndex("studytimes"))
                val tag = cursor.getString(cursor.getColumnIndex("tag"))
                if(studyTimes == -1)continue
                ID = id
                showInspectContent(content)
                val Recursor = db.query("Knowledge", null, "createdate=?",
                    arrayOf(createdate), null, null, null)
                if(Recursor.moveToFirst()){
                    var outText = ""
                    var timeth = -1
                    do{
                        timeth++
                        val reviewdate = Recursor.getString(Recursor.getColumnIndex("reviewdate"))
                        if(timeth == 0)continue
                        outText = outText + reviewdate + "第" + timeth.toString() + "次学习\n"
                    }while (Recursor.moveToNext())
                    Recursor.close()
                    val kTag = Tag(tag)
                    val tSet = ArraySet<String>()
                    for (str in kTag.tSet!!) {
                        if (str != "") tSet.add(str)
                    }
                    showTag(tSet)
                    outText = outText + createdate+ "创建,复习" +studyTimes.toString() + "次"
                    showReviewContent(outText)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        val InspectViewer = findViewById<TextView>(R.id.InspectViewer)
        InspectViewer.setOnClickListener {
            val intent = Intent(this, EditKnowledgeActivity::class.java)
            intent.putExtra("ID", ID)
            startActivity(intent)
            finish()
        }
    }

    private fun showInspectContent(Content: String) {
        val InspectViewer = findViewById<TextView>(R.id.InspectViewer)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            InspectViewer.setText(Html.fromHtml(Content, Html.FROM_HTML_MODE_COMPACT))
        } else {
            InspectViewer.setText(Html.fromHtml(Content))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showTag(tagSet: ArraySet<String>) {
        val layoutManager= LinearLayoutManager(this)
        layoutManager.orientation= LinearLayoutManager.HORIZONTAL
       InspectRecyclerView.layoutManager = layoutManager
        tagList.clear()
        for (tag in tagSet) {
            tagList.add(tag)
        }
        val tagAdapter = TagAdapter(tagList)
        InspectRecyclerView.adapter = tagAdapter
    }

    private fun showReviewContent(Content: String) {
        val ReviewViewer = findViewById<TextView>(R.id.ReviewViewer)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //Content = Content.replace("\\n","\n")
            ReviewViewer.setText(Html.fromHtml(Content, Html.FROM_HTML_MODE_COMPACT))
        } else {
            ReviewViewer.setText(Html.fromHtml(Content))
        }
    }
}