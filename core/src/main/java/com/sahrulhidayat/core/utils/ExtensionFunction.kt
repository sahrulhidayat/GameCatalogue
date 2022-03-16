package com.sahrulhidayat.core.utils

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sahrulhidayat.core.R

fun Context.loadImage(url: String?, imageView: ImageView) {
    Glide.with(this)
        .load(url)
        .apply(
            RequestOptions
                .placeholderOf(R.drawable.ic_image_loading)
                .error(R.drawable.ic_image_error)
        )
        .centerCrop()
        .into(imageView)
}

fun Context.showShortToast(message: CharSequence): Toast {
    return Toast.makeText(this, message, Toast.LENGTH_SHORT)
        .apply { show() }
}