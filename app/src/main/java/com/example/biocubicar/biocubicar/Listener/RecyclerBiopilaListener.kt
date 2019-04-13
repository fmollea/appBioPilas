package com.example.biocubicar.biocubicar.Listener

import com.example.biocubicar.biocubicar.model.BiopilaModel

interface RecyclerBiopilaListener {
    fun onClick(biopila: BiopilaModel, position: Int)
}