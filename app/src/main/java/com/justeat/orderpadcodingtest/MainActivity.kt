package com.justeat.orderpadcodingtest

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var errorTextView: TextView
    lateinit var refreshButton: Button

    var myAdapter = MyAdapter(mutableListOf(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = myAdapter

        swipeRefreshLayout = findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener { fetchItems() }
        swipeRefreshLayout.isRefreshing = true

        errorTextView = findViewById(R.id.error_text)

        refreshButton = findViewById(R.id.refresh_button)
        refreshButton.setOnClickListener { fetchItems() }

        fetchItems()
    }

    // URLS
    private val urlSearchMobile = "https://dummyjson.com/products/?search=Mobile"
    private val urlSearchLaptop = "https://dummyjson.com/products/?search=Laptop"

    // API key
    private val apiKey by lazy { getString(R.string.api_key) }

    fun fetchItems() {
        errorTextView.isVisible = false
        swipeRefreshLayout.isRefreshing = true

        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url(urlSearchMobile)
            .addHeader("API_KEY", apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    errorTextView.isVisible = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()

                val productList =
                    gson.fromJson(response.body!!.string(), ProductResponse::class.java)

                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    errorTextView.isVisible = false
                    recyclerView.adapter =
                        MyAdapter(productList.products.toMutableList(), this@MainActivity)
                }
            }
        })
    }
}
