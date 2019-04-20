package com.example.biocubicar.biocubicar.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.biocubicar.biocubicar.Adapter.listAdapter
import com.example.biocubicar.biocubicar.Listener.RecyclerBiopilaListener
import com.example.biocubicar.biocubicar.R
import com.example.biocubicar.biocubicar.helperDB.BiopilaDao
import com.example.biocubicar.biocubicar.helperDB.database
import com.example.biocubicar.biocubicar.model.BiopilaModel
import kotlinx.android.synthetic.main.activity_listar_biopila.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import java.io.File

class ListarBiopilaActivity : AppCompatActivity() {

    private val dao = BiopilaDao()
    private var biopilaList = ArrayList<BiopilaModel>()
    private lateinit var adapter: listAdapter
    private val layoutManager by lazy { LinearLayoutManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_biopila)
        loadBiopilaList()
        setRecyclerView()
        initToolbar()
        delegate()
    }

    override fun onRestart() {
        super.onRestart()
        loadBiopilaList()
        setRecyclerView()
    }

    fun initToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar_listar_biopila))
        //home navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_nav_back)
        supportActionBar?.title = "Lista de biopilas"
    }

    fun delegate() {
        toolbar_listar_biopila.setNavigationOnClickListener { View -> onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add -> {
            initViewBiopila(-1)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    fun loadBiopilaList() {
        database.use {
            biopilaList.clear()
            var result = select("biopila")
            var data = result.parseList(classParser<BiopilaModel>())
            biopilaList.addAll(data)
        }
    }

    private fun setRecyclerView() {
        if (biopilaList.size > 0) {
            rvListOfBiopila.setHasFixedSize(true)
            rvListOfBiopila.itemAnimator = DefaultItemAnimator()
            rvListOfBiopila.layoutManager = layoutManager

        } else {
            Toast.makeText(this, "No hay biopilas cargadas para mostrar", Toast.LENGTH_LONG).show()
        }
        val myDir = getExternalFilesDir("biopilas")
        adapter = (listAdapter(myDir, biopilaList, object : RecyclerBiopilaListener {
            override fun onClick(biopila: BiopilaModel, position: Int) {
                initViewBiopila(biopila.id)
            }

            override fun onDelete(biopila: BiopilaModel, position: Int) {
                deleteBiopila(biopila.id, position)
            }
        }))
        rvListOfBiopila.adapter = adapter

    }

    private fun initViewBiopila(id_biopila: Int) {
        val intent = Intent(this, BiopilaActivity::class.java)
        intent.putExtra("ID_BIOPILA", id_biopila)
        startActivity(intent)
    }

    private fun deleteBiopila(id_biopila: Int, position: Int) {
        biopilaList.removeAt(position)
        dao.deleteDao(id_biopila, this)
        deleteImages(id_biopila)
        setRecyclerView()
    }

    fun deleteImages(id_biopila: Int) {
        var index = 0
        val myDir = getExternalFilesDir("biopilas")
        var nameImage = "Image-BC-00" + id_biopila + "-" + index + ".jpg"
        var file = File(myDir, nameImage)

        while (file.exists()) {
            file.delete()
            index++
            nameImage = "Image-BC-00" + id_biopila + "-" + index + ".jpg"
            file = File(myDir, nameImage)
        }
    }
}
