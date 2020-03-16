package ru.otus.demo.contenproviderapp

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import java.util.*

class BookContentProvider : ContentProvider() {
    private var database: BookDatabaseHelper? = null

    companion object {
        private const val AUTHORITY = "ru.otus.demo.contenproviderapp"
        private const val BASE_PATH = "books"
        @JvmField
        val CONTENT_URI = Uri.parse("content://" + AUTHORITY
                + "/" + BASE_PATH)

        // used for the UriMacher
        private const val BOOKS = 10
        private const val BOOK_ID = 20
        private val sURIMatcher = UriMatcher(
                UriMatcher.NO_MATCH)

        init {
            sURIMatcher.addURI(AUTHORITY, BASE_PATH, BOOKS)
            sURIMatcher.addURI(AUTHORITY, "$BASE_PATH/#", BOOK_ID)
        }
    }

    override fun onCreate(): Boolean {
        database = BookDatabaseHelper(context)
        return false
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {

        // Using SQLiteQueryBuilder instead of query() method
        val queryBuilder = SQLiteQueryBuilder()

        // check if the caller has requested a column which does not exists
        checkColumns(projection)

        // Set the table
        queryBuilder.tables = BookTable.TABLE_BOOK
        val uriType = sURIMatcher.match(uri)
        when (uriType) {
            BOOKS -> {
            }
            BOOK_ID ->                 // adding the ID to the original query
                queryBuilder.appendWhere(BookTable.COLUMN_ID + "="
                        + uri.lastPathSegment)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        val db = database!!.writableDatabase
        val cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder)
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = database!!.writableDatabase
        var id: Long = 0
        id = when (uriType) {
            BOOKS -> sqlDB.insert(BookTable.TABLE_BOOK, null, values)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return Uri.parse("$BASE_PATH/$id")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = database!!.writableDatabase
        var rowsDeleted = 0
        rowsDeleted = when (uriType) {
            BOOKS -> sqlDB.delete(BookTable.TABLE_BOOK, selection,
                    selectionArgs)
            BOOK_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    sqlDB.delete(
                            BookTable.TABLE_BOOK,
                            BookTable.COLUMN_ID + "=" + id,
                            null)
                } else {
                    sqlDB.delete(
                            BookTable.TABLE_BOOK,
                            BookTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = database!!.writableDatabase
        var rowsUpdated = 0
        rowsUpdated = when (uriType) {
            BOOKS -> sqlDB.update(BookTable.TABLE_BOOK,
                    values,
                    selection,
                    selectionArgs)
            BOOK_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    sqlDB.update(BookTable.TABLE_BOOK,
                            values,
                            BookTable.COLUMN_ID + "=" + id,
                            null)
                } else {
                    sqlDB.update(BookTable.TABLE_BOOK,
                            values,
                            BookTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }

    private fun checkColumns(projection: Array<String>?) {
        val available = arrayOf(BookTable.COLUMN_NAME,
                BookTable.COLUMN_ISBN, BookTable.COLUMN_DESCRIPTION,
                BookTable.COLUMN_ID)
        if (projection != null) {
            val requestedColumns = HashSet(
                    Arrays.asList(*projection))
            val availableColumns = HashSet(
                    Arrays.asList(*available))
            // check if all columns which are requested are available
            require(availableColumns.containsAll(requestedColumns)) { "Unknown columns in projection" }
        }
    }
}