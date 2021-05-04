package com.devanshthakur.thefeeds.dao

import com.devanshthakur.thefeeds.models.Comment
import com.devanshthakur.thefeeds.models.Post
import com.devanshthakur.thefeeds.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyDao {

    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val userCollection = db.collection("users")
    val postCollection = db.collection("posts")


    fun addUser(user: User?){
        user?.let {
            GlobalScope.launch(Dispatchers.IO){
                userCollection.document(user.userId).set(it)
            }
        }
    }

    private fun getUserByUserId(uId: String): Task<DocumentSnapshot>{
        return userCollection.document(uId).get()
    }

    fun addNewPost(postText: String){
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch {
            val user = getUserByUserId(currentUserId).await().toObject(User::class.java)!!
            val currentTime = System.currentTimeMillis()
            val post = Post(postText, user, currentTime)

            postCollection.document().set(post)
        }
    }


    fun getPostDocSnapByPostId(postId: String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }

    fun addNewComment(commentText: String, postId: String){
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch {
            val user = getUserByUserId(currentUserId).await().toObject(User::class.java)!!
            val currentTime = System.currentTimeMillis()
            postCollection.document(postId).collection("comments").add(Comment(commentText, user, postId))
        }
    }

    fun getPostCommentSnapByPostId(postId: String): CollectionReference {
        return postCollection.document(postId).collection("comments")
    }

}