package com.example.myapplication_5_11

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import java.time.LocalDateTime
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication_5_11.ui.theme.MyApplication_5_11Theme
import java.time.format.DateTimeFormatter

class NMainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplication_5_11Theme {
                val login = remember{mutableStateOf("")}
                val text = remember{mutableStateOf("")}
                val context = LocalContext.current
                val filesDir = context.filesDir
                val intent2 = Intent(context, MainActivity::class.java)

                    Column(modifier = Modifier.padding(30.dp)) {

                        Text("Название заметки")
                        TextField(value = login.value, onValueChange = {newText -> login.value = newText},
                            textStyle = TextStyle(fontSize = 28.sp),
                            //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Text("текст заметки")
                        TextField(value = text.value, onValueChange = {newText -> text.value = newText})
                        Button(
                            onClick = {
                                val current = LocalDateTime.now()
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val formatted = current.format(formatter) as String
                                context.openFileOutput(formatted + ".txt", Context.MODE_PRIVATE).use{
                                    it.write(login.value.toByteArray())
                                    it.write(text.value.toByteArray())
                                }
                                context.startActivity(intent2)
                            },
                        )
                        { Text("Записать")}
                    }

            }

            }
        }
    }
