import models.News
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("NewsProcessing")

// Функция для загрузки новой страницы
suspend fun loadNewsPage(client: KtorClient, page: Int): List<News> {
    logger.trace("Запрос страницы новостей: page=$page")
    return try {
        client.getNews(page = page)
    } catch (e: Exception) {
        logger.error("Ошибка при загрузке страницы новостей: page=$page, ${e.message}", e)
        emptyList()
    }
}

// Функция для фильтрации новостей по дате
fun filterNewsByDate(news: List<News>, startDate: LocalDate, endDate: LocalDate): List<News> {
    logger.debug("Фильтрация новостей по дате: startDate={}, endDate={}", startDate, endDate)
    return news.filter { newsItem ->
        try {
            val publicationDate = Instant.ofEpochSecond(newsItem.publicationDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            publicationDate in startDate..endDate
        } catch (e: Exception) {
            logger.warn("Ошибка при преобразовании даты публикации: ${newsItem.publicationDate}. ${e.message}", e)
            false
        }
    }
}

// Функция для проверки выхода за пределы периода поиска
fun isLastPageBeforeStartDate(newsPage: List<News>, startDate: LocalDate): Boolean {
    logger.debug("Проверка последней страницы перед начальной датой: startDate={}", startDate)
    return try {
        val lastDate = newsPage.lastOrNull()?.let {
            Instant.ofEpochSecond(it.publicationDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
        lastDate?.isBefore(startDate) == true
    } catch (e: Exception) {
        logger.warn("Ошибка при проверке даты последней новости. ${e.message}", e)
        false
    }
}

// Функция для сортировки новостей по рейтингу и выбора максимального количества новостей
fun List<News>.getMostRatedNews(count: Int): List<News> {
    logger.trace("Получение топ-$count новостей по рейтингу")
    return try {
        this
            .sortedByDescending { it.rating }
            .take(count)
    } catch (e: Exception) {
        logger.error("Ошибка при сортировке новостей: ${e.message}", e)
        emptyList()
    }
}
