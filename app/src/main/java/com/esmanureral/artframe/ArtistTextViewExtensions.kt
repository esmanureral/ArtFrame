package com.esmanureral.artframe

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView

fun TextView.setArtistDisplay(artistTitle: String, artistDisplay: String) {
    val rest = artistDisplay.removePrefix(artistTitle).trim()

    val builder = SpannableStringBuilder()

    builder.append(artistTitle)
    builder.setSpan(
        StyleSpan(Typeface.BOLD),
        0,
        builder.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    builder.setSpan(
        AbsoluteSizeSpan(18, true),
        0,
        builder.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    builder.append("\n")

    val start = builder.length
    builder.append(rest)
    builder.setSpan(
        ForegroundColorSpan(Color.GRAY),
        start,
        builder.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    builder.setSpan(
        AbsoluteSizeSpan(14, true),
        start,
        builder.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    this.text = builder
}