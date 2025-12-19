package com.example.wocq_kart

data class User(var username : String,
                var rollNo : String,
                var password : String,
                var role : String = "student"
)
