package com.example.scodd.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.scodd.data.*
import com.example.scodd.data.source.local.*
import com.example.scodd.data.source.network.ChoreNetworkDataSource
import com.example.scodd.data.source.network.NetworkDataSource
import com.example.scodd.model.*
import com.example.scodd.utils.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.DayOfWeek
import java.time.Instant
import java.util.concurrent.Executors
import javax.inject.Singleton
import kotlin.random.Random


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindChoreRepository(repository: DefaultChoreRepository): ChoreRepository
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
        )
            .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {

                Executors.newSingleThreadScheduledExecutor()
                    .execute {
                        provideDataBase(context).choreDao().insertAllWorkflows(
                            listOf(LocalWorkflow(ROUNDUP, "Today's Roundup", isCheckList = true, RoutineInfo() ))

                                )
                    }

                Executors.newSingleThreadScheduledExecutor()
                    .execute {
                        provideDataBase(context).choreDao().insertAllRooms(rooms)
                    }

                Executors.newSingleThreadScheduledExecutor()
                    .execute {
                        provideDataBase(context).choreDao().insertAllModes(listOf(
                            LocalMode(ScoddMode.TimeMode.modeId, listOf(), listOf(), listOf(), listOf()),
                            LocalMode(ScoddMode.SpinMode.modeId, listOf(), listOf(), listOf(), listOf()),
                            LocalMode(ScoddMode.BankMode.modeId, listOf(), listOf(), listOf(),listOf()),
                            LocalMode(ScoddMode.QuestMode.modeId, listOf(), listOf(), listOf(), listOf()),
                            LocalMode(ScoddMode.SandMode.modeId, listOf(),listOf(), listOf(), listOf("1", "", "", "0"))  // hours, minutes, seconds, true == -, false == 0
                        )
                        )
                    }

                /**
                 * Test Data
                 */
//                                Executors.newSingleThreadScheduledExecutor()
//                    .execute {
//                        provideDataBase(context).choreDao().insertAllWorkflows(dummyWorkflows +
//                                        LocalWorkflow(ROUNDUP, "Today's Roundup", isCheckList = true, RoutineInfo() ))
//                    }
//
//                Executors.newSingleThreadScheduledExecutor()
//                    .execute {
//                        provideDataBase(context).choreDao().insertAllChoreItems(dummyLocalChoreItems)
//                    }
////
//

//
//                Executors.newSingleThreadScheduledExecutor()
//                    .execute {
//                        provideDataBase(context).choreDao().insertAllChores(dummyLocalChores)
//                    }


            }
        }
            ).build()
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
            val rooms = listOf("1RS") // Replace with actual rooms
            val routineInfo = RoutineInfo(
                date = Instant.now().toEpochMilli(),
                hour = i % 23, // Hours (0 to 23)
                minute = i % 59, // Minutes (0 to 59)
                frequencyValue = i % 7 + 1, // Frequency value (1 to 7)
                isOneTime = i % 2 == 0, // Every other chore is one-time
                scheduleType = ScoddTime.values().random(),
                frequencyOption = ScoddTime.values().random(),
                weeklyDay = DayOfWeek.values().random()
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
    fun generateRandomDate(startMillis: Long, endMillis: Long): Long {
        return Random.nextLong(startMillis, endMillis)
    }

    fun generateRandomHour(): Int {
        return Random.nextInt(0, 24)
    }

    fun generateRandomMinute(): Int {
        return Random.nextInt(0, 60)
    }

    val rooms = listOf(
        LocalRoom("1RS","Kitchen", false),
        LocalRoom("2RS", "Bedroom", false),
        LocalRoom("3RS", "Living Room", false),
        LocalRoom("4RS", "Bathroom", false),
        LocalRoom("5RS", "Personal", false),
        LocalRoom("6RS", "Home Office", false)
    )

    val dummyLocalChores = listOf(
        LocalChore("1", "Wash Dishes", listOf("1RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), false, ScoddTime.DAILY, 4, ScoddTime.WEEK, DayOfWeek.FRIDAY), false, 5, ScoddTime.MINUTE, false, 2, true),
        LocalChore("2", "Wipe Counter", listOf("1RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), true, ScoddTime.WEEKLY, 2, ScoddTime.WEEK, DayOfWeek.SATURDAY), true, 10, ScoddTime.HOUR, true, 3, false),
        LocalChore("3", "Vacuum Living Room", listOf("3RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), false, ScoddTime.MONTHLY, 1, ScoddTime.MONTH, DayOfWeek.SUNDAY), false, 15, ScoddTime.MINUTE, false, 1, true),
        LocalChore("4", "Dust Furniture", listOf("3RS","2RS","6RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), true, ScoddTime.YEARLY, 1, ScoddTime.YEAR, DayOfWeek.MONDAY), true, 8, ScoddTime.HOUR, false, 2, true),
        LocalChore("5", "Sweep Porch", listOf(), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), false, ScoddTime.CUSTOM, 2, ScoddTime.DAY, DayOfWeek.FRIDAY), false, 6, ScoddTime.MINUTE, true, 2, false),
        LocalChore("6", "Mop Floors", listOf("1RS","4RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), true, ScoddTime.DAILY, 1, ScoddTime.DAY, DayOfWeek.MONDAY), true, 7, ScoddTime.MINUTE, false, 3, true),
        LocalChore("7", "Make Bed", listOf("2RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), false, ScoddTime.WEEKLY, 2, ScoddTime.WEEK, DayOfWeek.WEDNESDAY), false, 2, ScoddTime.MINUTE, true, 2, false),
        LocalChore("8", "Wash Clothes", listOf("2RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), true, ScoddTime.MONTHLY, 1, ScoddTime.MONTH, DayOfWeek.SATURDAY), true, 5, ScoddTime.HOUR, false, 5, false),
        LocalChore("9", "Clean Toilet", listOf("4RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), false, ScoddTime.YEARLY, 1, ScoddTime.YEAR, DayOfWeek.MONDAY), false, 8, ScoddTime.MINUTE, true, 4, true),
        LocalChore("10", "Brush Teeth", listOf("5RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), true, ScoddTime.DAILY, 3, ScoddTime.WEEK, DayOfWeek.SUNDAY), true, 2, ScoddTime.MINUTE, false, 2, true),
        LocalChore("11", "Shred Papers", listOf("6RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), false, ScoddTime.CUSTOM, 2, ScoddTime.DAY, DayOfWeek.FRIDAY), false, 6, ScoddTime.MINUTE, true, 2, false),
        LocalChore("12", "Organize Closet", listOf("2RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), true, ScoddTime.DAILY, 1, ScoddTime.DAY, DayOfWeek.MONDAY), true, 10, ScoddTime.MINUTE, false, 3, true),
        LocalChore("13", "Water Plants", listOf("6RS","5RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), false, ScoddTime.WEEKLY, 2, ScoddTime.WEEK, DayOfWeek.WEDNESDAY), false, 4, ScoddTime.MINUTE, true, 2, false),
        LocalChore("14", "Empty Trash", listOf("1RS","2RS","3RS","4RS","6RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), true, ScoddTime.MONTHLY, 1, ScoddTime.MONTH, DayOfWeek.SATURDAY), true, 3, ScoddTime.MINUTE, false, 5, false),
        LocalChore("15", "Eat Breakfast", listOf("1RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), false, ScoddTime.DAILY, 3, ScoddTime.WEEK, DayOfWeek.SUNDAY), true, 2, ScoddTime.MINUTE, false, 2, true),
        LocalChore("16", "Fold Laundry", listOf("2RS"), RoutineInfo(Instant.now().toEpochMilli(), generateRandomHour(), generateRandomMinute(), true, ScoddTime.MONTHLY, 1, ScoddTime.MONTH, DayOfWeek.SATURDAY), true, 5, ScoddTime.HOUR, false, 5, false),
        )

    val dummyLocalChoreItems = listOf(
        LocalChoreItem("1", "7" ,"1", false),
        LocalChoreItem("2", "10","2", false),
        LocalChoreItem("3", "15", "1", false),
        LocalChoreItem("4", "8", "2", false),
        LocalChoreItem("5",  "16","2", false),
        LocalChoreItem("6", "3","3", false),
        LocalChoreItem("7",  "4","3", false),
        LocalChoreItem("8",  "2","3", false),
        LocalChoreItem("9",  "9","4", false),
        LocalChoreItem("10",  "2","4", false),
        LocalChoreItem("11",  "11","5", false),
        LocalChoreItem("12",  "12","5", false),

    )

    val dummyWorkflows = listOf(
        LocalWorkflow(
            id = "1",
            title = "Morning Routine",
            false,
            routineInfo = RoutineInfo(
                date = generateRandomDate(System.currentTimeMillis(), System.currentTimeMillis() + 2592000000),
                hour = generateRandomHour(),
                minute = generateRandomMinute(),
                isOneTime = true,
                scheduleType = ScoddTime.DAILY,
                frequencyValue = 1,
                frequencyOption = ScoddTime.DAY,
                weeklyDay = DayOfWeek.SUNDAY
            )
        ),
        LocalWorkflow(
            id = "2",
            title = "Laundry Day"
            ,false,
            routineInfo = RoutineInfo(
                date = generateRandomDate(System.currentTimeMillis(), System.currentTimeMillis() + 2592000000),
                hour = generateRandomHour(),
                minute = generateRandomMinute(),
                isOneTime = true,
                scheduleType = ScoddTime.WEEKLY,
                frequencyValue = 1,
                frequencyOption = ScoddTime.WEEK,
                weeklyDay = DayOfWeek.MONDAY
            )
        ),
        LocalWorkflow(
            id = "3",
            title = "Cleaning Day",
            true,
            routineInfo = RoutineInfo(
                date = generateRandomDate(System.currentTimeMillis(), System.currentTimeMillis() + 2592000000),
                hour = generateRandomHour(),
                minute = generateRandomMinute(),
                isOneTime = true,
                scheduleType = ScoddTime.MONTHLY,
                frequencyValue = 1,
                frequencyOption = ScoddTime.MONTH,
                weeklyDay = DayOfWeek.WEDNESDAY
            )
        ),
        LocalWorkflow(
            id = "4",
            title = "Weekly Chores"
            ,false,
            routineInfo = RoutineInfo(
                date = generateRandomDate(System.currentTimeMillis(), System.currentTimeMillis() + 2592000000),
                hour = generateRandomHour(),
                minute = generateRandomMinute(),
                isOneTime = true,
                scheduleType = ScoddTime.WEEKLY,
                frequencyValue = 1,
                frequencyOption = ScoddTime.WEEK,
                weeklyDay = DayOfWeek.FRIDAY
            )
        ),
        LocalWorkflow(
            id = "5",
            title = "Night Routine"
            ,false,
            routineInfo = RoutineInfo(
                date = generateRandomDate(System.currentTimeMillis(), System.currentTimeMillis() + 2592000000),
                hour = generateRandomHour(),
                minute = generateRandomMinute(),
                isOneTime = true,
                scheduleType = ScoddTime.DAILY,
                frequencyValue = 1,
                frequencyOption = ScoddTime.DAY,
                weeklyDay = DayOfWeek.SATURDAY
            )
        )
    )


}