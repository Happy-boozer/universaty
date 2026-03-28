package com.example.myapplication_5_2

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myapplication_5_2.ui.theme.MyApplication_5_2Theme
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private var currentPhotoUri: Uri? = null
    private var currentPhotoPath: String? = null

    // Регистрация для запроса разрешения камеры
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showToast("Разрешение получено")
            openCamera()
        } else {
            showToast("Разрешение не получено")
        }
    }

    // Регистрация для открытия камеры
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            showToast("Фото сохранено: $currentPhotoPath")
        } else {
            showToast("Ошибка при сохранении фото")
            // Удаляем временный файл при ошибке
            currentPhotoPath?.let { File(it).delete() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplication_5_2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CameraScreen(
                        modifier = Modifier.padding(innerPadding),
                        onOpenCamera = { openCamera() },
                        onRequestPermission = {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        currentPhotoUri = currentPhotoUri,
                        currentPhotoPath = currentPhotoPath
                    )
                }
            }
        }
    }

    // Создание файла для фото с использованием FileProvider
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_${timeStamp}"

        // Используем внешнее хранилище для фотографий
        val storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)

        return File(storageDir, "$imageFileName.jpg").apply {
            currentPhotoPath = absolutePath
        }
    }

    // Открытие камеры
    private fun openCamera() {
        try {
            val photoFile = createImageFile()
            currentPhotoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            currentPhotoUri?.let { takePictureLauncher.launch(it) }
        } catch (e: Exception) {
            showToast("Ошибка: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
    }
}

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onOpenCamera: () -> Unit,
    onRequestPermission: () -> Unit,
    currentPhotoUri: Uri?,
    currentPhotoPath: String?
) {
    val context = LocalContext.current
    var showPreview by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imagePath by remember { mutableStateOf<String?>(null) }

    // Проверка разрешения камеры
    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Кнопка открытия камеры
        Button(
            onClick = {
                if (hasCameraPermission) {
                    onOpenCamera()
                    // Сохраняем URI для превью
                    imageUri = currentPhotoUri
                    imagePath = currentPhotoPath
                    showPreview = true
                } else {
                    onRequestPermission()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Открыть камеру")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Информация о последнем фото
        if (imagePath != null) {
            Text("Последнее фото сохранено по пути:")
            Text(
                text = imagePath ?: "",
                modifier = Modifier.padding(8.dp),
                //fontSize = androidx.compose.ui.unit.sp(10.sp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Показываем превью фото
        if (showPreview && imageUri != null) {
            Text("Превью фото:")
            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(imageUri)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Preview",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        } else if (showPreview && imageUri == null) {
            Text("Фото еще не сделано")
        }
    }
}
