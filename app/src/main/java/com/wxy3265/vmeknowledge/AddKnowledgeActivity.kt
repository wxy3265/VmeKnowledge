package com.wxy3265.vmeknowledge

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wxy3265.vmeknowledge.KnowledgeEditor.OnTextChangeListener
import kotlinx.android.synthetic.main.activity_add_knowledge.*


class AddKnowledgeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_knowledge)
        supportActionBar?.hide()
        AddEditor.setEditorHeight(200)
        AddEditor.setFontSize(22)
        AddEditor.setEditorFontColor(Color.BLACK)
        AddEditor.setPadding(10, 10, 10, 10)
        AddEditor.setPlaceholder("Insert text here...")
        AddEditor.setOnTextChangeListener(OnTextChangeListener { text -> // Do Something
            AddEditorPreview.setText(text)
        })
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
            AddEditor.insertImage(
                "https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg",
                "dachshund", 320
            )
        }
        action_insert_youtube.setOnClickListener {
            AddEditor.insertYoutubeVideo(
                "https://www.youtube.com/embed/pS5peqApgUA"
            )
        }

        action_insert_audio.setOnClickListener {
            AddEditor.insertAudio(
                "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_5MG.mp3"
            )
        }
        action_insert_video.setOnClickListener {
            AddEditor.insertVideo(
                "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_10MB.mp4",
                360
            )
        }
        action_insert_link.setOnClickListener {
            AddEditor.insertLink(
                "https://github.com/wasabeef",
                "wasabeef"
            )
        }
        action_insert_checkbox.setOnClickListener { AddEditor.insertTodo() }
    }
}