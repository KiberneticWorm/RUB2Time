package ru.rubt.rubttimetable.viewmodel

interface Factory<T> {
    fun create(): T;
}