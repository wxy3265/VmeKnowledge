package com.wxy3265.vmeknowledge

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_add_knowledge.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class AddKnowledgeActivity : AppCompatActivity() {
    private var TAG: String = "AddKnowledgeActivity"
    val takePhoto = 1
    val fromAlbum = 2
    val fromAudio = 3
    val fromVideo = 4
    lateinit var imageUri: Uri
    lateinit var outputImage:File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_knowledge)
        supportActionBar?.hide()

        AddEditor.setEditorHeight(200)
        AddEditor.setFontSize(22)
        AddEditor.setEditorFontColor(Color.BLACK)
        AddEditor.setPadding(10, 10, 10, 10)
        AddEditor.setPlaceholder("在这里输入...")
        AddEditor.focusEditor()

        AddButtonAdd.setOnClickListener {
            val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1)
            val db = dbHelper.writableDatabase
            val formatter: SimpleDateFormat = SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss")
            val curDate: Date = Date(System.currentTimeMillis())
            val date: String = formatter.format(curDate)
            val value = ContentValues().apply {
                put("content", AddEditor.html)
                put("studytimes", 0)
                put("reviewdate", date)
            }
            db.insert("Knowledge", null, value)
            Toast.makeText(this, "创建成功", Toast.LENGTH_SHORT).show()
            AddEditor.html = ""
        }
        AddButtonDelete.setOnClickListener { finish() }

        action_undo.setOnClickListener { AddEditor.undo() }
        action_redo.setOnClickListener { AddEditor.redo() }
        action_bold.setOnClickListener { AddEditor.setBold() }
        action_italic.setOnClickListener { AddEditor.setItalic() }
        action_subscript.setOnClickListener { AddEditor.setSubscript() }
        action_superscript.setOnClickListener { AddEditor.setSuperscript() }
        action_strikethrough.setOnClickListener { AddEditor.setStrikeThrough() }
        action_underline.setOnClickListener { AddEditor.setUnderline() }
        action_heading1.setOnClickListener { AddEditor.setHeading(1) }
        action_heading2.setOnClickListener { AddEditor.setHeading(2) }
        action_heading3.setOnClickListener { AddEditor.setHeading(3) }
        action_heading4.setOnClickListener { AddEditor.setHeading(4) }
        action_heading5.setOnClickListener { AddEditor.setHeading(5) }
        action_heading6.setOnClickListener { AddEditor.setHeading(6) }
        action_txt_color.setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                AddEditor.setTextColor(if (isChanged) Color.BLACK else Color.RED)
                isChanged = !isChanged
            }
        })
        action_bg_color.setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                AddEditor.setTextBackgroundColor(if (isChanged) Color.TRANSPARENT else Color.YELLOW)
                isChanged = !isChanged
            }
        })
        action_indent.setOnClickListener { AddEditor.setIndent() }
        action_outdent.setOnClickListener { AddEditor.setOutdent() }
        action_align_left.setOnClickListener { AddEditor.setAlignLeft() }
        action_align_center.setOnClickListener { AddEditor.setAlignCenter() }
        action_align_right.setOnClickListener { AddEditor.setAlignRight() }
        action_blockquote.setOnClickListener { AddEditor.setBlockquote() }
        action_insert_bullets.setOnClickListener { AddEditor.setBullets() }
        action_insert_numbers.setOnClickListener { AddEditor.setNumbers() }
        action_insert_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, fromAlbum)

        }
        action_insert_camera.setOnClickListener {
            outputImage = File(externalCacheDir, "output_image.jpg")
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

        action_insert_audio.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            startActivityForResult(intent, fromAudio)
        }
        action_insert_video.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            startActivityForResult(intent, fromVideo)
        }
        action_insert_link.setOnClickListener {
            AddEditor.insertLink(
                "https://github.com/wasabeef",
                "wasabeef"
            )
        }
        action_insert_checkbox.setOnClickListener { AddEditor.insertTodo() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
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
        }
    }
}