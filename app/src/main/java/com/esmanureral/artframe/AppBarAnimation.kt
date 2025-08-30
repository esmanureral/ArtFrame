package com.esmanureral.artframe

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

fun AppBarLayout.animateCollapseExpand(
    fraction: Float = 0.3f,
    duration: Long = 800L,
    startDelay: Long = 700L
) {
    post {
        val params = layoutParams as? CoordinatorLayout.LayoutParams
        val behavior = params?.behavior as? AppBarLayout.Behavior ?: return@post
        val range = totalScrollRange

        val target = (-range * fraction).toInt()

        val collapse = ValueAnimator.ofInt(0, target).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addUpdateListener { animator ->
                behavior.topAndBottomOffset = animator.animatedValue as Int
            }
        }

        val expand = ValueAnimator.ofInt(target, 0).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addUpdateListener { animator ->
                behavior.topAndBottomOffset = animator.animatedValue as Int
            }
        }

        AnimatorSet().apply {
            playSequentially(collapse, expand)
            this.startDelay = startDelay
            start()
        }
    }
}