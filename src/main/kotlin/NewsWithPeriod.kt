import models.News
import java.time.LocalDate

// Функция получения списка новостей по указанному периоду
suspend fun getNewsWithinPeriod(client: KtorClient, startDate: LocalDate, endDate: LocalDate): List<News> {
    val allNews = mutableListOf<News>()
    var page = 1
    var continueLoading = true

    while (continueLoading) {
        val newsPage = loadNewsPage(client, page = page)
        if (newsPage.isEmpty()) break

        val filteredPage = filterNewsByDate(newsPage, startDate, endDate)
        allNews.addAll(filteredPage)

        if (isLastPageBeforeStartDate(newsPage, startDate)) {
            continueLoading = false
        } else {
            page++
        }
    }

    return allNews
}