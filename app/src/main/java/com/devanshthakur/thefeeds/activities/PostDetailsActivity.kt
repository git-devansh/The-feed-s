package com.devanshthakur.thefeeds.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.devanshthakur.thefeeds.R
import com.devanshthakur.thefeeds.dao.MyDao
import com.devanshthakur.thefeeds.models.Comment
import com.devanshthakur.thefeeds.models.Post
import com.devanshthakur.thefeeds.utility.TimeAgo
import com.squareup.picasso.Picasso


class PostDetailsActivity : AppCompatActivity() {

    private lateinit var userImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var createdTimeTextView: TextView
    private lateinit var postTextView: TextView
    private lateinit var commentEditText: EditText
    private lateinit var commentButton: Button
    private lateinit var commentsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        // getting post id
        val postId = intent.getStringExtra("POST_ID")

        // initialize all views
        initViews()

        val myDao: MyDao = MyDao()

        myDao.getPostDocSnapByPostId(postId!!).addOnSuccessListener { documentSnap ->
            if (documentSnap.exists()){

                val post = documentSnap.toObject(Post::class.java)!!

                Picasso.get().load(post.createdBy!!.imageUrl).into(userImageView)
                userNameTextView.text = post.createdBy.name
                postTextView.text = post.postText
                createdTimeTextView.text = "${TimeAgo.getTimeAgo(post.createdTime!!)}"


            }

        }


        myDao.getPostCommentSnapByPostId(postId).addSnapshotListener{ snapshot, e ->

            if (e != null) {
                Log.w("GetPostCommentSnapBy", "Listen failed.", e)
                return@addSnapshotListener
            }

            var count = 0
            if (snapshot != null) {
                commentsLayout.removeAllViews()
                for (doc in snapshot){
                    val comment = doc.toObject(Comment::class.java)
                    //Log.i("PostDetailsActivity:", comment.toString())

                    //Get the inflated layout
                    val view = LayoutInflater.from(this).inflate(R.layout.comment_single_item, null, false)
                    val commentTextView = view.findViewById<TextView>(R.id.comment_user_text_view)
                    val commentedBy = view.findViewById<TextView>(R.id.commented_by_text_view)
                    val imgView = view.findViewById<ImageView>(R.id.comment_user_image_view)

                    commentTextView.text =  comment.comment
                    commentedBy.text = "Commented by: ${comment.commentedByUser!!.name}"
                    Picasso.get().load(comment.commentedByUser.imageUrl).into(imgView)


                    commentsLayout.addView(view)

                    count++
                }
            }

            findViewById<TextView>(R.id.comments_text_view).text = "$count comments"
            commentEditText.setText("")
        }


        commentButton.setOnClickListener {
            val comment: String = commentEditText.text.toString().trim()
            if (comment.isNotEmpty()){
                myDao.addNewComment(comment, postId)
            }

        }


    }

    private fun initViews() {
        userImageView = findViewById(R.id.comment_user_image_view)
        userNameTextView = findViewById(R.id.details_activity_user_name_text_view)
        createdTimeTextView = findViewById(R.id.details_activity_created_time_text_view)
        postTextView = findViewById(R.id.details_activity_post_text_view)
        commentEditText = findViewById(R.id.comment_edit_text)
        commentButton = findViewById(R.id.comment_post_button)
        commentsLayout = findViewById(R.id.comments_layout)
    }
}