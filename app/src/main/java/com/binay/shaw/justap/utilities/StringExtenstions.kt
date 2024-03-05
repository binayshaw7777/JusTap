package com.binay.shaw.justap.utilities

fun String.compressString(): String {
    val output = StringBuilder()
    var count = 1
    var currentChar = this[0]

    for (i in 1 until this.length) {
        if (this[i] == currentChar) {
            count++
        } else {
            output.append("$currentChar$count")
            currentChar = this[i]
            count = 1
        }
    }
    output.append("$currentChar$count")
    return output.toString()
}

fun String.decompressString(): String {
    val output = StringBuilder()
    var count = 0

    for (char in this) {
        count = if (char.isDigit()) {
            count * 10 + (char - '0')
        } else {
            for (i in 1..count) {
                output.append(char)
            }
            0
        }
    }
    return output.toString()
}