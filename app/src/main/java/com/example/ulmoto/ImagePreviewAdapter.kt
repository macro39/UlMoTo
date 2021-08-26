package com.example.ulmoto

import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.base.PlutoViewHolder

class ImagePreviewAdapter(
    items: MutableList<String>
) : PlutoAdapter<String, ImagePreviewAdapter.ImagePreviewViewHolder>(items) {


    override fun getViewHolder(parent: ViewGroup, viewType: Int): ImagePreviewViewHolder {
        return ImagePreviewViewHolder(parent, R.layout.carousel_item)
    }

    class ImagePreviewViewHolder(parent: ViewGroup, itemLayoutId: Int) :
        PlutoViewHolder<String>(parent, itemLayoutId) {
        private var ivImage: ImageView = getView(R.id.ivCarousel)

        override fun set(item: String, position: Int) {
            Glide.with(context)
                .load(item)
                .placeholder(R.drawable.abc_vector_test)
                .into(ivImage)
        }
    }
}