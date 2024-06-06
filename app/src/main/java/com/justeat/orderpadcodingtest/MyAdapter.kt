package com.justeat.orderpadcodingtest

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import okhttp3.HttpUrl.Companion.toHttpUrl


class MyAdapter(var list: MutableList<Product>, val context: Context) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    fun addList(productList: List<Product>) {
        list = productList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return MyViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class MyViewHolder(val view: View, val context: Context) : ViewHolder(view) {
        fun bind(product: Product) {
            val title = view.findViewById<TextView>(R.id.product_title)
            val subTitle = view.findViewById<TextView>(R.id.product_subtitle)
            val thumbnail = view.findViewById<ImageView>(R.id.product_thumbnail)


            title.text = product.title
            subTitle.text = product.category!!
            Thread {
                val image = BitmapFactory.decodeStream(
                    product.thumbnail!!.toHttpUrl().toUrl().openConnection().getInputStream()
                )
                (context as AppCompatActivity).runOnUiThread {
                    thumbnail.setImageBitmap(image)
                }
            }.start()
        }
    }
}
