import models.News
import java.io.File
import java.io.FileWriter
import java.nio.charset.StandardCharsets

// Функция для сохранения новостей в CSV файл
fun saveNewsToCsv(filePath: String, news: List<News>) {
    FileWriter(File(filePath), StandardCharsets.UTF_8).use { writer ->
        writer.append("Идентификатор,\tНазвание,\tДата публикации,\tРейтинг\n")

        news.forEach { newsItem ->
            writer.append("${newsItem.id},${newsItem.title},${newsItem.publicationDate},${newsItem.rating}\n")
        }
    }
}