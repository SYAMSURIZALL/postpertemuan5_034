package com.rizal.post


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.rizal.post.databinding.ActivityAddPostBinding

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding
    private var selectedImageUri: Uri? = null
    private var editMode = false
    private var editPosition = -1

    // ActivityResultLauncher untuk pilih gambar
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
            binding.btnSave.text = "Update Post"
        }

        // Tombol untuk pilih gambar dari galeri
        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Tombol Simpan
        binding.btnSave.setOnClickListener {
            val data = Intent()
            data.putExtra("username", binding.edtUsername.text.toString())
            data.putExtra("caption", binding.edtCaption.text.toString())

            // Jika user memilih gambar dari galeri, simpan URI-nya
            data.putExtra("imageUri", selectedImageUri?.toString())

            if (editMode) {
                data.putExtra("editMode", true)
                data.putExtra("position", editPosition)
            }

            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }
}