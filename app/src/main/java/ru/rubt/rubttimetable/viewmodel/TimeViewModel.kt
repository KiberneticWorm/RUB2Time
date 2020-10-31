package ru.rubt.rubttimetable.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import ru.rubt.rubttimetable.db.Lesson

class TimeViewModel(
        private val timeRepository: TimeRepository
) : ViewModel() {

    val isLoaded: LiveData<Boolean> = liveData {
        val result : Boolean  = timeRepository.loadTimeTable()
        emit(result)
    }

    fun getGroups() : LiveData<List<String>> {
        return timeRepository.getGroups()
    }

    fun getLessons(group: String, isOdds: Array<Boolean?>) : LiveData<Map<Int, List<Lesson>>> {
        return liveData {
            val lessons: Map<Int, List<Lesson>> = timeRepository.getLessons(group, isOdds)
            emit(lessons)
        }
    }

    fun getPdfHeaders() : Set<String> = timeRepository.getPdfHeaders()

    fun saveGroup(group: String) {
        return timeRepository.saveGroup(group)
    }

    fun getSavedGroup() : String {
        return timeRepository.getSavedGroup()
    }

}