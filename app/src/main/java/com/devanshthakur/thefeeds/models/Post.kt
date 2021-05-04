package com.devanshthakur.thefeeds.models

data class Post(
    val postText: String? = null,
    val createdBy: User? = null,
    val createdTime: Long? = null,
    val comments: ArrayList<String> = ArrayList()
)