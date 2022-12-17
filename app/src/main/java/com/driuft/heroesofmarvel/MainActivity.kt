package com.driuft.heroesofmarvel

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var _heroes: MutableList<Hero>
    private lateinit var _heroList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setIcon(R.drawable.marvel_logo)

        _heroes = mutableListOf()
        _heroList = findViewById(R.id.hero_list)

        getHeroes()
    }

    private fun getHeroes(id: String? = "") {
        val client = AsyncHttpClient()
        val timestamp = System.currentTimeMillis()
        val privateKey = "12209a24295f60074f7aab82c4a67e8b3bc4922d"
        val publicKey = "2362d3ce223101547b7d2ef09a955724"
        val hash = md5("$timestamp$privateKey$publicKey")
        val url = if (id != "") {
            "https://gateway.marvel.com/v1/public/characters/$id?ts=$timestamp&apikey=$publicKey&hash=$hash"
        } else {
            "https://gateway.marvel.com/v1/public/characters?ts=$timestamp&apikey=$publicKey&hash=$hash"
        }
        client[url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                val heroArray = json.jsonObject.getJSONObject("data").getJSONArray("results")

                for (i in 0 until heroArray.length()) {
                    val heroObject = heroArray.getJSONObject(i)
                    val heroImagePath = heroObject.getJSONObject("thumbnail").getString("path")
                    val heroImageExt = heroObject.getJSONObject("thumbnail").getString("extension")
                    val heroImage = "$heroImagePath.$heroImageExt"
                    val hero = Hero(
                        id = heroObject.getString("id"),
                        imageUrl = heroImage,
                        name = heroObject.getString("name"),
                        description = heroObject.getString("description")
                    )
                    _heroes.add(hero)
                }

                val adapter = HeroAdapter(_heroes)
                _heroList.adapter = adapter
                val layoutManager = GridLayoutManager(this@MainActivity, 2)
                layoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position % 3 == 0) 2 else 1
                    }
                }
                _heroList.layoutManager = layoutManager
                    //LinearLayoutManager(this@MainActivity)
                //_heroList.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Hero Error", errorResponse)
            }
        }]
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}