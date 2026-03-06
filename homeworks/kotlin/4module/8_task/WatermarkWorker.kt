package com.example.myapplication21326545

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

class WatermarkWorker (context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params){
    override suspend fun doWork(): Result {
        val inputFile = inputData.getString(CompressWorker.KEY_OUTPUT_FILE) ?: return Result.failure()
        Log.d(TAG, "WatermarkWorker started for $inputFile")

        for (i in 1..100) {
            if (i % 10 == 0) {
                setProgress(workDataOf(CompressWorker.KEY_PROGRESS to i))
                delay(40) // разная скорость для имитации
            }
            yield()
        }

        val outputFile = "watermarked_$inputFile"
        val outputData = workDataOf(KEY_OUTPUT_FILE to outputFile)
        return Result.success(outputData)
    }

    companion object {
        const val KEY_OUTPUT_FILE = "output_file"
    }
}