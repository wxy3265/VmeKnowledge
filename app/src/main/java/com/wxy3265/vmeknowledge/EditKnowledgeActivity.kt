package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
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
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_edit_knowledge.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class EditKnowledgeActivity : AppCompatActivity() {
    private var TAG: String = "EditKnowledgeActivity"
    val takePhoto = 1
    val fromAlbum = 2
    val fromAudio = 3
    val fromVideo = 4
    lateinit var imageUri: Uri
    lateinit var outputImage:File
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        val dbHelper = MyDatabaseHelper(this, "Knowledge.db", 1)
        Log.d("EditAc", "acces")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_knowledge)
        supportActionBar?.hide()

        val ChangeTagButton: Button = findViewById(R.id.ChangeTag)
        ChangeTagButton.setOnClickListener {
            val ZhuanAddTag = Intent(this, ChooseTagActivity::class.java)
            startActivity(ZhuanAddTag)
        }

        val extraData = intent.getIntExtra("ID",-1)
        if (extraData == -1) {
            Toast.makeText(this, "打开错误", Toast.LENGTH_SHORT).show()
        }
        Log.d("EditAc", "onCreate: OpenID:" + extraData.toString())
        val db = dbHelper.writableDatabase
        val cursor = db.query("Knowledge", null, "id=?",
            arrayOf(extraData.toString()), null, null, null)
        if (cursor.moveToFirst()) {
            do {
                Log.d("cursor", "initKnowledges: suc")
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                EditEditor.html = content
            } while (cursor.moveToNext())
            cursor.close()
        }

        EditEditor.setEditorHeight(200)
        EditEditor.setFontSize(22)
        EditEditor.setEditorFontColor(Color.BLACK)
        EditEditor.setPadding(10, 10, 10, 10)
        EditEditor.setPlaceholder("在这里输入...")
        EditEditor.focusEditor()

        EditButtonSave.setOnClickListener {
            val values = ContentValues()
            values.put("Content", EditEditor.html)
            db.update("Knowledge", values, "id = ?", arrayOf(extraData.toString()))
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
            finish()
        }
        EditButtonDelete.setOnClickListener {
            db.delete("Knowledge", "id = ?", arrayOf(extraData.toString()))
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
            finish()
        }

        Edit_undo.setOnClickListener { EditEditor.undo() }
        Edit_redo.setOnClickListener { EditEditor.redo() }
        Edit_bold.setOnClickListener { EditEditor.setBold() }
        Edit_italic.setOnClickListener { EditEditor.setItalic() }
        Edit_subscript.setOnClickListener { EditEditor.setSubscript() }
        Edit_superscript.setOnClickListener { EditEditor.setSuperscript() }
        Edit_strikethrough.setOnClickListener { EditEditor.setStrikeThrough() }
        Edit_underline.setOnClickListener { EditEditor.setUnderline() }
        Edit_heading1.setOnClickListener { EditEditor.setHeading(1) }
        Edit_heading2.setOnClickListener { EditEditor.setHeading(2) }
        Edit_heading3.setOnClickListener { EditEditor.setHeading(3) }
        Edit_heading4.setOnClickListener { EditEditor.setHeading(4) }
        Edit_heading5.setOnClickListener { EditEditor.setHeading(5) }
        Edit_heading6.setOnClickListener { EditEditor.setHeading(6) }
        Edit_txt_color.setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                EditEditor.setTextColor(if (isChanged) Color.BLACK else Color.RED)
                isChanged = !isChanged
            }
        })
        Edit_bg_color.setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                EditEditor.setTextBackgroundColor(if (isChanged) Color.TRANSPARENT else Color.YELLOW)
                isChanged = !isChanged
            }
        })
        Edit_indent.setOnClickListener { EditEditor.setIndent() }
        Edit_outdent.setOnClickListener { EditEditor.setOutdent() }
        Edit_align_left.setOnClickListener { EditEditor.setAlignLeft() }
        Edit_align_center.setOnClickListener { EditEditor.setAlignCenter() }
        Edit_align_right.setOnClickListener { EditEditor.setAlignRight() }
        Edit_blockquote.setOnClickListener { EditEditor.setBlockquote() }
        Edit_insert_bullets.setOnClickListener { EditEditor.setBullets() }
        Edit_insert_numbers.setOnClickListener { EditEditor.setNumbers() }
        Edit_insert_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, fromAlbum)

        }
        Edit_insert_camera.setOnClickListener {
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

        Edit_insert_audio.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            startActivityForResult(intent, fromAudio)
        }
        Edit_insert_video.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            startActivityForResult(intent, fromVideo)
        }
        Edit_insert_link.setOnClickListener {
            EditEditor.insertLink(
                "https://github.com/wasabeef",
                "wasabeef"
            )
        }
        Edit_insert_checkbox.setOnClickListener { EditEditor.insertTodo() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    EditEditor.insertImage(
                        imageUri.toString(),
                        "dachshund", 320
                    )
                }
            }
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        EditEditor.insertImage(
                            uri.toString(),
                            "dachshund", 320
                        )
                    }
                }
            }
            fromAudio -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        EditEditor.insertAudio( uri.toString() )
                    }
                }
            }
            fromVideo -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        EditEditor.insertVideo( uri.toString() , 320)
                    }
                }
            }
        }
    }
}