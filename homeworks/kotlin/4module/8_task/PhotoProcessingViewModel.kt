package com.example.myapplication21326545

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoProcessingViewModel(application: Application) :
    AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    companion object {
        private const val UNIQUE_WORK_NAME = "photo_processing"
        private const val TAG_COMPRESS = "CompressWorker"
        private const val TAG_WATERMARK = "WatermarkWorker"
        private const val TAG_UPLOAD = "UploadWorker"
    }

    private val _uiState = MutableStateFlow(ProcessingState())
    val uiState: StateFlow<ProcessingState> = _uiState

    fun startProcessing() {

        _uiState.value = ProcessingState(isProcessing = true)

        val inputData = workDataOf(
            CompressWorker.KEY_INPUT_FILE to "photo.jpg"
        )

        val compressWork = OneTimeWorkRequestBuilder<CompressWorker>()
            .setInputData(inputData)
            .addTag(TAG_COMPRESS)
            .build()

        val watermarkWork = OneTimeWorkRequestBuilder<WatermarkWorker>()
            .addTag(TAG_WATERMARK)
            .build()

        val uploadWork = OneTimeWorkRequestBuilder<UploadWorker>()
            .addTag(TAG_UPLOAD)
            .build()

        workManager.beginUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            compressWork
        )
            .then(watermarkWork)
            .then(uploadWork)
            .enqueue()

        observeWork()
    }

    private fun observeWork() {
        workManager
            .getWorkInfosForUniqueWorkLiveData(UNIQUE_WORK_NAME)
            .observeForever { workInfos ->
                processWorkInfos(workInfos)
            }
    }

    private fun processWorkInfos(workInfos: List<WorkInfo>) {

        val failedWork = workInfos.firstOrNull { it.state == WorkInfo.State.FAILED }
        if (failedWork != null) {
            _uiState.value = ProcessingState(
                isProcessing = false,
                errorMessage = failedWork.outputData
                    .getString(UploadWorker.KEY_RESULT)
                    ?: "Ошибка обработки"
            )
            return
        }

        val runningWork = workInfos.firstOrNull { it.state == WorkInfo.State.RUNNING }

        val stepText = when {
            runningWork?.tags?.contains(TAG_COMPRESS) == true ->
                "Сжимаем фото..."

            runningWork?.tags?.contains(TAG_WATERMARK) == true ->
                "Добавляем водяной знак..."

            runningWork?.tags?.contains(TAG_UPLOAD) == true ->
                "Загружаем..."

            workInfos.all { it.state == WorkInfo.State.SUCCEEDED } ->
                "Готово!"

            else -> ""
        }

        val progress = runningWork?.progress
            ?.getInt(CompressWorker.KEY_PROGRESS, 0) ?: 0

        val isProcessing = workInfos.any {
            it.state == WorkInfo.State.RUNNING ||
                    it.state == WorkInfo.State.ENQUEUED
        }

        val isCompleted = workInfos.all {
            it.state == WorkInfo.State.SUCCEEDED
        }

        val resultMessage = if (isCompleted) {
            workInfos.firstOrNull {
                it.tags.contains(TAG_UPLOAD)
            }?.outputData?.getString(UploadWorker.KEY_RESULT)
                ?: "Готово! Фото загружено"
        } else null

        _uiState.value = ProcessingState(
            isProcessing = isProcessing,
            stepText = stepText,
            progress = progress,
            resultMessage = resultMessage
        )
    }

    fun cancelProcessing() {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
        _uiState.value = ProcessingState()
    }

    data class ProcessingState(
        val isProcessing: Boolean = false,
        val stepText: String = "",
        val progress: Int = 0,
        val resultMessage: String? = null,
        val errorMessage: String? = null
    )
}