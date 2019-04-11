package com.example.biocubicar.biocubicar.helperDB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DBHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "biopiladb") {

    companion object {
        private var instance: DBHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DBHelper {
            if (instance == null) {
                instance = DBHelper(ctx.applicationContext)
            }
            return instance as DBHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable("biopila", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "title" to TEXT,
                "description" to TEXT,
                "latitude" to REAL,
                "longitude" to REAL,
                "volume" to REAL
        )

        db?.createTable("biopilaImage", true,
                "id" to INTEGER + PRIMARY_KEY,
                "id_biopila" to INTEGER,
                "name_image" to TEXT,
                FOREIGN_KEY("id_biopila", "biopila", "id"))

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}

// Access property for Context
val Context.database: DBHelper
    get() = DBHelper.getInstance(applicationContext)

