package com.esmanureral.artframe

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ArtWorkViewModel by viewModels()
    private lateinit var adapter: ArtworkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    if (lastVisibleItem >= totalItemCount - 2) {
                        viewModel.fetchArtworks()
                    }
                }
            })
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ArtworkAdapter(mutableListOf()) { }
            recyclerView.adapter = adapter

        }
        viewModel.artworks.observe(this) { newItems ->
            adapter.addData(newItems)
        }
        viewModel.fetchArtworks()
    }
}
