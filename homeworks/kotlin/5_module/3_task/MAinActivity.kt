package com.example.myapplication_5_2

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myapplication_5_2.ui.theme.MyApplication_5_2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class PhotoItem(
    val id: String,
    val path: String,
    val uri: String,
    val timestamp: Long
)

class MainActivity : ComponentActivity() {

    private var currentPhotoUri: Uri? = null
    private var currentPhotoPath: String? = null
    private val photosList = ArrayList<PhotoItem>()

    // Регистрация для запроса разрешения камеры
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
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
    ) { success: Boolean ->
        if (success) {
            showToast("Фото сохранено: $currentPhotoPath")
            savePhotoToMemory(currentPhotoPath, currentPhotoUri)
        } else {
            showToast("Ошибка при сохранении фото")
            currentPhotoPath?.let { File(it).delete() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadPhotosFromMemory()

        setContent {
            MyApplication_5_2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CameraScreen(
                        modifier = Modifier.padding(innerPadding),
                        photos = photosList,
                        onOpenCamera = { openCamera() },
                        onRequestPermission = {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        onExportToGallery = { photoItem: PhotoItem ->
                            exportToGallery(photoItem)
                        },
                        onRefresh = {
                            loadPhotosFromMemory()
                        }
                    )
                }
            }
        }
    }

    private fun savePhotoToMemory(path: String?, uri: Uri?) {
        if (path != null && uri != null) {
            val photoItem = PhotoItem(
                id = UUID.randomUUID().toString(),
                path = path,
                uri = uri.toString(),
                timestamp = System.currentTimeMillis()
            )
            photosList.add(0, photoItem)
            savePhotosList()
        }
    }

    private fun savePhotosList() {
        val sharedPref = getSharedPreferences("photos", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Сохраняем количество фото
        editor.putInt("photos_count", photosList.size)

        // Сохраняем каждое фото
        photosList.forEachIndexed { index: Int, photo: PhotoItem ->
            editor.putString("photo_${index}_id", photo.id)
            editor.putString("photo_${index}_path", photo.path)
            editor.putString("photo_${index}_uri", photo.uri)
            editor.putLong("photo_${index}_timestamp", photo.timestamp)
        }

        editor.apply()
    }

    private fun loadPhotosFromMemory() {
        val sharedPref = getSharedPreferences("photos", Context.MODE_PRIVATE)
        val count = sharedPref.getInt("photos_count", 0)

        photosList.clear()

        for (i in 0 until count) {
            val id = sharedPref.getString("photo_${i}_id", "") ?: ""
            val path = sharedPref.getString("photo_${i}_path", "") ?: ""
            val uri = sharedPref.getString("photo_${i}_uri", "") ?: ""
            val timestamp = sharedPref.getLong("photo_${i}_timestamp", 0)

            if (id.isNotEmpty() && path.isNotEmpty()) {
                photosList.add(PhotoItem(id, path, uri, timestamp))
            }
        }
    }

    private fun exportToGallery(photoItem: PhotoItem) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val sourceFile = File(photoItem.path)
                    if (!sourceFile.exists()) {
                        withContext(Dispatchers.Main) {
                            showToast("Файл не найден")
                        }
                        return@withContext
                    }

                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        }
                    }

                    val resolver = this@MainActivity.contentResolver
                    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    if (uri != null) {
                        try {
                            val inputStream = FileInputStream(sourceFile)
                            val outputStream = resolver.openOutputStream(uri)

                            if (outputStream != null) {
                                val buffer = ByteArray(1024)
                                var length: Int
                                while (inputStream.read(buffer).also { length = it } > 0) {
                                    outputStream.write(buffer, 0, length)
                                }
                                outputStream.flush()
                                outputStream.close()
                            }
                            inputStream.close()

                            withContext(Dispatchers.Main) {
                                showToast("Фото экспортировано в галерею")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            withContext(Dispatchers.Main) {
                                showToast("Ошибка при копировании файла: ${e.message}")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            showToast("Ошибка при создании записи в галерее")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showToast("Ошибка: ${e.message}")
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_${timeStamp}"

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File(storageDir, "$imageFileName.jpg").apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun openCamera() {
        try {
            val photoFile = createImageFile()
            currentPhotoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            currentPhotoUri?.let { uri: Uri -> takePictureLauncher.launch(uri) }
        } catch (e: Exception) {
            showToast("Ошибка: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    photos: List<PhotoItem>,
    onOpenCamera: () -> Unit,
    onRequestPermission: () -> Unit,
    onExportToGallery: (PhotoItem) -> Unit,
    onRefresh: () -> Unit
) {
    val context = LocalContext.current
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Кнопка добавления фото
        Button(
            onClick = {
                if (hasCameraPermission) {
                    onOpenCamera()
                } else {
                    onRequestPermission()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
        ) {
            Text(if (photos.isEmpty()) "Сделать первое фото" else "Сделать новое фото")
        }

        // Сетка фото или пустой экран
        if (photos.isEmpty()) {
            EmptyScreen(onOpenCamera = {
                if (hasCameraPermission) {
                    onOpenCamera()
                } else {
                    onRequestPermission()
                }
            })
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photos) { photo: PhotoItem ->
                    PhotoGridItem(
                        photo = photo,
                        onExportClick = {
                            onExportToGallery(photo)
                            expandedItemId = null
                        },
                        expandedItemId = expandedItemId,
                        onExpandChange = { id: String ->
                            expandedItemId = if (expandedItemId == id) null else id
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGridItem(
    photo: PhotoItem,
    onExportClick: () -> Unit,
    expandedItemId: String?,
    onExpandChange: (String) -> Unit
) {
    val context = LocalContext.current
    val photoUri = try {
        Uri.parse(photo.uri)
    } catch (e: Exception) {
        Uri.fromFile(File(photo.path))
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onExpandChange(photo.id) }
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(photoUri)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = "Photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // DropDownMenu
        DropdownMenu(
            expanded = expandedItemId == photo.id,
            onDismissRequest = { onExpandChange(photo.id) },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp)
        ) {
            DropdownMenuItem(
                text = { Text("Экспорт в галерею") },
                onClick = onExportClick
            )
        }
    }
}

@Composable
fun EmptyScreen(onOpenCamera: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "У вас пока нет фото",
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = onOpenCamera,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Сделать первое фото")
        }
    }
}
