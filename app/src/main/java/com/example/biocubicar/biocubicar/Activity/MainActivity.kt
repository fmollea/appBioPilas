package com.example.biocubicar.biocubicar.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.biocubicar.biocubicar.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        delegate()
    }

    fun delegate() {
        btnCargarBiopila.setOnClickListener {
            initCargarBiopilaView()
        }

        btnListarBiopila.setOnClickListener {
            initListarBiopilaView()
        }
    }

    fun initCargarBiopilaView() {
        val intent = Intent(this, BiopilaActivity::class.java)
        intent.putExtra("ID_BIOPILA", -1)
        startActivity(intent)
    }

    fun initListarBiopilaView() {
        val intent = Intent(this, ListarBiopilaActivity::class.java)
        startActivity(intent)
    }


}
