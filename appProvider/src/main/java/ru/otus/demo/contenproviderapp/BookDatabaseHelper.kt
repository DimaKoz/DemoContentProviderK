package ru.otus.demo.contenproviderapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookDatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // Method is called during creation of the database
    override fun onCreate(database: SQLiteDatabase) {
        BookTable.onCreate(database)
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int,
                           newVersion: Int) {
        BookTable.onUpgrade(database, oldVersion, newVersion)
    }

    companion object {
        private const val DATABASE_NAME = "booktable.db"
        private const val DATABASE_VERSION = 1
    }
}