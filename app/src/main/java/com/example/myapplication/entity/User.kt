package com.example.myapplication.entity

data class User(
    val id:String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val transactions: HashMap<String, Transaction> = hashMapOf()
)
