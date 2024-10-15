import models.News
import java.time.LocalDate
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("NewsWithinPeriod")

// Функция получения списка новостей по указанному периоду
suspend fun getNewsWithinPeriod(client: KtorClient, startDate: LocalDate, endDate: LocalDate): List<News> {
    logger.info("Получение новостей по периоду: startDate=$startDate, endDate=$endDate")
    val allNews = mutableListOf<News>()
    var page = 1
    var continueLoading = true

    while (continueLoading) {
        val newsPage = try {
            loadNewsPage(client, page = page)
        } catch (e: Exception) {
            logger.error("Ошибка при загрузке страницы новостей: page=$page, ${e.message}", e)
            emptyList()
        }

        if (newsPage.isEmpty()) {
            logger.warn("Нет новостей на странице $page")
            break
        }

        val filteredPage = filterNewsByDate(newsPage, startDate, endDate)
        allNews.addAll(filteredPage)

        if (isLastPageBeforeStartDate(newsPage, startDate)) {
            continueLoading = false
            logger.info("Завершение загрузки: последняя страница перед начальной датой")
        } else {
            page++
        }
    }

    logger.info("Получено ${allNews.size} новостей по указанному периоду")
    return allNews
}
