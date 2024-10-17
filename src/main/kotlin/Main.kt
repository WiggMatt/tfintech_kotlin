import controller.NewsApiClient
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import org.slf4j.LoggerFactory
import ui.NewsHtmlGenerator

private val logger = LoggerFactory.getLogger("Main")

fun main() = runBlocking {
    logger.info("Запуск приложения")

    // 1. Инициализация клиента для взаимодействия с API
    val apiClient = NewsApiClient()

    // 2. Инициализация сервисов
    val newsService = NewsService(apiClient)
    val htmlGenerator = NewsHtmlGenerator()
    val csvWriter = NewsCsvWriter()

    // 3. Установка диапазона дат для фильтрации новостей
    val startDate = LocalDate.of(2024, 9, 16)
    val endDate = LocalDate.of(2024, 10, 17)

    // 4. Получение всех новостей за указанный период
    val newsList = newsService.getNewsWithinPeriod(apiClient, startDate, endDate)


    // 5. Отбор топ новостей по рейтингу (например, топ 5)
    val topNews = newsService.getMostRatedNews(newsList, 5)

    // 6. Генерация HTML-отчета
    val htmlContent = htmlGenerator.generateNewsHtml(freshNews = newsList, topRatedNews = topNews)
    val htmlFilePath = "news_report.html"
    htmlGenerator.saveHtmlToFile(htmlContent, htmlFilePath)

    // 7. Открытие HTML в браузере
    htmlGenerator.openHtmlInBrowser(htmlFilePath)
    logger.info("Отчет открыт в браузере: $htmlFilePath")

    // 8. Сохранение новостей в CSV
    val csvFilePath = "news_report.csv"
    csvWriter.saveNewsToCsv(csvFilePath, newsList)
    logger.info("Новости сохранены в CSV: $csvFilePath")
}
