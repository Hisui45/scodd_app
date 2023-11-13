package com.example.scodd.data.source

import androidx.room.TypeConverter
import com.example.scodd.model.ChoreItem
import com.example.scodd.model.Room
import com.example.scodd.model.RoutineInfo
import com.example.scodd.model.ScoddTime
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun roomToString(room: List<Room>): String = Gson().toJson(room)

    @TypeConverter
    fun stringToRoom(string: String) = Gson().fromJson(string, Array<Room>::class.java).toList()

    @TypeConverter
    fun routineInfoToString(routineInfo: RoutineInfo): String = Gson().toJson(routineInfo)

    @TypeConverter
    fun stringToRoutineInfo(string: String) = Gson().fromJson(string, RoutineInfo::class.java)

    @TypeConverter
    fun scoddTimeToString(scoddTime: ScoddTime): String = Gson().toJson(scoddTime)

    @TypeConverter
    fun stringToScoddTime(string: String) = Gson().fromJson(string, ScoddTime::class.java)

    @TypeConverter
    fun choreItemsToString(choreItem: List<ChoreItem>): String = Gson().toJson(choreItem)

    @TypeConverter
    fun stringToChoreItems(string: String) = Gson().fromJson(string, Array<ChoreItem>::class.java).toList()

}