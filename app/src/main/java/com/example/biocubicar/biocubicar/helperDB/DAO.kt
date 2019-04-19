package com.example.biocubicar.biocubicar.helperDB

import android.content.Context

interface DAO {

    fun insertDao(item: Any, context: Context): Boolean
    fun updateDao(id_item: Int, item: Any, context: Context): Boolean
    fun getAllDao(context: Context): ArrayList<Any>
    fun deleteDao(id_item: Int, context: Context): Boolean
    fun getDao(id_item: Int, context: Context): Any
}