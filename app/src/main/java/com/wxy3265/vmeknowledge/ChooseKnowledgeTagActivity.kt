package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArraySet
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_choose_tag.*

class ChooseKnowledgeTagActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    private val tagSet = ArraySet<String>()
    private val tagList = ArrayList<String>()
    private val choosenTag = Tag()
    private val TAG = "ChooseTagActivity"

    interface OnTagSaveListener {
        fun onTagSave(tag: Tag)
    }
    private var mOnTagSaveListener: OnTagSaveListener? = null

    fun setOnTagSaveListener(onTagSaveListener: OnTagSaveListener) {
        mOnTagSaveListener = onTagSaveListener
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_tag)
        initTags()
        showTags(tagSet)

        ChooseAddButton.setOnClickListener {
            if (ChooseAddEditor.text != null) {
                tagSet.add(ChooseAddEditor.text.toString())
                showTags(tagSet)
            }
            ChooseAddButton.text = null
        }

        ChooseTagAdd.setOnClickListener {
            Log.d("tagtest", "onCreate: " + choosenTag.orstr)
            val intent = Intent()
            intent.putExtra("tag_return", choosenTag.orstr.toString())
            setResult(RESULT_OK, intent)
            finish()
        }

        ChooseTagCancel.setOnClickListener {
            val intent = Intent()
            intent.putExtra("tag_return", "")
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("Range")
    private fun initTags() {
        val extraData = intent.getStringExtra("tags")
        val defTag = extraData?.let { Tag(it) }
        if (defTag != null) {
            for (tag in defTag.tSet!!) {
                choosenTag.addTag(tag)
            }
        }
        val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1)
        val db = dbHelper.writableDatabase
        val cursor = db.query("Knowledge", null, null, null,
            null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val tag = cursor.getString(cursor.getColumnIndex("tag"))
                var ktag = Tag()
                if (tag != null) ktag = Tag(tag)
                Log.d(TAG, "initTags: ktag:" + ktag.orstr.toString())
                if (ktag.orstr != "") {
                    for (str in ktag.tSet!!) {
                        if (str != "") tagSet.add(str)
                    }
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showTags(tSet: ArraySet<String>) {
        val layoutManager = LinearLayoutManager(this)
        ChooseRecyclerView.layoutManager = layoutManager
        tagList.clear()
        for (tag in tSet) {
            tagList.add(tag)
        }
        val adapter = ChooseAdapter(tagList)
        ChooseRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object: ChooseAdapter.OnItemClickListener{
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onItemClick(position: Int, holder: ChooseAdapter.ViewHolder) {
                if (holder.TagName.isChecked == true) choosenTag.addTag(holder.TagName.text.toString())
                else choosenTag.removeTag(holder.TagName.text.toString())
            }
        })
    }
}