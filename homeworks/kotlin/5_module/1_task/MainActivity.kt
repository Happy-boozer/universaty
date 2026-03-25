package com.example.myapplication_5_11

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication_5_11.ui.theme.MyApplication_5_11Theme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {


            val context = LocalContext.current
            val intent2 = Intent(context, NMainActivity2::class.java)
            val filesDir = context.filesDir
            val files = filesDir.listFiles()
            //context.startActivity(intent2)
            val items = mutableStateListOf<String>()
            files?.forEach { file ->
                if (file.isFile) {
                    // Читаем содержимое файла
                    val content = file.readText()
                    items.add(content)
                    //println("Файл: ${file.name}, Содержимое: $content")
                }
            }
            MyApplication_5_11Theme {
                Column() {
                Spacer(modifier = Modifier.height(50.dp))
                Button(
                    onClick = {
                        context.startActivity(intent2)
                    },
                )
                {Text("Зарегистрироваться")}
                }
                Row(modifier = Modifier.padding(100.dp)){
                   //Spacer(modifier = Modifier.padding(200.dp))
                LazyColumn() {
                    items(items) { item ->
                        Text(text = item, modifier = Modifier.padding(8.dp))
                    }
                }
                }
        }
    }
    }
}
