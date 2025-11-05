package com.rizal.post


import android.net.Uri

data class Post(
    val username: String,
    val caption: String,
    val imageResId: Int?,
    val imageUri: Uri?
)