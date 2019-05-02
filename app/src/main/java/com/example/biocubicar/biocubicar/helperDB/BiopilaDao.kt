package com.example.biocubicar.biocubicar.helperDB

import android.content.Context
import com.example.biocubicar.biocubicar.model.BiopilaModel
import org.jetbrains.anko.db.*
import java.util.*


class BiopilaDao : DaoInterface {

    override fun getDao(id_item: Int, context: Context): BiopilaModel {
        var result_dao = BiopilaModel(-1, "", "", -.1, -.1, -.1, -.1,-.1,-.1)
        try {
            context.database.use {
                var result = select("biopila")
                        .whereArgs("id = {id_bio}",
                                "id_bio" to id_item)
                var data = result.parseSingle(classParser<BiopilaModel>())
                result_dao = data
            }

            return result_dao
        } catch (ex: Exception) {
            return result_dao
        }
    }

    override fun insertDao(item: Any, context: Context): Boolean {
        try {
            context.database.use {
                insert("biopila",
                        "title" to (item as BiopilaModel).title,
                        "description" to (item).description,
                        "latitude" to (item).latitude,
                        "longitude" to (item).longitude,
                        "volume" to (item).volume,
                        "htp" to (item).htp,
                        "temperature" to (item).temperature,
                        "moisture" to (item).moisture)
            }
            return true

        } catch (ex: Exception) {
            return false
        }
    }

    override fun updateDao(id_item: Int, item: Any, context: Context): Boolean {
        try {
            context.database.use {
                update("biopila",
                        "title" to (item as BiopilaModel).title,
                        "description" to item.description,
                        "longitude" to item.longitude,
                        "latitude" to item.latitude,
                        "volume" to (item).volume,
                        "htp" to (item).htp,
                        "temperature" to (item).temperature,
                        "moisture" to (item).moisture)
                        .whereArgs("id = {id_bio}",
                                "id_bio" to id_item)
                        .exec()
            }
            return true
        } catch (ex: Exception) {
            return false
        }

    }

    fun saveVolumeDao(id_item: Int, volume: Double, context: Context): Boolean {
        try {
            context.database.use {
                update("biopila",
                        "volume" to volume)
                        .whereArgs("id = {id_bio}",
                                "id_bio" to id_item)
                        .exec()
            }
            return true
        } catch (ex: Exception) {
            return false
        }

    }

    fun getVolumeDao(id_item: Int, context: Context): String {
        var result_dao = "0"
        try {
            context.database.use {
                var result = select("biopila")
                        .whereArgs("id = {id_bio}",
                                "id_bio" to id_item)
                var data = result.parseSingle(classParser<BiopilaModel>())
                result_dao = data.volume.toString();
            }
            return result_dao
        } catch (ex: Exception) {
            return result_dao
        }
    }

    override fun getAllDao(context: Context): ArrayList<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteDao(id_item: Int, context: Context): Boolean {
        try {
            context.database.use {
                delete("biopila",
                        "id = {id_bio}",
                        "id_bio" to id_item)
            }
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    fun getIdDao(title: String, descr: String, context: Context): Int {
        var id_result = -1
        try {
            context.database.use {
                var result = select("Biopila")
                        .whereArgs("title = {titleP} and description = {descrP}",
                                "titleP" to title,
                                "descrP" to descr)
                var data = result.parseSingle(classParser<BiopilaModel>())
                id_result = data.id
            }
        } catch (ex: Exception) {
            id_result = -1
        }
        return id_result
    }
}