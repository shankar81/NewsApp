package com.example.newsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.newsapp.models.News

class FavouriteFragment : Fragment() {

    private val news = arrayListOf<News>()
    private val adapter = NewsAdapter(news)
    private lateinit var newsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        newsRecyclerView = view.findViewById(R.id.news_recycler_view)
        newsRecyclerView.setHasFixedSize(true)
        newsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        newsRecyclerView.adapter = adapter

        return view
    }


    private inner class NewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val category: TextView = itemView.findViewById(R.id.news_category)
        private val title: TextView = itemView.findViewById(R.id.news_title)
        private val image: ImageView = itemView.findViewById(R.id.news_image)

        fun bind(news: News) {
            title.text = news.title
            category.text = news.source.name

            Glide.with(this@FavouriteFragment)
                .asBitmap()
                .transform(CenterCrop(), RoundedCorners(20))
                .load(news.urlToImage)
                .into(image)
        }
    }

    private inner class NewsAdapter(private val newsList: List<News>) :
        RecyclerView.Adapter<NewsHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
            return NewsHolder(layoutInflater.inflate(R.layout.news_list_item, parent, false))
        }

        override fun onBindViewHolder(holder: NewsHolder, position: Int) {
            holder.bind(newsList[position])
        }

        override fun getItemCount() = newsList.size
    }
}