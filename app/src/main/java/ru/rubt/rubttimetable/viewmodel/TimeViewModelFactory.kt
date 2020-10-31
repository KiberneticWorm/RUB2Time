package ru.rubt.rubttimetable.viewmodel

class TimeViewModelFactory(private val timeRepository: TimeRepository) : Factory<TimeViewModel> {
    override fun create(): TimeViewModel {
        return TimeViewModel(timeRepository);
    }
}