package com.example.newsapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.newsapp.models.Favourite

private const val TAG = "FavouriteFragment"

class FavouriteFragment : Fragment() {

    private val news = arrayListOf<Favourite>()
    private val adapter = NewsAdapter(news)
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        newsRecyclerView = view.findViewById(R.id.news_recycler_view)
        newsRecyclerView.setHasFixedSize(true)
        newsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        newsRecyclerView.adapter = adapter

        return view
    }

    private fun doAPICall() {
        mainViewModel.getFavourites()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.favourites.observe(viewLifecycleOwner, { newsList ->
            news.clear()
            news.addAll(newsList)
            adapter.notifyDataSetChanged()
        })
        doAPICall()
    }

    private inner class NewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val category: TextView = itemView.findViewById(R.id.news_category)
        private val title: TextView = itemView.findViewById(R.id.news_title)
        private val image: ImageView = itemView.findViewById(R.id.news_image)
        private val favouriteButton: ImageView = itemView.findViewById(R.id.favouriteButton)

        init {
            favouriteButton.setImageResource(R.drawable.icon_delete)
        }

        fun bind(favourite: Favourite, position: Int) {
            title.text = favourite.title
            category.text = favourite.source.name

            Glide.with(this@FavouriteFragment)
                .asBitmap()
                .transform(CenterCrop(), RoundedCorners(20))
                .load(favourite.urlToImage)
                .into(image)

            favouriteButton.setOnClickListener {
                mainViewModel.removeFavourite(favourite)
                Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show()
                news.remove(favourite)
                adapter.notifyItemRemoved(position)
            }

            itemView.setOnLongClickListener {
                Log.d(TAG, "setOnLongClickListener: ")
                // Select multiple
                true // Consume Event don't propagate
            }
        }
    }

    private inner class NewsAdapter(private val newsList: List<Favourite>) :
        RecyclerView.Adapter<NewsHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
            return NewsHolder(layoutInflater.inflate(R.layout.news_list_item, parent, false))
        }

        override fun onBindViewHolder(holder: NewsHolder, position: Int) {
            holder.bind(newsList[position], position)
        }

        override fun getItemCount() = newsList.size
    }
}