package com.example.biocubicar.biocubicar.Adapter

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.biocubicar.biocubicar.Listener.RecyclerBiopilaListener
import com.example.biocubicar.biocubicar.R
import com.example.biocubicar.biocubicar.inflate
import com.example.biocubicar.biocubicar.model.BiopilaModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_biopila.view.*
import java.io.File

class listAdapter(private val myDir: File, private val biopilas: List<BiopilaModel>, private val listener: RecyclerBiopilaListener)
    : RecyclerView.Adapter<listAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.row_biopila))

    override fun getItemCount() = biopilas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(myDir, biopilas[position], listener)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(myDir: File, biopila: BiopilaModel, listener: RecyclerBiopilaListener) = with(itemView) {

            var nameImage = "Image-BC-00" + biopila.id + "-0.jpg"
            var file = File(myDir, nameImage)

            row_title.text = biopila.title
            row_description.text = biopila.description
            Picasso.with(context)
                    .load(Uri.fromFile(file).toString())
                    .into(row_image)
            setOnClickListener { listener.onClick(biopila, adapterPosition) }
            btDeleteRow.setOnClickListener { listener.onDelete(biopila, adapterPosition) }
        }
    }
}
