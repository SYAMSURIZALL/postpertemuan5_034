package com.rizal.post

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.rizal.post.databinding.ItemPostBinding
import java.io.File

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

        val uri = post.imageUri
        if (uri != null) {
            val file = File(uri.path ?: "")
            if (file.exists()) {
                holder.b.imageView.setImageURI(Uri.fromFile(file))
            } else {
                holder.b.imageView.setImageResource(R.drawable.makan)
            }
        } else {
            holder.b.imageView.setImageResource(R.drawable.makan)
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
