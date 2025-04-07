package site.neotrend

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val boldRegex: Regex = Regex("(?<!\\*)\\*\\*(?!\\*).*?(?<!\\*)\\*\\*(?!\\*)")

@Composable
fun AnnotatedText(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    var results: MatchResult? = boldRegex.find(text)
    val boldIndexes = mutableListOf<Pair<Int, Int>>()
    val keywords = mutableListOf<String>()
    var finalText: String = text // replace with StringBuilder
    while (results != null) {
        keywords.add(results.value)
        results = results.next()
    }
    keywords.forEach { keyword ->
        val index: Int = finalText.indexOf(keyword)
        val match: String = keyword.removeSurrounding("**")
        finalText = finalText.replace(keyword, match)
        boldIndexes.add(Pair(index, index + match.length))
    }
    val annotatedString: AnnotatedString = buildAnnotatedString {
        append(finalText)
        boldIndexes.forEach {
            addStyle(
                style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = fontSize.times(0.95)),
                start = it.first,
                end = it.second
            )
        }
    }
    Text(modifier = modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp), fontSize = fontSize, text = annotatedString, textAlign = TextAlign.Center)
}