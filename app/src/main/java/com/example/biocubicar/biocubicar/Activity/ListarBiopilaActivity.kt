package com.example.biocubicar.biocubicar.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.biocubicar.biocubicar.R
import kotlinx.android.synthetic.main.activity_listar_biopila.*

class ListarBiopilaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_biopila)

        setSupportActionBar(findViewById(R.id.toolbar_listar_biopila))
        //home navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_nav_back)
        toolbar_listar_biopila.setNavigationOnClickListener { View -> onBackPressed() }
    }
}
