package com.esmanureral.artframe

import android.view.View
import android.widget.ImageView
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.material.progressindicator.CircularProgressIndicator

fun ImageView.loadWithIndicator(
    url: String,
    progressIndicator: CircularProgressIndicator,
    errorRes: Int,
) {
    val request = ImageRequest.Builder(this.context)
        .data(url)
        .target(this)
        .error(errorRes)
        .listener(
            onStart = {
                progressIndicator.visibility = View.VISIBLE
            },
            onSuccess = { _, _ ->
                progressIndicator.visibility = View.GONE
            },
            onError = { _, _ ->
                progressIndicator.visibility = View.GONE
                this.setImageResource(errorRes)
            }
        )
        .build()

    ImageLoader(this.context).enqueue(request)
}