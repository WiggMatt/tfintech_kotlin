import models.News
import java.io.File
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("SaveNewsToCsv")

// Функция для сохранения новостей в CSV файл
fun saveNewsToCsv(filePath: String, news: List<News>) {
    logger.info("Сохранение новостей в CSV файл: $filePath")
    try {
        FileWriter(File(filePath), StandardCharsets.UTF_8).use { writer ->
            writer.append("Идентификатор,\tНазвание,\tДата публикации,\tРейтинг\n")

            news.forEach { newsItem ->
                writer.append("${newsItem.id},${newsItem.title},${newsItem.publicationDate},${newsItem.rating}\n")
            }
        }
        logger.info("Файл $filePath успешно сохранен")
    } catch (e: Exception) {
        logger.error("Ошибка при сохранении файла: ${e.message}", e)
    }
}
