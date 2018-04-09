package com.mqxin.androidcompare.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mqxin.androidcompare.R
import java.util.*


/**
 * Description :
 * Copyright   : Copyright (c) 2017
 * Company     : Raisecom
 * Author      : yxl
 * Date        : 2018-03-16 9:10
 */
class TextAdapter() : RecyclerView.Adapter<TextAdapter.TextHolder>() {
    private lateinit var context: Context
    private var data: List<SpannableStringBuilder> = ArrayList()
    var text: SpannableStringBuilder = SpannableStringBuilder.valueOf("")
        set(value) {
            field = value
            val list: ArrayList<SpannableStringBuilder> = ArrayList()
            val locations = getNewLineLocations(value)
            while (!locations.isEmpty()) {
                val end = locations.pop()
                val start = if (locations.isEmpty()) 0 else locations.peek()
                list.add(0, value.subSequence(if (start == 0) {
                    0
                } else {
                    start + 1
                }, end) as SpannableStringBuilder)
            }
            data = list
            notifyDataSetChanged()
        }

    constructor(context: Context) : this() {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TextHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_textview, parent, false)
        return TextHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TextHolder?, position: Int) {
        holder?.textView?.text = data[position]
    }

    private fun getNewLineLocations(unsegmented: SpannableStringBuilder): Stack<Int> {
        val loc: Stack<Int> = Stack()
        val string = unsegmented.toString()
        var next = string.indexOf('\n')
        while (next > 0) {
            //avoid chains of newline characters
            next = if (string[next - 1] != '\n') {
                loc.push(next)
                string.indexOf('\n', loc.peek() + 1)
            } else {
                string.indexOf('\n', next + 1)
            }
            if (next >= string.length) next = -1
        }
        return loc
    }

    inner class TextHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView? = itemView?.findViewById(R.id.textview)
    }
}