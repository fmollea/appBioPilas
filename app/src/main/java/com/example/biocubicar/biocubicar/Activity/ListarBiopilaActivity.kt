package com.example.biocubicar.biocubicar.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.example.biocubicar.biocubicar.Adapter.listAdapter
import com.example.biocubicar.biocubicar.Listener.RecyclerBiopilaListener
import com.example.biocubicar.biocubicar.R
import com.example.biocubicar.biocubicar.helperDB.database
import com.example.biocubicar.biocubicar.model.BiopilaModel
import kotlinx.android.synthetic.main.activity_listar_biopila.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class ListarBiopilaActivity : AppCompatActivity() {

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

    fun loadBiopilaList() {
        database.use {
            biopilaList.clear()
            var result = select("biopila")
            var data = result.parseList(classParser<BiopilaModel>())
            biopilaList.addAll(data)
        }
    }

    private fun setRecyclerView() {
        rvListOfBiopila.setHasFixedSize(true)
        rvListOfBiopila.itemAnimator = DefaultItemAnimator()
        rvListOfBiopila.layoutManager = layoutManager
        adapter = (listAdapter(biopilaList, object : RecyclerBiopilaListener {
            override fun onClick(biopila: BiopilaModel, position: Int) {

            }
        }))
        rvListOfBiopila.adapter = adapter
    }
}
