import models.News
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NewsPrinter {
    private val newsList = mutableListOf<News>()
    private var header: String = "Новости"
    private var dateFormat: String = "yyyy-MM-dd"
    private var showFavoritesCount: Boolean = true
    private var showCommentsCount: Boolean = true
    private var showRating: Boolean = true

    // Функции для настройки вывода
    fun header(header: String) {
        this.header = header
    }

    fun dateFormat(format: String) {
        this.dateFormat = format
    }

    fun hideFavoritesCount() {
        showFavoritesCount = false
    }

    fun hideCommentsCount() {
        showCommentsCount = false
    }

    fun hideRating() {
        showRating = false
    }

    fun news(vararg news: News) {
        newsList.addAll(news)
    }

    fun print() {
        println(header.bold().underline())
        println("-".repeat(header.length))

        newsList.forEach { news ->
            println("Title: ${news.title.italic()}")
            println("Publication Date: ${formatDate(news.publicationDate).blue()}")
            println() // Пустая строка для разделения новостей
        }
    }

    // Функции-расширения для стилизации текста
    fun String.bold(): String = "\u001B[1m$this\u001B[0m"
    fun String.italic(): String = "\u001B[3m$this\u001B[0m"
    fun String.underline(): String = "\u001B[4m$this\u001B[0m"
    fun String.blue(): String = "\u001B[34m$this\u001B[0m"
    fun String.green(): String = "\u001B[32m$this\u001B[0m"
    fun String.yellow(): String = "\u001B[33m$this\u001B[0m"
    fun String.red(): String = "\u001B[31m$this\u001B[0m"


    private fun formatDate(epochSeconds: Long): String {
        val instant = Instant.ofEpochSecond(epochSeconds)
        return instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(dateFormat))
    }
}


// Функции для красивого вывода новостей
fun printNews(configure: NewsPrinter.() -> Unit) {
    val printer = NewsPrinter().apply(configure)
    printer.print()
}

// Пример использования
fun main() {
    val news1 = News(
        id = 1,
        title = "Breaking News: Kotlin DSL",
        publicationDate = 1694928000L, // Example timestamp
        favoritesCount = 100,
        commentsCount = 50,
    )

    val news2 = News(
        id = 2,
        title = "Kotlin DSL in Action",
        publicationDate = 1695014400L, // Example timestamp
        favoritesCount = 200,
        commentsCount = 70,
    )

    printNews {
        header("Latest News Highlights".bold().underline())
        dateFormat("dd-MM-yyyy")
        news(news1, news2)
        hideCommentsCount()
        hideRating()
    }
}
