package com.justeat.orderpadcodingtest

import android.app.Activity
import android.content.Context
import androidx.core.view.isVisible
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

// URLS
private val urlSearchMobile = "https://dummyjson.com/products/?search=Mobile"
private val urlSearchLaptop = "https://dummyjson.com/products/?search=Laptop"



class Network {

	fun fetch(context: Context, successCallback: (ProductResponse)->Unit, failureCallback: ()->Unit) {

		CoroutineScope(Dispatchers.IO).launch {
//			val a = async{
//				netw.call1()
//			}
//			val b = async{
//				netw.call2()
//			}
//			val res = Result(a.await(), b.await())
//			val res = Result(a.await(), b.await())
		}
		// API key
		 val apiKey = "462b917d-b639-43cb-ae1d-069fe5fb0847"

		val client = OkHttpClient()

		val request: Request = Request.Builder()
			.url(urlSearchMobile)
			.addHeader("API_KEY", apiKey)
			.build()

		client.newCall(request).enqueue(object : Callback {
			override fun onFailure(call: Call, e: IOException) {
				(context as Activity).runOnUiThread{
					failureCallback()
				}
			}

			override fun onResponse(call: Call, response: Response) {
				val gson = Gson()

				val productList =
					gson.fromJson(response.body!!.string(), ProductResponse::class.java)

				(context as Activity).runOnUiThread {
					successCallback(productList)
				}
			}
		})
	}
}