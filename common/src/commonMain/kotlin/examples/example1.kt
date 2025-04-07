package site.neotrend.examples

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*

@Composable
fun example1() {
    MaterialTheme {
        var location: String by remember { mutableStateOf("Europe/Paris") }
        var timeAtLocation: String by remember { mutableStateOf("No location selected") }
        Column {
            Text(timeAtLocation)
            TextField(value = location, onValueChange = { location = it })
            Button(onClick = { timeAtLocation = "13:30" }) {
                Text("Show Time At Location")
            }
        }
    }
}