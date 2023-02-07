package com.wxy3265.vmeknowledge


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class MyDatabaseHelper(val context: Context, name: String, version: Int):
        SQLiteOpenHelper(context, name, null, version) {
    private  val createKnowledgeList = "create table Knowledge (" +
                              " id integer primary key autoincrement," +
                              "content text," +
                              "tag text," +
                              "createdate text,"+
                              "studytimes integer," +
                              "milliTime integer," +
                              "reviewdate text)"
    private val createScheduleList = "create table Schedule (" +
            " id integer primary key autoincrement," +
            "content text," +
            "startTime text," +
            "endTime text," +
            "startMilliTime integer," +
            "endMilliTime integer," +
            "tag text," +
            "notification text)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createKnowledgeList)
        db.execSQL(createScheduleList)
        Toast.makeText(context, "首次启动创建数据库", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
    }
}
