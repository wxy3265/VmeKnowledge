package com.wxy3265.vmeknowledge

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_add_knowledge.*
import kotlinx.android.synthetic.main.activity_add_knowledge.AddButtonAdd
import kotlinx.android.synthetic.main.activity_add_knowledge.AddButtonDelete
import kotlinx.android.synthetic.main.activity_add_knowledge.AddEditor
import kotlinx.android.synthetic.main.activity_add_knowledge.AddTagButton
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_align_center
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_align_left
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_align_right
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_bg_color
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_blockquote
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_bold
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_heading1
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_heading2
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_heading3
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_heading4
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_heading5
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_heading6
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_indent
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_insert_audio
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_insert_bullets
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_insert_camera
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_insert_checkbox
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_insert_image
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_insert_link
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_insert_numbers
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_insert_video
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_italic
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_outdent
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_redo
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_strikethrough
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_subscript
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_superscript
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_txt_color
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_underline
import kotlinx.android.synthetic.main.activity_add_knowledge.Add_undo
import kotlinx.android.synthetic.main.activity_add_schedule.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
class AddScheduleActivity : AppCompatActivity() {
    private var TAG: String = "AddKnowledgeActivity"
    private val takePhoto = 1
    private val fromAlbum = 2
    private val fromAudio = 3
    private val fromVideo = 4
    private val forTag = 5
    private var tags = ""
    private lateinit var imageUri: Uri
    private lateinit var outputImage:File
    var et_startdate: EditText? = null

    var et_endDate: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)
        supportActionBar?.hide()

        AddEditor.setEditorHeight(200)
        AddEditor.setFontSize(22)
        AddEditor.setEditorFontColor(Color.BLACK)
        AddEditor.setPadding(10, 10, 10, 10)
        AddEditor.setPlaceholder("在这里输入...")
        AddEditor.focusEditor()

        AddTagButton.setOnClickListener {
            val ZhuanAddTag = Intent(this, ChooseTagActivity::class.java)
            startActivityForResult(ZhuanAddTag, forTag)
        }
        AddTimeButton.setOnClickListener {
            val AddTimeTag = Intent(this, ChooseDateActivity::class.java)
            startActivityForResult(AddTimeTag, forTag)
        }
        AddButtonAdd.setOnClickListener {
            if (AddEditor.html != null) {
                val dbHelper = MyDatabaseHelper(this, "Schedule.db", 1)
                val db = dbHelper.writableDatabase
                val formatter = SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss")
                val curDate = Date(System.currentTimeMillis())
                val date: String = formatter.format(curDate)
                val value = ContentValues().apply {
                    put("content", AddEditor.html)
                    put("tag", tags)
                }
                db.insert("Schedule", null, value)
                Toast.makeText(this, "创建成功", Toast.LENGTH_SHORT).show()
                AddEditor.html = ""
            }
            else Toast.makeText(this, "无法创建空日程", Toast.LENGTH_SHORT).show()
            finish()
        }
        AddButtonDelete.setOnClickListener { finish() }
        Add_undo.setOnClickListener { AddEditor.undo() }
        Add_redo.setOnClickListener { AddEditor.redo() }
        Add_bold.setOnClickListener { AddEditor.setBold() }
        Add_italic.setOnClickListener { AddEditor.setItalic() }
        Add_subscript.setOnClickListener { AddEditor.setSubscript() }
        Add_superscript.setOnClickListener { AddEditor.setSuperscript() }
        Add_strikethrough.setOnClickListener { AddEditor.setStrikeThrough() }
        Add_underline.setOnClickListener { AddEditor.setUnderline() }
        Add_heading1.setOnClickListener { AddEditor.setHeading(1) }
        Add_heading2.setOnClickListener { AddEditor.setHeading(2) }
        Add_heading3.setOnClickListener { AddEditor.setHeading(3) }
        Add_heading4.setOnClickListener { AddEditor.setHeading(4) }
        Add_heading5.setOnClickListener { AddEditor.setHeading(5) }
        Add_heading6.setOnClickListener { AddEditor.setHeading(6) }
        Add_txt_color.setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                AddEditor.setTextColor(if (isChanged) Color.BLACK else Color.RED)
                isChanged = !isChanged
            }
        })
        Add_bg_color.setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                AddEditor.setTextBackgroundColor(if (isChanged) Color.TRANSPARENT else Color.YELLOW)
                isChanged = !isChanged
            }
        })
        Add_indent.setOnClickListener { AddEditor.setIndent() }
        Add_outdent.setOnClickListener { AddEditor.setOutdent() }
        Add_align_left.setOnClickListener { AddEditor.setAlignLeft() }
        Add_align_center.setOnClickListener { AddEditor.setAlignCenter() }
        Add_align_right.setOnClickListener { AddEditor.setAlignRight() }
        Add_blockquote.setOnClickListener { AddEditor.setBlockquote() }
        Add_insert_bullets.setOnClickListener { AddEditor.setBullets() }
        Add_insert_numbers.setOnClickListener { AddEditor.setNumbers() }
        Add_insert_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, fromAlbum)

        }
        Add_insert_camera.setOnClickListener {
            val formatter = SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss")
            val curDate = Date(System.currentTimeMillis())
            val date: String = formatter.format(curDate)
            outputImage = File(externalCacheDir, "Knowledge" + date + ".jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this,
                    "com.wxy3265.vmeknowledge.fileprovider",
                    outputImage)
            } else {
                Uri.fromFile(outputImage)
            }
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, takePhoto)
        }

        Add_insert_audio.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            startActivityForResult(intent, fromAudio)
        }
        Add_insert_video.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            startActivityForResult(intent, fromVideo)
        }
        Add_insert_link.setOnClickListener {
            AddEditor.insertLink(
                "https://github.com/wasabeef",
                "wasabeef"
            )
        }
        Add_insert_checkbox.setOnClickListener { AddEditor.insertTodo() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    AddEditor.insertImage(
                        imageUri.toString(),
                        "dachshund", 320
                    )
                }
            }
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        AddEditor.insertImage(
                            uri.toString(),
                            "dachshund", 320
                        )
                    }
                }
            }
            fromAudio -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        AddEditor.insertAudio( uri.toString() )
                    }
                }
            }
            fromVideo -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        AddEditor.insertVideo( uri.toString() , 320)
                    }
                }
            }
            forTag -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val returnedData = data?.getStringExtra("tag_return")
                    Log.d("tagtest", "onActivityResult: " + returnedData)
                    if (returnedData != "" && returnedData != "null") tags = returnedData.toString()
                }
            }
        }
    }
}