package com.dean.pr_po

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dean.pr_po.TlogContract.TlogColumns.Companion.TABLE_NAME

internal class TlogHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tlogdb"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_NOTE = "CREATE TABLE $TABLE_NAME" +
                " (${TlogContract.TlogColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${TlogContract.TlogColumns.ID_APP} TEXT NOT NULL," +
                " ${TlogContract.TlogColumns.ID_USER} TEXT NOT NULL," +
                " ${TlogContract.TlogColumns.ID_CONN} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_NOTE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)

    }
}