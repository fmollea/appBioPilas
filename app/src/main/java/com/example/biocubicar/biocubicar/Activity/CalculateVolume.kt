package com.example.biocubicar.biocubicar.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.biocubicar.biocubicar.R
import com.example.biocubicar.biocubicar.helperDB.BiopilaDao
import kotlinx.android.synthetic.main.activity_calculate_volume.*

class CalculateVolume : AppCompatActivity() {

    private var idBiopila: Int = -1
    private val dao = BiopilaDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_volume)
        initToolbar()
        initView()
        delegate()
    }

    fun initToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar_volume))
        //home navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_nav_back)
        supportActionBar?.title = "Calcular volumen"

    }

    fun initView() {
        val bundle = intent.extras
        idBiopila = bundle.getInt("ID_BIOPILA")
    }

    fun delegate() {
        toolbar_volume.setNavigationOnClickListener { View ->
            onBackPressed()
        }

        btCalcular.setOnClickListener {
            calculateVolume()
        }
    }

    private fun calculate(): Double {
        var h = java.lang.Double.parseDouble(etH.text.toString())
        var a1 = java.lang.Double.parseDouble(etA1.text.toString())
        var a2 = java.lang.Double.parseDouble(etA2.text.toString())
        var b1 = java.lang.Double.parseDouble(etB1.text.toString())
        var b2 = java.lang.Double.parseDouble(etB2.text.toString())

        return (h * (a1 * b1 + a2 * b2 + (a1 + a2) * (b1 + b2))) / 6
    }

    private fun saveResult(volume: Double): Boolean {
        return dao.saveVolumeDao(idBiopila, volume, this);
    }

    private fun valuesNullOrEmpty(): Boolean {
        return etH.text.isNullOrEmpty() || etA1.text.isNullOrEmpty() || etA2.text.isNullOrEmpty() || etB1.text.isNullOrEmpty() || etB2.text.isNullOrEmpty()
    }

    private fun calculateVolume() {
        if (valuesNullOrEmpty()) {
            Toast.makeText(this, "Debe completar todos los campos para calcular el volumen", Toast.LENGTH_LONG)
        } else {
            if (saveResult(calculate())) {
                finish()
            } else {
                Toast.makeText(this, "Ocurrió un error al calcular el volumen", Toast.LENGTH_SHORT)
            }
        }
    }
}
