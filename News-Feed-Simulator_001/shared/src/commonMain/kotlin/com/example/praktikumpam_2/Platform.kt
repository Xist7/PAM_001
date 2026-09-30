package com.example.praktikumpam_2

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform