package com.example.myapplication21326545

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

class CompressWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val inputFile = inputData.getString(KEY_INPUT_FILE) ?: return Result.failure()
        Log.d(TAG, "CompressWorker started for $inputFile")

        // Имитация прогресса
        for (i in 1..100) {
            if (i % 10 == 0) {
                setProgress(workDataOf(KEY_PROGRESS to i))
                delay(50) // имитация работы
            }
            yield()
        }

        // Имитация успешного завершения
        val outputFile = "compressed_$inputFile"
        val outputData = workDataOf(KEY_OUTPUT_FILE to outputFile)
        return Result.success(outputData)
    }

    companion object {
        const val KEY_INPUT_FILE = "input_file"
        const val KEY_OUTPUT_FILE = "output_file"
        const val KEY_PROGRESS = "progress"
        private const val TAG = "CompressWorker"
    }
}