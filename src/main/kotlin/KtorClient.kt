import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import models.News
import models.NewsResponse

class KtorClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    // Функция для получения новостей из API KudaGo
    suspend fun getNews(count: Int = 100): List<News> {
        return try {
            val response: HttpResponse = client.get("https://kudago.com/public-api/v1.4/news/") {
                parameter("fields", "id,title,place,description,publication_date,favorites_count,comments_count,site_url")
                parameter("location", "kzn")
                parameter("actual_only", true)
                parameter("page_size", count)
                parameter("expand", "place")
            }

            val newsResponse: NewsResponse = response.body()
            newsResponse.results
        } catch (e: Exception) {
            println("Ошибка при запросе новостей: ${e.message}")
            emptyList()
        } finally {
            client.close()
        }
    }
}

fun main() = runBlocking {
    val client = KtorClient()
    val news = client.getNews()
    news.forEach { println(it) }
}





