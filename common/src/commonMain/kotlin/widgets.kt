package site.neotrend

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val boldRegex: Regex = Regex("(?<!\\*)\\*\\*(?!\\*).*?(?<!\\*)\\*\\*(?!\\*)")
private val italicRegex: Regex = Regex("\\*(?![*\\s])(?:[^*]*[^*\\s])?\\*")

@Composable
fun AnnotatedText(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    var results: MatchResult? = boldRegex.find(text)
    val boldIndexes: ArrayList<Pair<Int, Int>> = ArrayList()
    val keywords: ArrayList<String> = ArrayList()
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
    Text(
        modifier = modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        fontSize = fontSize,
        text = annotatedString,
        textAlign = TextAlign.Center
    )
}

@Composable
fun AnnotatedTextField(text: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    var results: MatchResult? = italicRegex.find(text)
    val keywords: ArrayList<String> = ArrayList()
    while (results != null) {
        keywords.add(results.value)
        results = results.next()
    }

    var result: String = text // replace with StringBuilder
    val ranges: ArrayList<IntRange> = ArrayList()
    val matches: ArrayList<String> = ArrayList()
    keywords.forEach { keyword ->
        val index: Int = result.indexOf(keyword)
        val match: String = keyword.removeSurrounding("*")
        result = result.replace(keyword, match)
        ranges.add(index..index + match.length)
        matches.add(match)
    }

    class ColorsTransformation : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val annotatedString: AnnotatedString = buildAnnotatedString {
                append(result)
                ranges.forEach {
                    addStyle(
                        style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.Gray, fontSize = fontSize.times(0.95)),
                        start = it.first,
                        end = it.last
                    )
                }
            }
            return TransformedText(annotatedString, OffsetMapping.Identity)
        }
    }
    TextField(
        value = result,
        onValueChange = { onValueChange(it) },
        modifier = modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        visualTransformation = ColorsTransformation()
    )
}

// https://github.com/creativedrewy/KRATE/blob/main/extension/src/commonMain/kotlin/com/solanamobile/krate/extension/ui/CommonComposables.kt#L35
@Composable
fun Modifier.keyboardBottomPadding(): Modifier {
    return padding(bottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding())
}
