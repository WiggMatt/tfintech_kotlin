import controller.NewsApiClient
import models.News
import java.time.LocalDate
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.ZoneId

class NewsService {
    private val logger = LoggerFactory.getLogger(NewsService::class.java)

    // Функция получения списка новостей по указанному периоду
    suspend fun getNewsWithinPeriod(client: NewsApiClient, page: Int, startDate: LocalDate, endDate: LocalDate): List<News> {
        logger.info("Получение новостей по периоду: startDate=$startDate, endDate=$endDate")
        val allNews = mutableListOf<News>()
        var continueLoading = true

        while (continueLoading) {
            val newsPage = loadNewsPage(client, page = page)

            if (newsPage.isEmpty()) {
                logger.warn("Нет новостей на странице $page")
                break
            }

            val filteredPage = filterNewsByDate(newsPage, startDate, endDate)
            allNews.addAll(filteredPage)

            if (isLastPageBeforeStartDate(newsPage, startDate)) {
                continueLoading = false
                logger.info("Завершение загрузки: последняя страница перед начальной датой")
            }
        }

        logger.info("Получено ${allNews.size} новостей по указанному периоду")
        return allNews
    }


    // Функция для загрузки новой страницы
    private suspend fun loadNewsPage(client: NewsApiClient, page: Int): List<News> {
        logger.trace("Запрос страницы новостей: page=$page")
        return try {
            client.getNews(page = page).results
        } catch (e: Exception) {
            logger.error("Ошибка при загрузке страницы новостей: page=$page, ${e.message}", e)
            throw e
        }
    }

    // Функция для фильтрации новостей по дате
    private fun filterNewsByDate(news: List<News>, startDate: LocalDate, endDate: LocalDate): List<News> {
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
    private fun isLastPageBeforeStartDate(newsPage: List<News>, startDate: LocalDate): Boolean {
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
    fun getMostRatedNews(news: List<News>, count: Int): List<News> {
        logger.trace("Получение топ-$count новостей по рейтингу")
        return try {
            news
                .sortedByDescending { it.rating }
                .take(count)
        } catch (e: Exception) {
            logger.error("Ошибка при сортировке новостей: ${e.message}", e)
            emptyList()
        }
    }
}
