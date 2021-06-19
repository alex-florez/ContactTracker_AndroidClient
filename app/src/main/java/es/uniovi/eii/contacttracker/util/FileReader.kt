package es.uniovi.eii.contacttracker.util

import android.content.Context

fun readFile(ctx: Context, filename: String): List<String> {
    var lines = listOf<String>()
    ctx.assets.open(filename).bufferedReader().use {
        lines = it.readLines()
    }

    return lines
}