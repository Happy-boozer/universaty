package com.example.myapplication89

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication89.ui.theme.MyApplication89Theme
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext


data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

data class Author(
    val id: Int,
    val name: String,
    val avatarColor: String  // имитация цвета или URL
)

data class Comment(
    val id: Int,
    val postId: Int,
    val name: String,
    val email: String,
    val body: String
)

class PostRepository(private val context: Context) {
    private suspend fun loadJsonFromAsset(fileName: String): String = withContext(Dispatchers.IO) {
        context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    suspend fun getPosts(): List<Post> {
        delay(500) // имитация сети
        val json = loadJsonFromAsset("posts.json")
        return Gson().fromJson(json, Array<Post>::class.java).toList()
    }

    suspend fun getAuthors(): List<Author> {
        delay(300)
        val json = loadJsonFromAsset("authors.json")
        return Gson().fromJson(json, Array<Author>::class.java).toList()
    }

    suspend fun getCommentsForPost(postId: Int): List<Comment> {
        delay(400) // случайная задержка
        val json = loadJsonFromAsset("comments.json")
        val allComments = Gson().fromJson(json, Array<Comment>::class.java).toList()
        return allComments.filter { it.postId == postId }
    }

    suspend fun getAuthorById(userId: Int): Author? {
        delay(400)
        val json = loadJsonFromAsset("authors.json")
        return Gson().fromJson(json, Array<Author>::class.java).toList().find { it.id == userId }
    }
}

sealed class LoadState<out T> {
    object Loading : LoadState<Nothing>()
    data class Ready<T>(val data: T) : LoadState<T>()
    data class Error(val throwable: Throwable?) : LoadState<Nothing>()
}

data class PostUiState(
    val post: Post,
    val avatarState: LoadState<Author> = LoadState.Loading,
    val commentsState: LoadState<List<Comment>> = LoadState.Loading
)


class PostsViewModel(
    private val repository: PostRepository
) : ViewModel() {
    private val _postsState = MutableStateFlow<List<PostUiState>>(emptyList())
    val postsState: StateFlow<List<PostUiState>> = _postsState.asStateFlow()

    private var currentLoadJob: Job? = null

    // Убираем автоматическую загрузку из init
    // Загружаем только по запросу

    fun loadPosts() {
        // Отменяем предыдущую загрузку
        currentLoadJob?.cancel()

        currentLoadJob = viewModelScope.launch {
            try {
                // Показываем состояние загрузки
                _postsState.value = emptyList()

                // Загружаем посты
                val posts = withContext(Dispatchers.IO) {
                    repository.getPosts().take(15)
                }

                val postsUi = posts.map { post ->
                    PostUiState(post = post)
                }
                _postsState.value = postsUi

                // Загружаем доп данные для каждого поста
                posts.forEachIndexed { index, post ->
                    launchPostDataLoad(index, post)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _postsState.value = emptyList()
            }
        }
    }

    private fun CoroutineScope.launchPostDataLoad(index: Int, post: Post) {
        launch {
            try {
                supervisorScope {
                    val avatarDeferred = async {
                        try {
                            val author = repository.getAuthorById(post.userId)
                            if (author != null) {
                                LoadState.Ready(author)
                            } else {
                                LoadState.Error(null)
                            }
                        } catch (e: Exception) {
                            LoadState.Error(e)
                        }
                    }

                    val commentsDeferred = async {
                        try {
                            val comments = repository.getCommentsForPost(post.id)
                            LoadState.Ready(comments)
                        } catch (e: Exception) {
                            LoadState.Error(e)
                        }
                    }

                    val avatarState = avatarDeferred.await()
                    val commentsState = commentsDeferred.await()

                    _postsState.update { currentList ->
                        if (index < currentList.size) {
                            currentList.toMutableList().apply {
                                set(index, currentList[index].copy(
                                    avatarState = avatarState,
                                    commentsState = commentsState
                                ))
                            }
                        } else {
                            currentList
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refresh() {
        loadPosts()
    }

    override fun onCleared() {
        currentLoadJob?.cancel()
        super.onCleared()
    }
}



class PostsViewModelFactory(private val repository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity3 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplication89Theme {


                val repository = PostRepository(applicationContext)

                val factory = PostsViewModelFactory(repository)

                val viewModel: PostsViewModel = viewModel(factory = factory)
                viewModel.PostsScreen() // Вызов extension function

            }
        }
    }
}

@Composable
fun PostsViewModel.PostsScreen() {
    val posts by postsState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column {
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            onClick = { coroutineScope.launch { refresh() } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Обновить")
        }

        LazyColumn {
            items(posts) { postUi ->
                PostCard(postUi = postUi)
            }
        }
    }
}

@Composable
fun PostCard(postUi: PostUiState) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Заголовок и тело поста
            Text(text = postUi.post.title, fontWeight = FontWeight.Bold)
            Text(text = postUi.post.body, maxLines = 3)

            Spacer(modifier = Modifier.height(8.dp))

            // Аватарка автора
            when (val avatar = postUi.avatarState) {
                is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Text("Загрузка автора...")
                }
                is LoadState.Ready -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Имитация аватарки цветом
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(android.graphics.Color.parseColor(avatar.data.avatarColor)))
                        )
                        Text(avatar.data.name, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                is LoadState.Error -> {
                    //Icon(Icons.Default.Error, contentDescription = "Ошибка загрузки автора")
                    Text("Не удалось загрузить автора")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Комментарии
            when (val comments = postUi.commentsState) {
                is LoadState.Loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text("Загрузка комментариев...")
                }
                is LoadState.Ready -> {
                    Text("Комментарии (${comments.data.size}):")
                    Column {
                        comments.data.take(2).forEach { comment ->
                            Text(text = "${comment.name}: ${comment.body.take(30)}...", fontSize = 12.sp)
                        }
                    }
                }
                is LoadState.Error -> {
                    //Icon(Icons.Default.Error, contentDescription = "Ошибка загрузки комментариев")
                    Text("Не удалось загрузить комментарии")
                }
            }
        }
    }
}



