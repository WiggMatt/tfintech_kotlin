package flow

import NewsCsvWriter
import NewsService
import controller.NewsApiClient
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import models.News
import org.slf4j.LoggerFactory
import java.time.LocalDate

const val WORKER_COUNT = 4
private val logger = LoggerFactory.getLogger("Main")

@OptIn(DelicateCoroutinesApi::class)
fun main(): Unit = runBlocking {
    val workerContext = newFixedThreadPoolContext(WORKER_COUNT, "WorkerPool")
    val newsService = NewsService()
    val apiClient = NewsApiClient()

    val startDate = LocalDate.of(2024, 9, 16)
    val endDate = LocalDate.of(2024, 12, 17)

    val processorActor = newsProcessorActor("news_report.csv")

    val workerJobs = List(WORKER_COUNT) { id ->
        CoroutineScope(workerContext).startWorker(id + 1, processorActor, newsService, apiClient, startDate, endDate)
    }

    workerJobs.forEach { it.join() }
    processorActor.close()
}

// Функция для создания актора
@OptIn(ObsoleteCoroutinesApi::class)
fun CoroutineScope.newsProcessorActor(outputFilePath: String) = actor<List<News>> {
    val csvWriter = NewsCsvWriter()
    try {
        for (news: List<News> in channel) {
            logger.info("Processor обрабатывает данные...")
            csvWriter.saveNewsToCsv(outputFilePath, news)
        }
    } catch (e: Exception) {
        logger.error("Ошибка при обработке данных: ${e.message}", e)
    } finally {
        logger.info("Запись в файл завершена.")
    }
}

fun CoroutineScope.startWorker(
    id: Int,
    processorActor: SendChannel<List<News>>,
    newsService: NewsService,
    apiClient: NewsApiClient,
    startDate: LocalDate,
    endDate: LocalDate
) = launch {
    var page = id
    while (true) {
        try {
            logger.info("Worker #$id загружает страницу $page")
            val news = newsService.getNewsWithinPeriod(apiClient, page, startDate, endDate)
            if (news.isEmpty()) {
                logger.info("Worker #$id: Нет новостей на странице $page")
                break
            }
            processorActor.send(news)
            page += WORKER_COUNT
        } catch (e: Exception) {
            logger.error("Worker #$id: Ошибка при загрузке страницы $page: ${e.message}", e)
            break
        }
    }
}

