package ui

import models.News
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId

class NewsHtmlGenerator {
    // Настройка логгера
    private val logger = LoggerFactory.getLogger("ui.NewsHtmlGenerator")

    // Функция для форматирования даты
    private fun formatDate(epochSeconds: Long): String {
        return Instant.ofEpochSecond(epochSeconds)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .toString()
    }

    // Функция для генерации HTML
    fun generateNewsHtml(freshNews: List<News>, topRatedNews: List<News>): String {
        logger.info("Генерация HTML для новостей.")

        return HtmlBuilder().apply {
            html {
                head {
                    title("Новости KudaGo")
                }
                body {
                    h1 { plus("Новости KudaGo") }

                    h2 { plus("Топ новости") }
                    table {
                        tr {
                            th { plus("Заголовок") }
                            th { plus("Дата публикации") }
                            th { plus("Рейтинг") }
                        }
                        topRatedNews.forEach { news ->
                            tr {
                                td { a(href = news.siteUrl.orEmpty()) { plus(news.title) } }
                                td { plus(formatDate(news.publicationDate)) }
                                td { plus("${news.rating}") }
                            }
                        }
                    }

                    h2 { plus("Свежие новости") }
                    div {
                        freshNews.forEach { news ->
                            div {
                                h3 { a(href = news.siteUrl.orEmpty()) { plus(news.title) } }
                                p { plus("Дата публикации: ${formatDate(news.publicationDate)}") }
                                news.place?.let { place ->
                                    div {
                                        h4 { plus("Место проведения") }
                                        p { plus("Название: ${place.title}") }
                                        p { plus("Локация: ${place.location}") }
                                        p { plus("Адрес: ${place.address}") }
                                        p { plus("Телефон: ${place.phone}") }
                                    }
                                } ?: p { plus("") }
                                p {
                                    unsafe {
                                        plus(news.description.orEmpty())
                                    }
                                }
                                p {
                                    plus("Комментариев: ${news.commentsCount} | ")
                                    plus("В избранном: ${news.favoritesCount}")
                                }
                                hr()
                            }
                        }
                    }
                }
            }
        }.toHtmlString()
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
}
