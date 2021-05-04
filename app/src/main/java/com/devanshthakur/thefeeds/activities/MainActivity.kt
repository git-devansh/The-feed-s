package com.devanshthakur.thefeeds.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devanshthakur.thefeeds.AllPostAdapter
import com.devanshthakur.thefeeds.OnPostAdapter
import com.devanshthakur.thefeeds.R
import com.devanshthakur.thefeeds.dao.MyDao
import com.devanshthakur.thefeeds.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity(), OnPostAdapter {

    private lateinit var allPostAdapter: AllPostAdapter
    private lateinit var myDao: MyDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing MyDao
        myDao = MyDao()

        val postsCollection = myDao.postCollection
        val orderByQuery = postsCollection.orderBy("createdTime", Query.Direction.DESCENDING)


        val postRecyclerViewOption = FirestoreRecyclerOptions.Builder<Post>().setQuery(orderByQuery, Post::class.java).build()

        // Initializing adapter
        allPostAdapter = AllPostAdapter(postRecyclerViewOption, this)

        // RecyclerView
        val postRecyclerView: RecyclerView = findViewById(R.id.feeds_recycler_view)
        postRecyclerView.adapter = allPostAdapter
        postRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    fun onAddPost(view: View) {
        startActivity(Intent(this, CreatePostActivity::class.java))
    }


    override fun onStart() {
        super.onStart()
        allPostAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        allPostAdapter.stopListening()
    }




    override fun onPostItemClicked(postId: String) {
        val intent: Intent = Intent(this, PostDetailsActivity::class.java).apply {
            putExtra("POST_ID", postId)
        }
        startActivity(intent)
    }
}