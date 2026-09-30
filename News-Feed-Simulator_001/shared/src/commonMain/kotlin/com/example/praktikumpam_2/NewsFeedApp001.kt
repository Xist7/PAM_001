package com.example.praktikumpam_2

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class NewsItem(val id: Int, val title: String, val category: String)

class NewsFeedRepository {

    fun getNewsStream(): Flow<NewsItem> = flow {
        val categories = listOf("ITERA", "Informatika", "Pengembangan Aplikasi Mobile", "Galih")
        var id = 1
        while (true) {
            delay(2000)
            val randomCategory = categories.random()
            emit(NewsItem(id, "Berita Utama Hari Ini #$id", randomCategory))
            id++
        }
    }


    suspend fun fetchNewsDetail(newsId: Int): String {
        delay(1000)
        return "Isi lengkap konten berita id $newsId..."
    }
}

class NewsViewModel(private val repository: NewsFeedRepository) {

    private val _readCount = MutableStateFlow(0)
    val readCount: StateFlow<Int> = _readCount.asStateFlow()

    fun markAsRead() {
        _readCount.value++
    }
}

fun main() = runBlocking {
    val repository = NewsFeedRepository()
    val viewModel = NewsViewModel(repository)


    val readTrackerJob = launch {
        viewModel.readCount.collect { count ->
            println(" Total Berita Dibaca: $count")
        }
    }

    println("--- Memulai News Feed Simulator ---")


    val newsJob = launch {
        repository.getNewsStream()
            .filter { news -> news.category == "Informatika" }
            .map { news -> "[NEWS Informatika] ${news.title}" }
            .catch { e -> println("Error pada stream: ${e.message}") }
            .collect { formattedNews ->
                println(" Berita Masuk: $formattedNews")


                val detail = async { repository.fetchNewsDetail(1) }
                println(" Detail Fetched: ${detail.await()}")

                viewModel.markAsRead()
            }
    }


    delay(20000)
    newsJob.cancel()
    readTrackerJob.cancel()
    println("--- Simulation Ended ---")
}