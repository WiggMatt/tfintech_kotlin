package controller

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import models.NewsResponse
import org.slf4j.LoggerFactory

class NewsApiClient {
    private val logger = LoggerFactory.getLogger(NewsApiClient::class.java)

    private val client: HttpClient
        get() = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

    // Оригинальная функция для запроса новостей с одной страницы
    suspend fun getNews(count: Int = 100, page: Int? = 1): NewsResponse {
        logger.debug("Запрос новостей: count=$count, page=$page")

        return client.use {
            try {
                val response: HttpResponse = it.get("https://kudago.com/public-api/v1.4/news/") {
                    parameter("fields", "id,title,place,description,publication_date,favorites_count,comments_count,site_url")
                    parameter("location", "kzn")
                    parameter("actual_only", true)
                    parameter("page", page)
                    parameter("page_size", count)
                    parameter("expand", "place")
                }

                response.body<NewsResponse>().also {
                    logger.debug("Получено ${it.results.size} новостей на странице $page")
                }

            } catch (e: ClientRequestException) {
                logger.error("Ошибка при запросе новостей: ${e.message}. Код состояния: ${e.response.status.value}", e)
                throw e
            } catch (e: ServerResponseException) {
                logger.error("Ошибка сервера при запросе новостей: ${e.message}. Код состояния: ${e.response.status.value}", e)
                throw e
            } catch (e: JsonConvertException){
                logger.error("Ошибка при парсинге ответа: ${e.message}", e)
                throw e
            }catch (e: Exception) {
                logger.error("Неизвестная ошибка при запросе новостей: ${e.message}", e)
                throw e
            }
        }
    }
}
