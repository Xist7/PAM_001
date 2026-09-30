package com.example.profilgalih

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform