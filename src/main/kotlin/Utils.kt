import models.News
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// Функция для загрузки новой страницы
suspend fun loadNewsPage(client: KtorClient, page: Int): List<News> {
    return client.getNews(page = page)
}

// Функция для фильтрации новостей по дате
fun filterNewsByDate(news: List<News>, startDate: LocalDate, endDate: LocalDate): List<News> {
    return news.filter { newsItem ->
        val publicationDate = Instant.ofEpochSecond(newsItem.publicationDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        publicationDate in startDate..endDate
    }
}

// Функция для проверки выхода за пределы периода поиска
fun isLastPageBeforeStartDate(newsPage: List<News>, startDate: LocalDate): Boolean {
    val lastDate = newsPage.lastOrNull()?.let {
        Instant.ofEpochSecond(it.publicationDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
    return lastDate?.isBefore(startDate) == true
}

// Функция для сортировки новостей по рейтингу и выбора максимального количества новостей
fun List<News>.getMostRatedNews(count: Int): List<News> {
    return this
        .sortedByDescending { it.rating }
        .take(count)
}
