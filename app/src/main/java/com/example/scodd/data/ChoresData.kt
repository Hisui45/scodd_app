package com.example.scodd.data

import com.example.scodd.model.Chore
import com.example.scodd.model.Room

val Room1 = Room("1RS","Kitchen")
val Room2 = Room("2RS","Bedroom")
val Room3 = Room("3RS","Living Room")
val Room4 = Room("4RS","Bathroom")
val Room5 = Room("5RS","Personal")
val Room6 = Room("6RS","Home Office")
val Room7 = Room("7RS","Favorites")

val Chore1 = Chore(
    title = "Wash Dishes and Clean Stove and Wipe Counter",
//    roomId = "1RS",
    timerModeValue = 8,
    bankModeValue = 10,
    isFavorite = false,
    id = "1S"
)

val Chore2 = Chore(
    title = "Make Bed",
//    roomId = "2RS",
    timerModeValue = 2,
    bankModeValue = 1,
    isFavorite = true,
    id = "2S"
)

val Chore3 = Chore(
    title = "Wash Clothes",
//    roomId = "3RS",
    timerModeValue = 5,
    bankModeValue = 5,
    isFavorite = true,
//    isCompleted = false,
    id = "3S"
)

val Chore4 = Chore(
    title = "Vacuum",
//    roomId = "4RS",
    timerModeValue = 12,
    bankModeValue = 5,
    isFavorite = false,
//    isCompleted = false,
    id = "4S"
)

val Chore5 = Chore(
    title = "Clean Toilet",
//    roomId = "5RS",
    timerModeValue = 4,
    bankModeValue = 8,
    isFavorite = false,
//    isCompleted = false,
    id = "5S"
)

val Chore6 = Chore(
    title = "Brush Teeth",
//    roomId = "6RS",
    timerModeValue = 3,
    bankModeValue = 2,
    isFavorite = false,
//    isCompleted = false,
    id = "6S"
)

val Chore7 = Chore(
    title = "Shred Papers",
//    roomId = "7RS",
    timerModeValue = 15,
    bankModeValue = 6,
    isFavorite = false,
//    isCompleted = false,
    id = "7S"
)

val scoddChores = listOf(Chore1,Chore2, Chore3, Chore4, Chore5, Chore6, Chore7)

val scoddRooms = listOf(Room1, Room2, Room3, Room4, Room5, Room6, Room7)

