package com.rizal.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.rizal.post.databinding.ActivityAddPostBinding
import java.io.File

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding
    private var selectedImageUri: Uri? = null
    private var editMode = false
    private var editPosition = -1
    private var oldImagePath: String? = null

    // Pilih gambar dari galeri
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imgPreview.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek apakah sedang edit post
        editMode = intent.getBooleanExtra("editMode", false)

        if (editMode) {
            editPosition = intent.getIntExtra("position", -1)
            binding.edtUsername.setText(intent.getStringExtra("username"))
            binding.edtCaption.setText(intent.getStringExtra("caption"))
            oldImagePath = intent.getStringExtra("imageUri")
            binding.btnSave.text = "Update Post"

            // Tampilkan gambar lama saat mode edit
            oldImagePath?.let {
                val file = File(it)
                if (file.exists()) binding.imgPreview.setImageURI(Uri.fromFile(file))
            }
        }

        // Tombol pilih gambar
        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Tombol simpan
        binding.btnSave.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val caption = binding.edtCaption.text.toString()
            var imagePath: String? = null

            // Jika user memilih gambar baru → simpan file baru
            if (selectedImageUri != null) {
                imagePath = persistImage(selectedImageUri!!)
            } else {
                // Jika editMode tapi tidak pilih gambar → pakai gambar lama
                imagePath = oldImagePath
            }

            val data = Intent().apply {
                putExtra("username", username)
                putExtra("caption", caption)
                putExtra("imageUri", imagePath)
                if (editMode) {
                    putExtra("editMode", true)
                    putExtra("position", editPosition)
                }
            }

            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    // Salin file gambar ke internal storage
    private fun persistImage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = "post_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            file.outputStream().use { output -> inputStream.copyTo(output) }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
