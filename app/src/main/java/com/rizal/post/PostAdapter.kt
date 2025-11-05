package com.rizal.post

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.rizal.post.databinding.ItemPostBinding

class PostAdapter(
    private val context: Context,
    private val posts: MutableList<Post>,
    private val listener: OnPostActionListener
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val b: ItemPostBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val b = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(b)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.b.username.text = post.username
        holder.b.caption.text = post.caption

        if (post.imageUri != null) {
            holder.b.imageView.setImageURI(post.imageUri)
        } else {
            post.imageResId?.let { holder.b.imageView.setImageResource(it) }
        }

        holder.b.btnMenu.setOnClickListener { v ->
            val popup = PopupMenu(context, v)
            popup.menuInflater.inflate(R.menu.menu_post, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        listener.onEdit(position, post)
                        true
                    }
                    R.id.action_delete -> {
                        listener.onDelete(position)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = posts.size
}