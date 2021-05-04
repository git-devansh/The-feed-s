package com.devanshthakur.thefeeds

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devanshthakur.thefeeds.models.Post
import com.devanshthakur.thefeeds.utility.TimeAgo
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso

class AllPostAdapter(options: FirestoreRecyclerOptions<Post>, val listener: OnPostAdapter): FirestoreRecyclerAdapter<Post, AllPostAdapter.MyViewHolder>(options) {

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userText: TextView = itemView.findViewById<TextView>(R.id.details_activity_user_name_text_view)
        val userImg: ImageView = itemView.findViewById<ImageView>(R.id.comment_user_image_view)
        val createdTime: TextView = itemView.findViewById<TextView>(R.id.details_activity_created_time_text_view)
        val postText: TextView = itemView.findViewById<TextView>(R.id.details_activity_list_post_text_view)

        init {
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onPostItemClicked(snapshots.getSnapshot(adapterPosition).id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_post_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, post: Post) {
        holder.userText.text = post.createdBy!!.name
        Picasso.get().load(post.createdBy.imageUrl).into(holder.userImg)
        holder.createdTime.text = TimeAgo.getTimeAgo(post.createdTime!!)
        holder.postText.text = post.postText
    }
}


interface OnPostAdapter{
    fun onPostItemClicked(postId: String)
}