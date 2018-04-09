package com.mqxin.androidcompare.core

class StringDescription(var content: String) {
    var begin: Int = 0
    var end: Int = 0

    init {
        begin = 0
        end = this.content.length - 1
    }

}
