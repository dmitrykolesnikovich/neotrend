package site.neotrend.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun App(navigation: Navigation) {
    println("App")
    val author: Author = readAuthor()
    val avatar: ByteArray = readAvatar(author.authorDto.id)
    println(author)
    println("avatar: $avatar ${avatar.size}")

    var text by remember { mutableStateOf("Hello, World!") }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {
            VideoPlayer(modifier = Modifier.fillMaxWidth().height(400.dp), author.fileName)
            Button(onClick = {
                text = "Hello!"
                navigation.navigate("example1")
            }) { Text(text) }
            Card(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable { println("CardView Clicked....") }, elevation = 6.dp, shape = RoundedCornerShape(8.dp), backgroundColor = Color(0xFF00BFA5),) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Image(
                        bitmap = avatar.toComposeImageBitmap(),
                        contentDescription = "Card Image",
                        modifier = Modifier.width(29.dp).height(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(8.dp)),)
                    Image(
                        bitmap = drawableToImageBitmap("arrows.svg"),
                        contentDescription = "Card Image",
                        modifier = Modifier.width(29.dp).height(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Title Text", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Sample description text.", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
expect fun VideoPlayer(modifier: Modifier, fileName: String)

expect fun drawableToImageBitmap(drawable: String): ImageBitmap

expect fun ByteArray.toComposeImageBitmap(): ImageBitmap
