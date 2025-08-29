package com.esmanureral.artframe

fun String?.orDefault(default: String): String {
    return if (this.isNullOrBlank()) default else this
}