package com.rizal.post

interface OnPostActionListener {
    fun onEdit(position: Int, post: Post)
    fun onDelete(position: Int)
}