package com.rizal.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizal.post.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var postAdapter: PostAdapter
    private val postList = mutableListOf<Post>()
    private val storyList = mutableListOf<Story>()

    // Launcher untuk Add/Edit Post
    private val addPostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val editMode = data.getBooleanExtra("editMode", false)
            val username = data.getStringExtra("username") ?: ""
            val caption = data.getStringExtra("caption") ?: ""
            val imageResId = data.getIntExtra("imageResId", R.drawable.makan)
            val imageUriString = data.getStringExtra("imageUri")

            val post = if (imageUriString != null) {
                Post(username, caption, imageResId = null, imageUri = android.net.Uri.parse(imageUriString))
            } else {
                Post(username, caption, imageResId = imageResId, imageUri = null)
            }

            if (editMode) {
                val pos = data.getIntExtra("position", -1)
                if (pos >= 0 && pos < postList.size) {
                    postList[pos] = post
                    postAdapter.notifyItemChanged(pos)
                }
            } else {
                postList.add(0, post)
                postAdapter.notifyItemInserted(0)
                binding.recyclerView.scrollToPosition(0)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ====== DATA STORY (atas) ======
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

        // ====== DATA POST (bawah) ======
        postList.apply {
            add(Post("SIuu", "Makan gengss ", R.drawable.makan, null))
            add(Post("kambing_goat", "deres euyy! ☀️", R.drawable.hujan, null))
            add(Post("amel", "Makan 💚", R.drawable.makan, null))
        }

        // ====== INISIALISASI STORY RECYCLERVIEW ======
        binding.recyclerStory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerStory.adapter = StoryAdapter(storyList)

        // ====== INISIALISASI POST RECYCLERVIEW ======
        postAdapter = PostAdapter(this, postList, object : OnPostActionListener {
            override fun onEdit(position: Int, post: Post) {
                val intent = Intent(this@MainActivity, AddPostActivity::class.java)
                intent.putExtra("editMode", true)
                intent.putExtra("position", position)
                intent.putExtra("username", post.username)
                intent.putExtra("caption", post.caption)

                post.imageUri?.let {
                    intent.putExtra("imageUri", it.toString())
                } ?: intent.putExtra("imageResId", post.imageResId ?: R.drawable.makan)

                addPostLauncher.launch(intent)
            }

            override fun onDelete(position: Int) {
                postList.removeAt(position)
                postAdapter.notifyItemRemoved(position)
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = postAdapter

        // ====== FAB TAMBAH POST ======
        binding.fabAdd.setOnClickListener {
            addPostLauncher.launch(Intent(this, AddPostActivity::class.java))
        }
    }
}
