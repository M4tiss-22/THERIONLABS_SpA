package com.example.therionlabs_spa.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LeadEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun leadDao(): LeadDao

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun obtenerInstancia(contexto: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                val nuevaInstancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDatabase::class.java,
                    "therionlabs_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCIA = nuevaInstancia
                nuevaInstancia
            }
        }
    }
}