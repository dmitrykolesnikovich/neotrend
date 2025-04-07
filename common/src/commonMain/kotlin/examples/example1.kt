package site.neotrend.examples

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import site.neotrend.Navigation

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun example1(navigation: Navigation) {
    println("example1")
    val sheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetContent = {
            IconButton(onClick = {
                coroutineScope.launch {
                    sheetState.hide()
                }
                navigation.navigate("App")
            }) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
            LazyColumn {
                items(50) {
                    ListItem(
                        text = {
                            Text("Item $it")
                        },
                        icon = {
                            Icon(Icons.Default.Favorite, contentDescription = "Localized description")
                        }
                    )
                }
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Rest of the UI")
            Spacer(Modifier.height(20.dp))
            Button(onClick = {
                coroutineScope.launch {
                    sheetState.show()
                }
            }) {
                Text("Click to show sheet")
            }
        }
    }
}
