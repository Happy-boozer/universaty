package com.example.myapplication7777



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReminderViewModel : ViewModel() {

    private val _reminderState = MutableLiveData<Boolean>(false)
    val reminderState: LiveData<Boolean> = _reminderState

    private val _nextReminderTime = MutableLiveData<Long>(0L)
    val nextReminderTime: LiveData<Long> = _nextReminderTime

    fun setReminderState(enabled: Boolean) {
        _reminderState.value = enabled
    }

    fun setNextReminderTime(timeInMillis: Long) {
        _nextReminderTime.value = timeInMillis
    }
}