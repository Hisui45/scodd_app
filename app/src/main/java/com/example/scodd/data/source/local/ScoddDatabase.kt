package com.example.scodd.data.source.local


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.scodd.data.source.Converters

/**
 * The Room Database that contains the Chore table.
 *
 * Note that exportSchema should be true in production databases.
 */

@Database(entities = [LocalChore::class,LocalRoom::class,LocalWorkflow::class,LocalChoreItem::class, LocalMode::class], version = 1, exportSchema = false)

@TypeConverters(Converters::class)
abstract class ScoddDatabase : RoomDatabase() {
    abstract fun choreDao(): ChoreDao

}