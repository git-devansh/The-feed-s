package com.devanshthakur.thefeeds.models

data class Comment(
    val comment: String = "",
    val commentedByUser: User? = null,
    val postId: String = ""
)