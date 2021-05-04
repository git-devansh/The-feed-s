package com.devanshthakur.thefeeds.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.devanshthakur.thefeeds.R
import com.devanshthakur.thefeeds.dao.MyDao

class CreatePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        // init view
        val postButton = findViewById<Button>(R.id.post_button)
        val postEditText = findViewById<EditText>(R.id.post_edit_text)

        postButton.setOnClickListener {
            val postText: String = postEditText.text.toString().trim()
            if (postText.isNotEmpty()){
                val myDao = MyDao()
                myDao.addNewPost(postText)
                finish()
            }
        }
    }
}