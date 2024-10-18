package flow

import NewsCsvWriter
import NewsService
import controller.NewsApiClient
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import models.News
import org.slf4j.LoggerFactory
import java.time.LocalDate

const val WORKER_COUNT = 4
private val logger = LoggerFactory.getLogger("Main")

@OptIn(DelicateCoroutinesApi::class)
fun main() = runBlocking {
    val workerContext = newFixedThreadPoolContext(WORKER_COUNT, "WorkerPool")
    val newsService = NewsService()
    val apiClient = NewsApiClient()

    val startDate = LocalDate.of(2024, 9, 16)
    val endDate = LocalDate.of(2024, 12, 17)

    val channel = Channel<List<News>>(Channel.UNLIMITED)

    val processorJob = CoroutineScope(workerContext).startProcessor(channel, "news_report.csv")
    val workerJobs = List(WORKER_COUNT) { id ->
        CoroutineScope(workerContext).startWorker(id + 1, channel, newsService, apiClient, startDate, endDate)
    }

    workerJobs.forEach { it.join() }
    channel.close()
    processorJob.join()
}

fun CoroutineScope.startWorker(
    id: Int,
    channel: Channel<List<News>>,
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
            channel.send(news)
            page += WORKER_COUNT
        } catch (e: Exception) {
            logger.error("Worker #$id: Ошибка при загрузке страницы $page: ${e.message}", e)
            break
        }
    }
}

fun CoroutineScope.startProcessor(channel: Channel<List<News>>, outputFilePath: String) = launch {
    val csvWriter = NewsCsvWriter()
    try {
        for (news in channel) {
            logger.info("Processor обрабатывает данные...")
            csvWriter.saveNewsToCsv(outputFilePath, news)
        }
    } catch (e: Exception) {
        logger.error("Ошибка при обработке данных: ${e.message}", e)
    } finally {
        logger.info("Запись в файл завершена.")
    }
}
