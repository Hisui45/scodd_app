package com.example.scodd.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.scodd.data.*
import com.example.scodd.data.source.local.ChoreDao
import com.example.scodd.data.source.local.LocalChore
import com.example.scodd.data.source.local.LocalRoom
import com.example.scodd.data.source.local.ScoddDatabase
import com.example.scodd.data.source.network.ChoreNetworkDataSource
import com.example.scodd.data.source.network.NetworkDataSource
import com.example.scodd.model.RoutineInfo
import com.example.scodd.model.ScoddTime
import com.example.scodd.utils.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Instant
import java.util.concurrent.Executors
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindChoreRepository(repository: DefaultChoreRepository): ChoreRepository

    @Singleton
    @Binds
    abstract fun bindWorkflowRepository(repository: DefaultWorkflowRepository): WorkflowRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(dataSource: ChoreNetworkDataSource): NetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): ScoddDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ScoddDatabase::class.java,
            "Scodd.db"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                Executors.newSingleThreadScheduledExecutor()
                    .execute {
                        provideDataBase(context).choreDao().insertAllRooms(listOf(
                        LocalRoom("1RS","Kitchen", false),
                        LocalRoom("2RS", "Bedroom", false),
                        LocalRoom("3RS", "Living Room", false),
                        LocalRoom("4RS", "Bathroom", false),
                        LocalRoom("5RS", "Personal", false),
                        LocalRoom("6RS", "Home Office", false)
                    ))
                    }

                Executors.newSingleThreadScheduledExecutor()
                    .execute {
                        provideDataBase(context).choreDao().insertAllChores(listOf(
                            LocalChore("1", "Wash Dishes", listOf(Room1), RoutineInfo(Instant.now().toEpochMilli(), 3, 8, false, ScoddTime.DAILY, 4, ScoddTime.WEEK, ScoddTime.FRIDAY), false, 5, ScoddTime.MINUTE, false, 2, true),
                            LocalChore("2", "Clean Counter", listOf(Room2), RoutineInfo(Instant.now().toEpochMilli(), 2, 4, true, ScoddTime.WEEKLY, 2, ScoddTime.WEEK, ScoddTime.SATURDAY), true, 10, ScoddTime.HOUR, true, 3, false),
                            LocalChore("3", "Vacuum", listOf(Room3), RoutineInfo(Instant.now().toEpochMilli(), 1, 2, false, ScoddTime.MONTHLY, 1, ScoddTime.MONTH, ScoddTime.SUNDAY), false, 15, ScoddTime.MINUTE, false, 1, true),
                            LocalChore("4", "Dust Furniture", listOf(Room1), RoutineInfo(Instant.now().toEpochMilli(), 4, 6, true, ScoddTime.YEARLY, 1, ScoddTime.YEAR, ScoddTime.MONDAY), true, 8, ScoddTime.HOUR, false, 2, true),
                            LocalChore("5", "Sweep Porch", listOf(Room2), RoutineInfo(Instant.now().toEpochMilli(), 5, 3, false, ScoddTime.CUSTOM, 2, ScoddTime.DAY, ScoddTime.FRIDAY), false, 6, ScoddTime.MINUTE, true, 2, false),
                            LocalChore("6", "Mop Floors", listOf(Room3), RoutineInfo(Instant.now().toEpochMilli(), 3, 5, true, ScoddTime.DAILY, 1, ScoddTime.DAY, ScoddTime.MONDAY), true, 7, ScoddTime.MINUTE, false, 3, true),
                            LocalChore("7", "Make Bed", listOf(Room4), RoutineInfo(Instant.now().toEpochMilli(), 6, 7, false, ScoddTime.WEEKLY, 2, ScoddTime.WEEK, ScoddTime.WEDNESDAY), false, 2, ScoddTime.MINUTE, true, 2, false),
                            LocalChore("8", "Wash Clothes", listOf(Room1), RoutineInfo(Instant.now().toEpochMilli(), 2, 4, true, ScoddTime.MONTHLY, 1, ScoddTime.MONTH, ScoddTime.SATURDAY), true, 5, ScoddTime.HOUR, false, 5, false),
                            LocalChore("9", "Clean Toilet", listOf(Room2), RoutineInfo(Instant.now().toEpochMilli(), 1, 2, false, ScoddTime.YEARLY, 1, ScoddTime.YEAR, ScoddTime.MONDAY), false, 8, ScoddTime.MINUTE, true, 4, true),
                            LocalChore("10", "Brush Teeth", listOf(Room3), RoutineInfo(Instant.now().toEpochMilli(), 5, 8, true, ScoddTime.DAILY, 3, ScoddTime.WEEK, ScoddTime.SUNDAY), true, 2, ScoddTime.MINUTE, false, 2, true),
                            LocalChore("11", "Shred Papers", listOf(Room4), RoutineInfo(Instant.now().toEpochMilli(), 4, 6, false, ScoddTime.CUSTOM, 2, ScoddTime.DAY, ScoddTime.FRIDAY), false, 6, ScoddTime.MINUTE, true, 2, false),
                            LocalChore("12", "Organize Closet", listOf(Room1), RoutineInfo(Instant.now().toEpochMilli(), 3, 5, true, ScoddTime.DAILY, 1, ScoddTime.DAY, ScoddTime.MONDAY), true, 10, ScoddTime.MINUTE, false, 3, true),
                            LocalChore("13", "Water Plants", listOf(Room2), RoutineInfo(Instant.now().toEpochMilli(), 6, 7, false, ScoddTime.WEEKLY, 2, ScoddTime.WEEK, ScoddTime.WEDNESDAY), false, 4, ScoddTime.MINUTE, true, 2, false),
                            LocalChore("14", "Empty Trash", listOf(Room3), RoutineInfo(Instant.now().toEpochMilli(), 2, 4, true, ScoddTime.MONTHLY, 1, ScoddTime.MONTH, ScoddTime.SATURDAY), true, 3, ScoddTime.MINUTE, false, 5, false),
                        ))
                    }

            }
        }).build()
    }

//    private var dbCallback: RoomDatabase.Callback = object : RoomDatabase.Callback() {
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            Executors.newSingleThreadScheduledExecutor()
//                .execute({ provideDataBase(context = ).yourDAO().insertData(yourDataList) })
//        }
//    }

    @Provides
    fun provideChoreDao(database: ScoddDatabase): ChoreDao = database.choreDao()

    fun generateDummyData(): List<LocalChore> {
        val dummyData = mutableListOf<LocalChore>()
        val choreTitles = listOf(
            "Wash Dishes",
            "Clean Counter",
            "Vacuum",
            "Dust Furniture",
            "Sweep Porch",
            "Mop Floors",
            "Make Bed",
            "Wash Clothes",
            "Clean Toilet",
            "Brush Teeth",
            "Shred Papers",
            "Organize Closet",
            "Water Plants",
            "Empty Trash",
            "Clean Windows"
        )
        for (i in 1..15) {
            val id = i.toString()
            val title = choreTitles[i]
            val rooms = listOf(Room1) // Replace with actual rooms
            val routineInfo = RoutineInfo(
                date = Instant.now().toEpochMilli(),
                hour = i % 23, // Hours (0 to 23)
                minute = i % 59, // Minutes (0 to 59)
                frequencyValue = i % 7 + 1, // Frequency value (1 to 7)
                isOneTime = i % 2 == 0, // Every other chore is one-time
                scheduleType = ScoddTime.values().random(),
                frequencyOption = ScoddTime.values().random(),
                weeklyDay = ScoddTime.values().random()
            )
            val isFavorite = i % 2 == 0 // Every other chore is a favorite
            val timerValue = i % 10 + 1 // Timer value (1 to 10)
            val timeUnit = ScoddTime.values().random()
            val isScheduled = i % 2 == 0 // Every other chore is scheduled

            val localChore = LocalChore(id, title, rooms, routineInfo, isFavorite, timerValue, timeUnit, false, 2, isScheduled)
            dummyData.add(localChore)
        }

        return dummyData
    }


}