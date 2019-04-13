package com.example.biocubicar.biocubicar.Adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.biocubicar.biocubicar.Listener.RecyclerBiopilaListener
import com.example.biocubicar.biocubicar.R
import com.example.biocubicar.biocubicar.inflate
import com.example.biocubicar.biocubicar.model.BiopilaModel
import kotlinx.android.synthetic.main.row_biopila.view.*

class listAdapter(private val biopilas: List<BiopilaModel>, private val listener: RecyclerBiopilaListener)
    : RecyclerView.Adapter<listAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.row_biopila))

    override fun getItemCount() = biopilas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(biopilas[position], listener)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(biopila: BiopilaModel, listener: RecyclerBiopilaListener) = with(itemView) {
            row_title.text = biopila.title
            row_description.text = biopila.description
            setOnClickListener { listener.onClick(biopila, adapterPosition) }
        }
    }
}
