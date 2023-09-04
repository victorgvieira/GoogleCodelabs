package com.ggvieira.basicscodelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ggvieira.basicscodelab.ui.theme.BasicsCodelabTheme

// TO_KNOWN: Activity remains the entry point to an Android app.
// MainActivity is launched when the user opens the app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TO_KNOWN:  Use setContent to define your layout, but instead of using an XML file
        // you call Composable functions within it.
        setContent {
            BasicsCodelabTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun MyApp(
    modifier: Modifier = Modifier,
    names: List<String> = listOf("Victor", "Compose")
) {
    // each row takes the minimum space it can and the preview does the same thing
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        for (name in names) {
            Greeting(name = name)
        }
    }
}

@Composable
fun Greeting(name: String) {
    // A surface container using the 'background' color from the theme
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        // TO_KNOWN: Modifiers an optional modifier parameter that tell a UI element
        // how to lay out, display, or behave within its parent layout.
        // ex for padding: Modifier.padding().
//        Modifiers can have overloads so, for example, you can specify different ways to create a padding.
//        To add multiple modifiers to an element, you simply chain them.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
//         TO_KNOWN: The weight modifier makes the element fill all available space,
//         making it flexible, pushing away the other elements that don't have a weight
//         ,which are called inflexible. It also makes the fillMaxWidth modifier redundant.
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Hello,")
                Text(text = "$name!")
            }
            ElevatedButton(onClick = { /*TODO*/ }) {
                Text(text = "Show more")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BasicsCodelabTheme {
        Greeting("Victor")
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    BasicsCodelabTheme {
        MyApp()
    }
}