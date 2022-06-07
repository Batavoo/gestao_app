package com.example.myapplication.entity

data class Transaction(
    val id:Int? = null,
    val name:String,
    val date:String,
    val isRevenue:Boolean,
    val userId:Int
)
