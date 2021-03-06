package com.example.biocubicar.biocubicar.Adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.biocubicar.biocubicar.R
import com.squareup.picasso.Picasso

class SlideAdapter : PagerAdapter {

    var context : Context
    var images: ArrayList<String>
    lateinit var inflater : LayoutInflater

    constructor(context: Context, images: ArrayList<String>) : super() {
        this.context = context
        this.images = images
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object` as View

    override fun getCount(): Int = images.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var image : ImageView
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view : View = inflater.inflate(R.layout.slider_image_item, container, false)
        image = view.findViewById(R.id.slider_image)
        Picasso.with(context)
                .load(images[position])
                .into(image)
        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}