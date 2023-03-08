package com.wxy3265.vmeknowledge

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.FileProvider
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import kotlinx.android.synthetic.main.activity_edit_schedule.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditScheduleActivity : AppCompatActivity() {
    private var TAG: String = "EditKnowledgeActivity"
    val takePhoto = 1
    val fromAlbum = 2
    val fromAudio = 3
    val fromVideo = 4
    lateinit var imageUri: Uri
    lateinit var outputImage: File
    var startMilliTime: Long = 0
    var endMilliTime: Long = 0

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_schedule)
        val dbHelper = MyDatabaseHelper(this, "Schedule.db", 1)
        supportActionBar?.hide()

        val ChangeTagButton: Button = findViewById(R.id.ChangeTagS)
        ChangeTagButton.visibility = View.GONE
        ChangeTagButton.setOnClickListener {
            val ZhuanAddTag = Intent(this, ChooseKnowledgeTagActivity::class.java)
            startActivity(ZhuanAddTag)
        }

        ChangeTimeS.setOnClickListener {
            CardDatePickerDialog.builder(this)
                .setTitle("设置日期")
                .setPickerLayout(R.layout.date_picker)
                .showBackNow(false)
                .setThemeColor(Color.rgb(98, 0, 238))
                .setOnChoose {
                    val chosenDate: Long = it
                    Log.d(TAG, "onCreate: cho" + chosenDate)
                    CardDatePickerDialog.builder(this)
                        .setTitle("设置开始时间")
                        .showBackNow(false)
                        .setPickerLayout(R.layout.time_picker)
                        .setDefaultTime(chosenDate)
                        .setThemeColor(Color.rgb(98, 0, 238))
                        .setOnChoose {
                            startMilliTime = it
                            CardDatePickerDialog.builder(this)
                                .setTitle("设置结束时间")
                                .setPickerLayout(R.layout.time_picker)
                                .showBackNow(false)
                                .setMinTime(startMilliTime + 60000)
                                .setDefaultTime(startMilliTime + 60000)
                                .setThemeColor(Color.rgb(98, 0, 238))
                                .setOnChoose {
                                    endMilliTime = it
                                }.build().show()
                        }.build().show()
                }.build().show()
        }

        val extraData = intent.getIntExtra("ID",-1)
        if (extraData == -1) {
            Toast.makeText(this, "打开错误", Toast.LENGTH_SHORT).show()
        }
        Log.d("EditAc", "onCreate: OpenID:" + extraData.toString())
        val db = dbHelper.writableDatabase
        val cursor = db.query("Schedule", null, "id=?",
            arrayOf(extraData.toString()), null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val content = cursor.getString(cursor.getColumnIndex("content"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                EditEditorS.html = content
                startMilliTime = cursor.getLong(cursor.getColumnIndex("startMilliTime"))
                endMilliTime = cursor.getLong(cursor.getColumnIndex("endMilliTime"))
            } while (cursor.moveToNext())
            cursor.close()
        }
        Log.d(TAG, "onCreate: " + EditEditorS.html)
        EditEditorS.setEditorHeight(200)
        EditEditorS.setFontSize(22)
        EditEditorS.setEditorFontColor(Color.BLACK)
        EditEditorS.setPadding(10, 10, 10, 10)
        EditEditorS.setPlaceholder("在这里输入...")
        EditEditorS.focusEditor()

        EditSButtonSave.setOnClickListener {
            val values = ContentValues()
            values.put("Content", EditEditorS.html)
            values.put("startMilliTime", startMilliTime)
            values.put("endMilliTime", endMilliTime)
            db.update("Schedule", values, "id = ?", arrayOf(extraData.toString()))
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
            finish()
        }
        EditSButtonDelete.setOnClickListener {
            db.delete("Schedule", "id = ?", arrayOf(extraData.toString()))
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
            finish()
        }

        EditS_undo.setOnClickListener { EditEditorS.undo() }
        EditS_redo.setOnClickListener { EditEditorS.redo() }
        EditS_bold.setOnClickListener { EditEditorS.setBold() }
        EditS_italic.setOnClickListener { EditEditorS.setItalic() }
        EditS_subscript.setOnClickListener { EditEditorS.setSubscript() }
        EditS_superscript.setOnClickListener { EditEditorS.setSuperscript() }
        EditS_strikethrough.setOnClickListener { EditEditorS.setStrikeThrough() }
        EditS_underline.setOnClickListener { EditEditorS.setUnderline() }
        EditS_heading1.setOnClickListener { EditEditorS.setHeading(1) }
        EditS_heading2.setOnClickListener { EditEditorS.setHeading(2) }
        EditS_heading3.setOnClickListener { EditEditorS.setHeading(3) }
        EditS_heading4.setOnClickListener { EditEditorS.setHeading(4) }
        EditS_heading5.setOnClickListener { EditEditorS.setHeading(5) }
        EditS_heading6.setOnClickListener { EditEditorS.setHeading(6) }
        EditS_txt_color.setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                EditEditorS.setTextColor(if (isChanged) Color.BLACK else Color.RED)
                isChanged = !isChanged
            }
        })
        EditS_bg_color.setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                EditEditorS.setTextBackgroundColor(if (isChanged) Color.TRANSPARENT else Color.YELLOW)
                isChanged = !isChanged
            }
        })
        EditS_indent.setOnClickListener { EditEditorS.setIndent() }
        EditS_outdent.setOnClickListener { EditEditorS.setOutdent() }
        EditS_align_left.setOnClickListener { EditEditorS.setAlignLeft() }
        EditS_align_center.setOnClickListener { EditEditorS.setAlignCenter() }
        EditS_align_right.setOnClickListener { EditEditorS.setAlignRight() }
        EditS_blockquote.setOnClickListener { EditEditorS.setBlockquote() }
        EditS_insert_bullets.setOnClickListener { EditEditorS.setBullets() }
        EditS_insert_numbers.setOnClickListener { EditEditorS.setNumbers() }
        EditS_insert_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, fromAlbum)

        }
        EditS_insert_camera.setOnClickListener {
            val formatter = SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss")
            val curDate = Date(System.currentTimeMillis())
            val date: String = formatter.format(curDate)
            outputImage = File(externalCacheDir, "Schedule" + date + ".jpg")
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

        EditS_insert_audio.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            startActivityForResult(intent, fromAudio)
        }
        EditS_insert_video.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            startActivityForResult(intent, fromVideo)
        }
        EditS_insert_link.setOnClickListener {
            EditEditorS.insertLink(
                "https://github.com/wasabeef",
                "wasabeef"
            )
        }
        EditS_insert_checkbox.setOnClickListener { EditEditorS.insertTodo() }
    }
}