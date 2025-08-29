package com.esmanureral.artframe

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import coil.load

fun ImageView.loadWithIndicator(
    url: String?,
    progressIndicator: View,
    errorRes: Int,
    lifecycleOwner: LifecycleOwner? = null,
    onSuccess: (() -> Unit)? = null,
    onError: (() -> Unit)? = null
) {
    progressIndicator.isVisible = true
    this.load(url) {
        crossfade(true)
        error(errorRes)
        lifecycle(lifecycleOwner)
        listener(
            onSuccess = { _, _ ->
                progressIndicator.isVisible = false
                onSuccess?.invoke()
            },
            onError = { _, result ->
                progressIndicator.isVisible = false
                this@loadWithIndicator.setImageResource(errorRes)
                onError?.invoke()
            }
        )
    }
}