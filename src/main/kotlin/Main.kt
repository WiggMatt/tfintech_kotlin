import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Main")

fun main() = runBlocking {
    logger.info("Запуск приложения")

    val client = KtorClient()

    println("\n-----------------100 СВЕЖИХ НОВОСТЕЙ-------------------\n")

    val news = client.getNews()
    news.forEach { println(it) }

    println("\n-----------------ТОП НОВОСТИ-------------------\n")

    // Примерные даты для поиска новостей
    val startDate = LocalDate.of(2024, 9, 16)
    val endDate = LocalDate.of(2024, 9, 17)

    // Фильтруем новости по указанному периоду
    val filteredNews = getNewsWithinPeriod(client, startDate, endDate)

    // Получаем топ-5 новостей с наивысшим рейтингом
    val mostRatedNews = filteredNews.getMostRatedNews(count = 5)

    // Выводим результаты
    mostRatedNews.forEach {
        println(
            "Название: ${it.title}, " +
                    "Рейтинг: ${it.rating}, " +
                    "Число избранных: ${it.favoritesCount}, " +
                    "Число комментариев: ${it.commentsCount}"
        )
    }

    saveNewsToCsv("filtered_news.csv", mostRatedNews)
    logger.info("Новости сохранены в файл filtered_news.csv")
}
