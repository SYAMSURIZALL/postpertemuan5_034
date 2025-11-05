package com.rizal.post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizal.post.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var postAdapter: PostAdapter
    private val postList = mutableListOf<Post>()
    private val storyList = mutableListOf<Story>()
    private lateinit var db: AppDatabase
    private lateinit var dao: PostDao

    // Launcher untuk Add/Edit Post
    private val addPostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val editMode = data.getBooleanExtra("editMode", false)
            val username = data.getStringExtra("username") ?: ""
            val caption = data.getStringExtra("caption") ?: ""
            val imageUriString = data.getStringExtra("imageUri")

            if (editMode) {
                val pos = data.getIntExtra("position", -1)
                if (pos >= 0 && pos < postList.size) {
                    val oldPost = postList[pos]

                    val finalImageUriString = imageUriString ?: oldPost.imageUri?.path

                    val updatedPost = Post(
                        username,
                        caption,
                        null,
                        finalImageUriString?.let { Uri.fromFile(File(it)) }
                    )

                    postList[pos] = updatedPost
                    postAdapter.notifyItemChanged(pos)

                    lifecycleScope.launch {
                        val allPosts = dao.getAll()
                        val oldEntity = allPosts[allPosts.size - 1 - pos]
                        dao.update(
                            PostEntity(
                                id = oldEntity.id,
                                username = username,
                                caption = caption,
                                imageUri = finalImageUriString
                            )
                        )
                    }
                }
            } else {
                val newPost = Post(
                    username,
                    caption,
                    null,
                    imageUriString?.let { Uri.fromFile(File(it)) }
                )

                postList.add(0, newPost)
                postAdapter.notifyItemInserted(0)
                binding.recyclerView.scrollToPosition(0)

                lifecycleScope.launch {
                    dao.insert(
                        PostEntity(
                            username = username,
                            caption = caption,
                            imageUri = imageUriString
                        )
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        dao = db.postDao()

        // ====== STORY DATA ======
        storyList.apply {
            add(Story("Maxzie", R.drawable.gmbr))
            add(Story("kambing_goat", R.drawable.gmbr2))
            add(Story("rubi_community", R.drawable.gmbr1))
            add(Story("rizka", R.drawable.gmbr2))
            add(Story("amel", R.drawable.gmbr1))
            add(Story("lia", R.drawable.gmbr2))
            add(Story("burhan", R.drawable.gmbr))
            add(Story("rizki", R.drawable.gmbr1))
        }

        binding.recyclerStory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerStory.adapter = StoryAdapter(storyList)

        // ====== POST ADAPTER ======
        postAdapter = PostAdapter(this, postList, object : OnPostActionListener {
            override fun onEdit(position: Int, post: Post) {
                val intent = Intent(this@MainActivity, AddPostActivity::class.java).apply {
                    putExtra("editMode", true)
                    putExtra("position", position)
                    putExtra("username", post.username)
                    putExtra("caption", post.caption)
                    putExtra("imageUri", post.imageUri?.path)
                }
                addPostLauncher.launch(intent)
            }

            override fun onDelete(position: Int) {
                val deletedPost = postList[position]
                postList.removeAt(position)
                postAdapter.notifyItemRemoved(position)

                lifecycleScope.launch {
                    val allPosts = dao.getAll()
                    if (position in allPosts.indices) {
                        val entity = allPosts[allPosts.size - 1 - position]
                        dao.delete(entity)
                    }
                    // (Opsional) hapus file fisik dari internal storage
                    deletedPost.imageUri?.path?.let { path ->
                        val file = File(path)
                        if (file.exists()) file.delete()
                    }
                }
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = postAdapter

        binding.fabAdd.setOnClickListener {
            addPostLauncher.launch(Intent(this, AddPostActivity::class.java))
        }

        // ====== LOAD DATABASE ======
        lifecycleScope.launch {
            val savedPosts = dao.getAll()
            postList.clear()
            postList.addAll(savedPosts.map {
                val file = it.imageUri?.let { path -> File(path) }
                val uri = file?.let { f -> if (f.exists()) Uri.fromFile(f) else null }
                Post(it.username, it.caption, null, uri)
            })
            postAdapter.notifyDataSetChanged()
        }
    }
}
