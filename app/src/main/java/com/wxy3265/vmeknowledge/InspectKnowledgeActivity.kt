package com.wxy3265.vmeknowledge

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


class InspectKnowledgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspect_knowledge)

        val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1)
        val extraData = intent.getIntExtra("ID",-1)
        val db = dbHelper.writableDatabase
        val cursor = db.query("Knowledge", null, "id=?",
            arrayOf(extraData.toString()), null, null, null)

        if (cursor.moveToFirst()) {
            do {
                Log.d("cursor", "initKnowledges: suc")
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val createdate = cursor.getString(cursor.getColumnIndex("createdate"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val studyTimes = cursor.getInt(cursor.getColumnIndex("studytimes"))
                if(studyTimes == -1)continue
                showInspectContent(content)
                showReviewContent(createdate+ "共复习 次")
                val Recursor = db.query("Knowledge", null, "createdate=?",
                    arrayOf(createdate), null, null, null)
                if(Recursor.moveToFirst()){
                    do{
                        val reviewdate = Recursor.getString(Recursor.getColumnIndex("reviewdate"))
                        val studytimes = Recursor.getString(Recursor.getColumnIndex("studytimes"))
                        showReviewContent(reviewdate+ "第"+studytimes+"次学习")
                    }while (Recursor.moveToNext())
                    Recursor.close()
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    private fun showInspectContent(Content: String) {
        val InspectViewer = findViewById<TextView>(R.id.InspectViewer)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            InspectViewer.setText(Html.fromHtml(Content, Html.FROM_HTML_MODE_COMPACT))
        } else {
            InspectViewer.setText(Html.fromHtml(Content))
        }
    }

    private fun showReviewContent(Content: String) {
        val ReviewViewer = findViewById<TextView>(R.id.ReviewViewer)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ReviewViewer.setText(Html.fromHtml(Content, Html.FROM_HTML_MODE_COMPACT))
        } else {
            ReviewViewer.setText(Html.fromHtml(Content))
        }
    }
}