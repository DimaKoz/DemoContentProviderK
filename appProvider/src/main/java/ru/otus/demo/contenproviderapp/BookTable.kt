package ru.otus.demo.contenproviderapp

import android.database.sqlite.SQLiteDatabase
import android.util.Log

object BookTable {
    // Database table
    const val TABLE_BOOK = "book"
    const val COLUMN_ID = "_id"
    const val COLUMN_NAME = "name"
    const val COLUMN_ISBN = "isbn"
    const val COLUMN_DESCRIPTION = "description"

    // Database creation SQL statement
    private const val DATABASE_CREATE = ("create table "
            + TABLE_BOOK + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_ISBN + " text not null,"
            + COLUMN_DESCRIPTION + " text not null" + ");")

    fun onCreate(database: SQLiteDatabase) {
        database.execSQL(DATABASE_CREATE)
    }

    fun onUpgrade(database: SQLiteDatabase, oldVersion: Int,
                  newVersion: Int) {
        Log.w(BookTable::class.java.name, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data")
        database.execSQL("DROP TABLE IF EXISTS $TABLE_BOOK")
        onCreate(database)
    }
}