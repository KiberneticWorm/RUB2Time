package ru.rubt.rubttimetable.viewmodel

// Working with PDF files
// This structure is using to divide PDF files into 3 groups
enum class State {
    BEING, NEED_TO_DOWNLOAD, NEED_TO_DELETE
}