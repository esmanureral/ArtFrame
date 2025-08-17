package com.esmanureral.artframe

import android.view.View
import android.widget.ImageView
import coil.load
import com.facebook.shimmer.ShimmerFrameLayout

fun ImageView.loadWithShimmer(
    url: String,
    shimmerLayout: ShimmerFrameLayout,
    errorRes: Int = R.drawable.error,
    placeRes:Int=R.drawable.logo
) {
    this.load(url) {
        crossfade(true)
        placeholder(placeRes)
        error(errorRes)
        listener(
            onSuccess = { _, _ ->
                shimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                this@loadWithShimmer.visibility = View.VISIBLE
            },
            onError = { _, _ ->
                shimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                this@loadWithShimmer.setImageResource(errorRes)
                this@loadWithShimmer.visibility = View.VISIBLE
            }
        )
    }
}
