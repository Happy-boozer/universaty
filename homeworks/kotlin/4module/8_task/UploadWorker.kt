package com.example.myapplication21326545

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

class UploadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val inputFile = inputData.getString(WatermarkWorker.KEY_OUTPUT_FILE) ?: return Result.failure()
        Log.d(TAG, "UploadWorker started for $inputFile")

        for (i in 1..100) {
            if (i % 10 == 0) {
                setProgress(workDataOf(CompressWorker.KEY_PROGRESS to i))
                delay(30)
            }
            yield()
        }

        // Имитация успешной загрузки
        val resultMessage = "Готово! Фото загружено: $inputFile"
        val outputData = workDataOf(KEY_RESULT to resultMessage)
        return Result.success(outputData)
    }

    companion object {
        const val KEY_RESULT = "result"
    }
}