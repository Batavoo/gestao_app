package com.example.myapplication.listener

import android.view.View

interface TransactionListener {
    fun setOnItemClickListener(view: View,position:Int)

    fun setOnItemLongClickListener(view: View,position:Int)
}