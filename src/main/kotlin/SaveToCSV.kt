import models.News
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("SaveNewsToCsv")

// Функция для сохранения новостей в CSV файл с использованием Apache Commons CSV
fun saveNewsToCsv(filePath: String, news: List<News>) {
    logger.info("Сохранение новостей в CSV файл: $filePath")
    try {
        FileWriter(filePath, StandardCharsets.UTF_8).use { fileWriter ->
            val csvFormat = CSVFormat.Builder.create()
                .setHeader("Идентификатор", "Название", "Дата публикации", "Рейтинг")
                .setSkipHeaderRecord(false)
                .build()

            CSVPrinter(fileWriter, csvFormat).use { csvPrinter ->
                news.forEach { newsItem ->
                    csvPrinter.printRecord(
                        newsItem.id,
                        newsItem.title,
                        newsItem.publicationDate,
                        newsItem.rating
                    )
                }
            }
        }
        logger.info("Файл $filePath успешно сохранен")
    } catch (e: Exception) {
        logger.error("Ошибка при сохранении файла: ${e.message}", e)
    }
}
