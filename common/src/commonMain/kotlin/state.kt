package site.neotrend

import androidx.compose.ui.graphics.ImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.toByteArray
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import site.neotrend.platform.bitmap
import site.neotrend.platform.cacheBytes
import kotlin.random.Random

class AppState(val author: Author, val avatarBitmap: ImageBitmap)

@Serializable
data class Author(
    val name: String,
    val createdDate: String,
    val fileName: String,
    val statistics: AuthorStatistics,
    val authorDto: AuthorDto,
    val rating: Float = Random.nextFloat() * 5,
)

@Serializable
data class AuthorStatistics(
    val viewsCount: Int,
    val commentsCount: Int,
    val repostsCount: Int,
    val savesCount: Int,
)

@Serializable
data class AuthorDto(
    val id: Int,
    val name: String,
)

fun EmptyAppState(): AppState = AppState(emptyAuthor, emptyAvatarBitmap)
val AppState.isEmpty: Boolean get() = author === emptyAuthor || avatarBitmap === emptyAvatarBitmap
private val emptyAuthor: Author = Author("", "", "", AuthorStatistics(0, 0, 0, 0), AuthorDto(0, ""))
private val emptyAvatarBitmap: ImageBitmap = ImageBitmap(0, 0)

fun readAppState(): AppState {
    val author: Author = readAuthor()
    val avatarBitmap: ImageBitmap = readAvatar(author.authorDto.id)
    cacheBytes(author.fileName) { readVideo(author.fileName) }
    println(author)
    return AppState(author, avatarBitmap)
}

/*api*/

private const val API_URL: String = "https://neotrend.site:8082/api/test-task"

private fun readAuthor(): Author = runBlocking {
    return@runBlocking rest.get { url("$API_URL/review") }.body()
}

private fun readAvatar(id: Int): ImageBitmap = runBlocking {
    return@runBlocking media.get { url("$API_URL/authors/$id/avatar") }.bodyAsChannel().toByteArray().bitmap()
}

private fun readVideo(fileName: String): ByteArray = runBlocking {
    return@runBlocking media.get { url("$API_URL/video?fileName=${fileName}") }.bodyAsChannel().toByteArray()
}

private val rest: HttpClient = HttpClient().config {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

private val media: HttpClient = HttpClient()
