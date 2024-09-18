import kotlinx.html.*
import kotlinx.html.stream.createHTML
import models.News
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId

// Настройка логгера
private val logger = LoggerFactory.getLogger("NewsHtmlGenerator")

// Функция для форматирования даты
fun formatDate(epochSeconds: Long): String {
    return Instant.ofEpochSecond(epochSeconds)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .toString()
}

// Функция для генерации HTML
fun generateNewsHtml(freshNews: List<News>, topRatedNews: List<News>): String {
    logger.info("Генерация HTML для новостей.")

    return createHTML().html {
        head {
            title("Новости KudaGo")
        }
        body {
            h1 { +"Новости KudaGo" }

            h2 { +"Топ новости" }
            table {
                tr {
                    th { +"Заголовок" }
                    th { +"Дата публикации" }
                    th { +"Рейтинг" }
                }
                topRatedNews.forEach { news ->
                    tr {
                        td { a(href = news.siteUrl) { +news.title } }
                        td { +formatDate(news.publicationDate) }
                        td { +"${news.rating}" }
                    }
                }
            }

            h2 { +"Свежие новости" }
            div {
                freshNews.forEach { news ->
                    div("news-card") {
                        h3 { a(href = news.siteUrl) { +news.title } }
                        p { +"Дата публикации: ${formatDate(news.publicationDate)}" }
                        news.place?.let { place ->
                            div("place-info") {
                                h4 { +"Место проведения" }
                                p { +"Название: ${place.title}" }
                                p { +"Локация: ${place.location}" }
                                p { +"Адрес: ${place.address}" }
                                p { +"Телефон: ${place.phone}" }
                            }
                        } ?: p { +"" }
                        p {
                            unsafe {
                                +news.description!!
                            }
                        }
                        p {
                            +"Комментариев: ${news.commentsCount} | "
                            +"В избранном: ${news.favoritesCount}"
                        }
                    }
                    hr {}
                }
            }
        }
    }
}

// Функция для сохранения HTML в файл
fun saveHtmlToFile(htmlContent: String, filePath: String) {
    logger.info("Сохранение HTML в файл: $filePath")
    try {
        File(filePath).writeText(htmlContent, StandardCharsets.UTF_8)
        logger.info("HTML успешно сохранен в файл: $filePath")
    } catch (e: Exception) {
        logger.error("Ошибка при сохранении файла: ${e.message}", e)
    }
}

// Функция для открытия HTML в браузере
fun openHtmlInBrowser(filePath: String) {
    val file = File(filePath)
    if (file.exists()) {
        logger.info("Открытие HTML файла в браузере: $filePath")
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(file.toURI())
                logger.info("HTML файл открыт в браузере.")
            } else {
                logger.warn("Открытие браузера не поддерживается на этой системе.")
            }
        } catch (e: Exception) {
            logger.error("Ошибка при открытии файла в браузере: ${e.message}", e)
        }
    } else {
        logger.warn("Файл не найден: $filePath")
    }
}
