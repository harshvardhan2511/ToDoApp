package com.example.to_dolist

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class toDoList(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var date: Date,
    var title: String
)