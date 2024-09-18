import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Main")

fun main() = runBlocking {
    logger.info("Запуск приложения")

    val client = KtorClient()

    val news = client.getNews()

    val startDate = LocalDate.of(2024, 9, 16)
    val endDate = LocalDate.of(2024, 9, 17)

    // Фильтруем новости по указанному периоду
    val filteredNews = getNewsWithinPeriod(client, startDate, endDate)

    // Получаем топ-5 новостей с наивысшим рейтингом
    val mostRatedNews = filteredNews.getMostRatedNews(count = 5)

    saveNewsToCsv("filtered_news.csv", mostRatedNews)
    logger.info("Новости сохранены в файл filtered_news.csv")

    val htmlContent = generateNewsHtml(news, mostRatedNews)
    logger.info("Сгенерирован HTML контент")

    val filePath = "news.html"
    saveHtmlToFile(htmlContent, filePath)
    logger.info("Контент HTML сохранен в файл news.html")

    openHtmlInBrowser(filePath)
}
